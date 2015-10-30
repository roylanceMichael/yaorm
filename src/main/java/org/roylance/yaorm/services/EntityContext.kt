package org.roylance.yaorm.services

import com.google.gson.Gson
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.models.migration.*
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.DefinitionModelComparisonUtil
import java.util.*

public abstract class EntityContext(
    protected val entityServices:List<IEntityService<*,*>>,
    protected val migrationService:IEntityService<Long, MigrationModel>,
    protected val contextName:String,
    private val gson:Gson = Gson()) {

    private val typeToAction = object: HashMap<
            String,
            (entityService: IEntityService<*, *>, differenceModel: DifferenceModel) -> Boolean>() {
        init {
            put(DifferenceModel.OperationCreate + DifferenceModel.EntityTypeColumn,
                    { service, model ->  createColumn(service, model) })
            put(DifferenceModel.OperationCreate + DifferenceModel.EntityTypeIndex,
                    { service, model ->  createIndex(service, model) })
            put(DifferenceModel.OperationCreate + DifferenceModel.EntityTypeTable,
                    { service, model ->  createTable(service) })
            put(DifferenceModel.OperationDrop + DifferenceModel.EntityTypeColumn,
                    { service, model ->  dropColumn(service, model) })
            put(DifferenceModel.OperationDrop + DifferenceModel.EntityTypeIndex,
                    { service, model ->  dropIndex(service, model) })
            put(DifferenceModel.OperationDrop + DifferenceModel.EntityTypeTable,
                    { service, model ->  dropTable(service) })
        }
    }

    init {
        this.migrationService.createTable()
    }

    public fun handleMigrations(newId:Long=0) {
        val differenceReport = this.getDifferenceReport()

        if (!differenceReport.migrationExists || differenceReport.differenceExists()) {
            this.applyMigrations(differenceReport)
            this.createNewMigration(newId)
        }
    }

    public fun getLatestMigrationDefinition() : DefinitionModels? {
        val whereItem = WhereClauseItem(
                MigrationModel.ContextName,
                WhereClauseItem.Equals,
                this.contextName)

        val migrations = this.migrationService.where(whereItem)

        if (!migrations.isEmpty()) {
            val lastMigration =  migrations
                    .sortedByDescending { it.id }
                    .first()

            return this.gson
                    .fromJson(
                            lastMigration.modelDefinitionJson,
                            DefinitionModels::class.java)
        }

        return null
    }

    public fun createNewMigration(id:Long) {
        val definitionsModels = this.getDefinitions()

        val migrationModel = MigrationModel(
                id,
                this.contextName,
                this.gson.toJson(definitionsModels))

        this.migrationService.create(migrationModel)
    }

    public fun getDifferenceReport() : DifferenceReportModel {
        val returnModels = ArrayList<DifferenceModel>()

        val latestMigration = this.getLatestMigrationDefinition()
        val currentModel = this.getDefinitions()

        currentModel
            .definitionModels
            .forEach { currentDefinition ->
                if (latestMigration == null) {
                    DefinitionModelComparisonUtil
                            .addDifferenceIfDifferent(
                                    currentDefinition,
                                    null,
                                    returnModels)
                }
                else {
                    val foundDefinition = latestMigration.definitionModels
                            .filter { currentDefinition.name.equals(it.name) }
                            .firstOrNull()

                    // will look at children as well
                    DefinitionModelComparisonUtil
                            .addDifferenceIfDifferent(
                                    currentDefinition,
                                    foundDefinition,
                                    returnModels)
                }
            }

        return DifferenceReportModel(
                latestMigration != null,
                returnModels)
    }

    public fun applyMigrations(differenceReportModel: DifferenceReportModel) {
        if (!differenceReportModel.migrationExists) {
            this.entityServices
                .forEach {
                    it.createTable()
                }
            return
        }

        differenceReportModel
            .differences
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

    public fun getDefinitions():DefinitionModels {
        val returnList = ArrayList<DefinitionModel>()

        this.entityServices.forEach {
            val propertyNames = it.entityDefinition
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                    .map { it.name.substring(CommonSqlDataTypeUtilities.GetSetLength) }
                    .toHashSet()

            val propertyDefinitions = it.entityDefinition
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            propertyNames.contains(it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)) &&
                            !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.returnType.name) }
                    .map {
                        val name = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
                        val javaType = it.returnType.name
                        PropertyDefinitionModel(name, javaType)
                    }

            returnList.add(DefinitionModel(
                    it.entityDefinition.simpleName,
                    propertyDefinitions,
                    it.indexDefinition))
        }

        return DefinitionModels(returnList)
    }

    private fun createIndex(entityService: IEntityService<*,*>, differenceModel: DifferenceModel):Boolean {
        if (differenceModel.indexModel != null) {
            return entityService.createIndex(differenceModel.indexModel)
        }
        return false
    }

    private fun dropIndex(entityService: IEntityService<*, *>, differenceModel: DifferenceModel):Boolean {
        if (differenceModel.indexModel != null) {
            return entityService.dropIndex(differenceModel.indexModel)
        }
        return false
    }

    private fun createColumn(entityService: IEntityService<*, *>, differenceModel: DifferenceModel):Boolean {
        if (differenceModel.propertyDefinition != null) {
            return entityService.createColumn(differenceModel.propertyDefinition)
        }
        return false
    }

    private fun dropColumn(entityService: IEntityService<*, *>, differenceModel: DifferenceModel):Boolean {
        if (differenceModel.propertyDefinition != null) {
            return entityService.dropColumn(differenceModel.propertyDefinition)
        }
        return false
    }

    private fun createTable(entityService: IEntityService<*, *>):Boolean {
        return entityService.createTable()
    }

    private fun dropTable(entityService: IEntityService<*, *>):Boolean {
        return entityService.dropTable()
    }
}
