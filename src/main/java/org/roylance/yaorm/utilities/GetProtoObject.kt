package org.roylance.yaorm.utilities

import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.proto.IEntityProtoService
import org.roylance.yaorm.services.proto.IProtoGeneratedMessageBuilder
import java.util.*

internal class GetProtoObject(
        private val entityService: IEntityProtoService,
        private val generatedMessageBuilder: IProtoGeneratedMessageBuilder) {
    private val tableDefinitionGraphs = HashMap<String, YaormModel.TableDefinitionGraphs>()

    internal fun <T: Message> execute(builder: T, entityId:String):T {
        if (!tableDefinitionGraphs.containsKey(builder.descriptorForType.name)) {
            tableDefinitionGraphs[builder.descriptorForType.name] = ProtobufUtils.buildDefinitionGraph(builder.descriptorForType)
        }
        val tableDefinitionGraph = tableDefinitionGraphs[builder.descriptorForType.name]!!
        val builderForType = builder.newBuilderForType()

        val foundRecord = this.entityService.get(entityId, tableDefinitionGraph.mainTableDefinition)
                ?: return builderForType.build() as T

        // main fields
        builder.descriptorForType
                .fields
                .filter { !it.isRepeated }
                .forEach { fieldKey ->
                    if (!foundRecord.columns.containsKey(fieldKey.name)) {
                        return@forEach
                    }
                    val foundColumn = foundRecord.columns[fieldKey.name]!!
                    if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.SCALAR)) {
                        builderForType.setField(fieldKey, CommonUtils.getAnyObject(foundColumn))
                    }
                    else if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME)) {
                        builderForType.setField(fieldKey, fieldKey.enumType.findValueByName(foundColumn.stringHolder.toUpperCase()))
                    }
                    else if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)) {
                        // recursively get the child object
                        val childObject = generatedMessageBuilder.buildGeneratedMessage(fieldKey.messageType.name)
                        if (foundColumn.stringHolder.length > 0) {
                            val reconciledObject = this.execute(childObject, foundColumn.stringHolder)
                            builderForType.setField(fieldKey, reconciledObject)
                        }
                    }
                }

        // repeated enums
        builder.descriptorForType
                .fields
                .filter { it.type.name.equals(ProtobufUtils.ProtoEnumType) && it.isRepeated }
                .forEach { fieldKey ->
                    val definitionForLinkerTable = tableDefinitionGraph.tableDefinitionGraphsList
                            .firstOrNull { it.columnName.equals(fieldKey.name) && it.otherName.equals(fieldKey.enumType.name) }
                            ?: return@forEach

                    val customWhereClause = YaormModel.WhereClause.newBuilder()
                            .setNameAndProperty(YaormModel.Column.newBuilder()
                                    .setDefinition(
                                            YaormModel.ColumnDefinition.newBuilder()
                                                    .setName(builder.descriptorForType.name)
                                                    .setType(YaormModel.ProtobufType.STRING))
                                    .setStringHolder(entityId))
                            .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                            .build()

                    val foundRecords = entityService.where(customWhereClause, definitionForLinkerTable.linkerTableTable)
                    foundRecords.recordsList.forEach { record ->
                        if (record.columns.containsKey(fieldKey.enumType.name)) {
                            val nameColumn = record.columns[fieldKey.enumType.name]!!
                            val enumToAdd = fieldKey.enumType.findValueByName(nameColumn.stringHolder.toUpperCase())
                            builderForType.addRepeatedField(fieldKey, enumToAdd)
                        }
                    }
                }

        // repeated messages
        builder.descriptorForType
                .fields
                .filter { it.type.name.equals(ProtobufUtils.ProtoMessageType) && it.isRepeated }
                .forEach { fieldKey ->
                    val definitionForLinkerTable = tableDefinitionGraph.tableDefinitionGraphsList
                            .firstOrNull { it.columnName.equals(fieldKey.name) && it.otherName.equals(fieldKey.messageType.name) }
                            ?: return@forEach

                    val mainColumnName = ProtobufUtils.buildLinkerMessageMainTableColumnName(builder.descriptorForType.name)
                    val customWhereClause = YaormModel.WhereClause.newBuilder()
                            .setNameAndProperty(YaormModel.Column.newBuilder()
                                    .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                                            .setName(mainColumnName)
                                            .setType(YaormModel.ProtobufType.STRING))
                                    .setStringHolder(entityId))
                            .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                            .build()

                    val childBuilder = generatedMessageBuilder.buildGeneratedMessage(fieldKey.messageType.name)
                    if (!ProtobufUtils.isMessageOk(childBuilder)) {
                        return@forEach
                    }

                    val foundRecords = entityService.where(customWhereClause, definitionForLinkerTable.linkerTableTable)
                    foundRecords.recordsList
                            .forEach { record ->
                        if (record.columns.containsKey(fieldKey.messageType.name)) {
                            val nameColumn = record.columns[fieldKey.messageType.name]!!
                            if (nameColumn.stringHolder.length > 0) {
                                val constructedMessage = this.execute(childBuilder, nameColumn.stringHolder)
                                builderForType.addRepeatedField(fieldKey, constructedMessage)
                            }
                        }
                    }
                }

        return builderForType.build() as T
    }
}
