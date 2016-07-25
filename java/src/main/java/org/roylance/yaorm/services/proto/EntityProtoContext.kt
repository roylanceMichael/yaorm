package org.roylance.yaorm.services.proto

import com.google.protobuf.Descriptors
import org.roylance.common.service.IBase64Service
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.utilities.ProtobufUtils
import org.roylance.yaorm.utilities.YaormUtils
import org.roylance.yaorm.utilities.migration.DefinitionModelComparisonUtil
import java.util.*

class EntityProtoContext(
        val fileDescriptor: Descriptors.FileDescriptor,
        val protoGeneratedMessageBuilder: IProtoGeneratedMessageBuilder,
        val protoService: IEntityProtoService,
        val contextName:String,
        val customIndexes: HashMap<String, YaormModel.Index>,
        val base64Service: IBase64Service) {
    val entityMessageService: IEntityMessageService

    private val typeToAction = object: HashMap<
            String,
            (difference: YaormModel.Difference) -> Boolean>() {
        init {
            put(YaormModel.Difference.Operation.CREATE.name + YaormModel.Difference.EntityType.COLUMN.name,
                    { model ->  createColumn(model) })
            put(YaormModel.Difference.Operation.CREATE.name + YaormModel.Difference.EntityType.INDEX.name,
                    { model ->  createIndex(model) })
            put(YaormModel.Difference.Operation.CREATE.name + YaormModel.Difference.EntityType.TABLE.name,
                    { model ->  createTable(model) })
            put(YaormModel.Difference.Operation.DROP.name + YaormModel.Difference.EntityType.COLUMN.name,
                    { model ->  dropColumn(model) })
            put(YaormModel.Difference.Operation.DROP.name + YaormModel.Difference.EntityType.INDEX.name,
                    { model ->  dropIndex(model) })
            put(YaormModel.Difference.Operation.DROP.name + YaormModel.Difference.EntityType.TABLE.name,
                    { model ->  dropTable(model) })
        }
    }

    init {
        this.entityMessageService = EntityMessageService(this.protoGeneratedMessageBuilder,
                this.protoService,
                this.customIndexes)
        this.entityMessageService.createEntireSchema(YaormModel.Migration.getDefaultInstance())
    }

    fun handleMigrations(newId:String = UUID.randomUUID().toString()) {
        val differenceReport = this.getDifferenceReport()

        if (!differenceReport.migrationExists || differenceReport.differencesCount > 0) {
            this.applyMigrations(differenceReport)
            this.createNewMigration(newId)
        }
    }

    fun createNewMigration(id:String) {
        val definitionsModels = this.getDefinitions()
        val string64 = this.base64Service.serialize(definitionsModels.toByteArray())
        val migrationModel = YaormModel.Migration.newBuilder()
            .setId(id)
            .setContextName(this.contextName)
            .setModelDefinitionBase64(string64)
            .setInsertDate(Date().time)
            .build()

        this.entityMessageService.merge(migrationModel)
    }

    fun applyMigrations(differenceReportModel: YaormModel.DifferenceReport) {
        if (!differenceReportModel.migrationExists) {
            this.entityMessageService.createEntireSchema(this.fileDescriptor)
            return
        }

        differenceReportModel
                .differencesList
                .forEach { differenceModel ->
                    val key = "${differenceModel.operation}${differenceModel.entityType}"
                    if (this.typeToAction.containsKey(key)) {
                        this.typeToAction[key]!!.invoke(differenceModel)
                    }
                }
    }

    fun getDefinitions():YaormModel.TableDefinitions {
        val actualDefinitions = HashMap<String, YaormModel.TableDefinition>()

        this.fileDescriptor.messageTypes.forEach { messageDescriptor ->
            val foundDefinitions = ProtobufUtils.buildDefinitionGraph(messageDescriptor, this.customIndexes)

            if (YaormUtils.checkIfOk(foundDefinitions.mainTableDefinition)) {
                actualDefinitions[foundDefinitions.mainTableDefinition.name] = foundDefinitions.mainTableDefinition
            }

            foundDefinitions.tableDefinitionGraphsList.forEach { graph ->
                if (YaormUtils.checkIfOk(graph.mainTableDefinition)) {
                    actualDefinitions[graph.mainTableDefinition.name] = graph.mainTableDefinition
                }
                if (graph.hasLinkerTableTable() &&
                        YaormUtils.checkIfOk(graph.linkerTableTable)) {
                    actualDefinitions[graph.linkerTableTable.name] = graph.linkerTableTable
                }
                if (graph.hasOtherTableDefinition() &&
                        YaormUtils.checkIfOk(graph.otherTableDefinition)) {
                    actualDefinitions[graph.otherName] = graph.otherTableDefinition
                }
            }
        }

        return YaormModel.TableDefinitions.newBuilder().addAllTableDefinitions(actualDefinitions.values).build()
    }

    fun getDifferenceReport() : YaormModel.DifferenceReport {
        val returnModels = ArrayList<YaormModel.Difference>()

        val latestMigration = this.getLatestMigrationDefinition()
        val currentModel = this.getDefinitions()

        currentModel
                .tableDefinitionsList
                .forEach { currentDefinition ->
                    if (latestMigration == null) {
                        DefinitionModelComparisonUtil
                                .addDifferenceIfDifferent(
                                        currentDefinition,
                                        null,
                                        returnModels)
                    }
                    else {
                        val foundDefinition = latestMigration.tableDefinitionsList
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

        return YaormModel.DifferenceReport.newBuilder()
                .addAllDifferences(returnModels)
                .setMigrationExists(latestMigration != null)
                .build()
    }

    fun getLatestMigrationDefinition() : YaormModel.TableDefinitions? {
        val propertyHolder = YaormModel.Column.newBuilder().setStringHolder(this.contextName)

        val propertyDefinition = YaormModel.ColumnDefinition.newBuilder()
                .setName(YaormModel.Migration.getDescriptor().findFieldByNumber(YaormModel.Migration.CONTEXT_NAME_FIELD_NUMBER).name)
                .setType(YaormModel.ProtobufType.STRING)
                .setIsKey(false)
        propertyHolder.setDefinition(propertyDefinition)

        val whereClause = YaormModel.WhereClause.newBuilder()
                .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                .setNameAndProperty(propertyHolder)
                .build()

        val migrations = this.entityMessageService.where(YaormModel.Migration.getDefaultInstance(), whereClause)

        if (!migrations.isEmpty()) {
            val lastMigration =  migrations
                    .sortedByDescending { it.insertDate }
                    .first()
            val bytes = this.base64Service.deserialize(lastMigration.modelDefinitionBase64)
            return YaormModel.TableDefinitions.parseFrom(bytes)
        }

        return null
    }

    private fun createIndex(differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.hasIndex() && differenceModel.hasTableDefinition()) {
            return this.protoService.createIndex(differenceModel.index, differenceModel.tableDefinition)
        }
        return false
    }

    private fun dropIndex(differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.hasIndex() && differenceModel.hasTableDefinition()) {
            return this.protoService.dropIndex(differenceModel.index, differenceModel.tableDefinition)
        }
        return false
    }

    private fun createColumn(differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.hasPropertyDefinition() && differenceModel.hasTableDefinition()) {
            return this.protoService.createColumn(differenceModel.propertyDefinition, differenceModel.tableDefinition)
        }
        return false
    }

    private fun dropColumn(differenceModel: YaormModel.Difference):Boolean {
        if (differenceModel.propertyDefinition != null && differenceModel.hasTableDefinition()) {
            return this.protoService.dropColumn(differenceModel.propertyDefinition, differenceModel.tableDefinition)
        }
        return false
    }

    private fun createTable(difference: YaormModel.Difference):Boolean {
        if (difference.hasTableDefinition()) {
            return this.protoService.createTable(difference.tableDefinition)
        }
        return false
    }

    private fun dropTable(difference: YaormModel.Difference):Boolean {
        if (difference.hasTableDefinition()) {
            return this.protoService.dropTable(difference.tableDefinition)
        }
        return false
    }
}
