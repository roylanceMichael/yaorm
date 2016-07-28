package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.proto.IEntityProtoService
import org.roylance.yaorm.services.proto.IProtoGeneratedMessageBuilder
import java.util.*

internal class GetProtoObject(
        private val entityService: IEntityProtoService,
        private val generatedMessageBuilder: IProtoGeneratedMessageBuilder,
        private val definitions: MutableMap<String, YaormModel.TableDefinitionGraphs>,
        private val customIndexes: MutableMap<String, YaormModel.Index>) {
    private val typeNameMap = HashMap<String, Message.Builder>()

    internal fun <T: Message> build(builder: T, entityId:String):T? {
        val uniqueKey = ProtobufUtils.buildUniqueKey(builder, entityId)
        if (this.typeNameMap.containsKey(uniqueKey)) {
            return this.typeNameMap[uniqueKey]!!.build() as T
        }

        if (!definitions.containsKey(builder.descriptorForType.name)) {
            definitions[builder.descriptorForType.name] = ProtobufUtils.buildDefinitionGraph(builder.descriptorForType, this.customIndexes)
        }
        val tableDefinitionGraph = definitions[builder.descriptorForType.name]!!
        val builderForType = builder.newBuilderForType()

        val foundRecord = this.entityService.get(entityId, tableDefinitionGraph.mainTableDefinition)
        if (foundRecord == null) {
            this.typeNameMap[uniqueKey] = builderForType
            return null
        }

        val childMessageHandler = object: IChildMessageHandler {
            override fun handle(fieldKey: Descriptors.FieldDescriptor, idColumn: YaormModel.Column, builder: Message.Builder) {
                // recursively get the child object
                val childObject = generatedMessageBuilder.buildGeneratedMessage(fieldKey.messageType.name)
                if (idColumn.stringHolder.length > 0) {
                    val reconciledObject = build(childObject, idColumn.stringHolder)
                    builder.setField(fieldKey, reconciledObject)
                }
            }
        }

        // main fields
        ConvertRecordsToProtobuf.build(builderForType, foundRecord, childMessageHandler)

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
                        val nameColumn = record.columnsList.firstOrNull { fieldKey.enumType.name.equals(it.definition.name) }
                        if (nameColumn != null) {
                            val enumToAdd = fieldKey.enumType.findValueByName(nameColumn.stringHolder.toUpperCase())
                            builderForType.addRepeatedField(fieldKey, enumToAdd)
                        }
                    }
                }

        this.typeNameMap[ProtobufUtils.buildUniqueKey(builder, entityId)] = builderForType

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
                    val otherColumnName = ProtobufUtils.buildLinkerMessageOtherTableColumnName(fieldKey.messageType.name)

                    foundRecords.recordsList
                            .forEach { record ->
                                val nameColumn = record.columnsList.firstOrNull { otherColumnName.equals(it.definition.name) }
                                if (nameColumn != null) {
                                    if (nameColumn.stringHolder.length > 0) {
                                        val constructedMessage = this.build(childBuilder, nameColumn.stringHolder)
                                        builderForType.addRepeatedField(fieldKey, constructedMessage)
                                    }
                                }
                            }
                }

        return this.typeNameMap[ProtobufUtils.buildUniqueKey(builder, entityId)]!!.build() as T
    }
}
