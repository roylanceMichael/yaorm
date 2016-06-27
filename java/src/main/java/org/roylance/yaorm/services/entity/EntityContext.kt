package org.roylance.yaorm.services.entity

import com.google.protobuf.ByteString
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.utilities.CommonUtils
import org.roylance.yaorm.utilities.DefinitionModelComparisonUtil
import java.util.*

abstract class EntityContext(
    protected val entityServices:List<IEntityService<*>>,
    protected val migrationService:IEntityService<MigrationModel>,
    protected val contextName:String) {

    private val typeToAction = object: HashMap<
            String,
            (entityService: IEntityService<*>, difference: YaormModel.Difference) -> Boolean>() {
        init {
            put(YaormModel.Difference.Operation.CREATE.name + YaormModel.Difference.EntityType.COLUMN.name,
                    { service, model ->  createColumn(service, model) })
            put(YaormModel.Difference.Operation.CREATE.name + YaormModel.Difference.EntityType.INDEX.name,
                    { service, model ->  createIndex(service, model) })
            put(YaormModel.Difference.Operation.CREATE.name + YaormModel.Difference.EntityType.TABLE.name,
                    { service, model ->  createTable(service) })
            put(YaormModel.Difference.Operation.DROP.name + YaormModel.Difference.EntityType.COLUMN.name,
                    { service, model ->  dropColumn(service, model) })
            put(YaormModel.Difference.Operation.DROP.name + YaormModel.Difference.EntityType.INDEX.name,
                    { service, model ->  dropIndex(service, model) })
            put(YaormModel.Difference.Operation.DROP.name + YaormModel.Difference.EntityType.TABLE.name,
                    { service, model ->  dropTable(service) })
        }
    }

    init {
        this.migrationService.createTable()
        this.entityServices
            .forEach {
                it.entityContext = this
            }
    }

    fun clearAllCache() {
        this.migrationService.clearCache()
        this.entityServices
            .forEach { it.clearCache() }
    }

    fun handleMigrations(newId:String=UUID.randomUUID().toString()) {
        val differenceReport = this.getDifferenceReport()

        if (!differenceReport.migrationExists || differenceReport.migrationExists) {
            this.applyMigrations(differenceReport)
            this.createNewMigration(newId)
        }
    }

    fun getLatestMigrationDefinition() : YaormModel.TableDefinitions? {
        val propertyHolder = YaormModel.Column.newBuilder()
        propertyHolder.stringHolder = this.contextName
        val propertyDefinition = YaormModel.ColumnDefinition.newBuilder()
            .setName(MigrationModel.ContextName)
            .setType(YaormModel.ProtobufType.STRING)
            .setIsKey(false)
        propertyHolder.setDefinition(propertyDefinition)

        val whereClause = YaormModel.WhereClause.newBuilder()
                    .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                    .setNameAndProperty(propertyHolder)
                    .build()

        val migrations = this.migrationService.where(whereClause)

        if (!migrations.isEmpty()) {
            val lastMigration =  migrations
                    .sortedByDescending { it.insertDate }
                    .first()
            val byteString = ByteString.copyFromUtf8(lastMigration.modelDefinitionJson)
            return YaormModel.TableDefinitions.parseFrom(byteString)
        }

        return null
    }

    fun createNewMigration(id:String) {
        val definitionsModels = this.getDefinitions()
        definitionsModels.toByteString().toStringUtf8()

        val migrationModel = MigrationModel(
                id,
                this.contextName,
                definitionsModels.toByteString().toStringUtf8())

        this.migrationService.create(migrationModel)
    }

    fun getDifferenceReport() : YaormModel.DifferenceReport {
        val returnModels = ArrayList<YaormModel.Difference>()

        val latestMigration = this.getLatestMigrationDefinition()
        val currentModel = this.getDefinitions()

        currentModel
            .tableDefinitions
            .forEach { currentDefinition ->
                if (latestMigration == null) {
                    DefinitionModelComparisonUtil
                            .addDifferenceIfDifferent(
                                    currentDefinition.value,
                                    null,
                                    returnModels)
                }
                else {
                    val foundDefinition = latestMigration.tableDefinitions
                            .filter { currentDefinition.key.equals(it.key) }
                            .values
                            .firstOrNull()

                    // will look at children as well
                    DefinitionModelComparisonUtil
                            .addDifferenceIfDifferent(
                                    currentDefinition.value,
                                    foundDefinition,
                                    returnModels)
                }
            }

        return YaormModel.DifferenceReport.newBuilder()
                .addAllDifferences(returnModels)
                .setMigrationExists(latestMigration != null)
                .build()
    }

    fun applyMigrations(differenceReportModel: YaormModel.DifferenceReport) {
        if (!differenceReportModel.migrationExists) {
            this.entityServices
                .forEach {
                    it.createTable()
                }
            return
        }

        differenceReportModel
            .differencesList
            .forEach { differenceModel ->
                val foundEntityService = this.entityServices
                        .filter {
                            differenceModel.name.equals(it.entityDefinition.simpleName)
                        }
                        .firstOrNull() ?: return@forEach

                val key = "${differenceModel.operation}${differenceModel.entityType}"

                if (this.typeToAction.containsKey(key)) {
                    this.typeToAction[key]!!.invoke(foundEntityService, differenceModel)
                }
            }
    }

    fun getDefinitions():YaormModel.TableDefinitions {
        val definitions = YaormModel.TableDefinitions.newBuilder()

        this.entityServices.forEach {
            val propertyNames = it.entityDefinition
                    .methods
                    .filter { it.name.startsWith(CommonUtils.Set) }
                    .map { it.name.substring(CommonUtils.GetSetLength) }
                    .toHashSet()

            val newDefinition = YaormModel.TableDefinition.newBuilder()
                    .setName(it.entityDefinition.simpleName)
            if (it.indexDefinition != null) {
                newDefinition.index = it.indexDefinition
            }

            it.entityDefinition
                    .methods
                    .filter { it.name.startsWith(CommonUtils.Get) &&
                            propertyNames.contains(it.name.substring(CommonUtils.GetSetLength)) &&
                            !CommonUtils.JavaObjectName.equals(it.returnType.name) }
                    .forEach {
                        val name = CommonUtils.lowercaseFirstChar(
                                it.name.substring(CommonUtils.GetSetLength))
                        val property = YaormModel.ColumnDefinition.newBuilder()
                                .setName(name)
                                .setIsKey(it.name.equals(CommonUtils.IdName))
                        if (CommonUtils.JavaToProtoMap.containsKey(it.returnType)) {
                            property.type = CommonUtils.JavaToProtoMap[it.returnType]
                        }
                        else {
                            property.type = YaormModel.ProtobufType.STRING
                        }

                        newDefinition.mutableColumnDefinitions[name] = property.build()
                    }

            definitions.mutableTableDefinitions[newDefinition.name] = newDefinition.build()
        }

        return definitions.build()
    }

    fun <T: IEntity> getForeignService(entityType:Class<T>): IEntityService<T>? {
        val foundService = this.entityServices
                .filter { it.entityDefinition.equals(entityType) }
                .firstOrNull() ?: return null

        return foundService as IEntityService<T>
    }

    private fun createIndex(entityService: IEntityService<*>, differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.index != null) {
            return entityService.createIndex(differenceModel.index)
        }
        return false
    }

    private fun dropIndex(entityService: IEntityService<*>, differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.index != null) {
            return entityService.dropIndex(differenceModel.index)
        }
        return false
    }

    private fun createColumn(entityService: IEntityService<*>, differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.propertyDefinition != null) {
            return entityService.createColumn(differenceModel.propertyDefinition)
        }
        return false
    }

    private fun dropColumn(entityService: IEntityService<*>, differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.propertyDefinition != null) {
            return entityService.dropColumn(differenceModel.propertyDefinition)
        }
        return false
    }

    private fun createTable(entityService: IEntityService<*>):Boolean {
        return entityService.createTable()
    }

    private fun dropTable(entityService: IEntityService<*>):Boolean {
        return entityService.dropTable()
    }
}
