package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import java.util.*

internal class ConvertProtobufToRecords(
        internal val definitions: MutableMap<String, YaormModel.TableDefinitionGraphs>,
        internal val customIndexes: MutableMap<String, YaormModel.Index>) {
    internal fun build(message: Message): MutableMap<String, YaormModel.TableRecords.Builder> {
        if (!ProtobufUtils.isMessageOk(message)) {
            return HashMap()
        }

        val mainMessageId = ProtobufUtils.getIdFromMessage(message)
        val recordsMap = HashMap<String, YaormModel.TableRecords.Builder>()

        if (!definitions.containsKey(message.descriptorForType.name)) {
            definitions[message.descriptorForType.name] = ProtobufUtils.buildDefinitionGraph(
                    message.descriptorForType,
                    this.customIndexes)
        }

        definitions[message.descriptorForType.name]!!
                .tableDefinitionGraphsList
                .forEach {
            if (!recordsMap.containsKey(it.mainName)) {
                recordsMap[it.mainName] = YaormModel.TableRecords.newBuilder()
                        .setTableName(it.mainName)
                        .setTableDefinition(it.mainTableDefinition)
            }
            if (it.hasLinkerTableTable() && !recordsMap.containsKey(it.linkerTableTable.name)) {
                recordsMap[it.linkerTableTable.name] = YaormModel.TableRecords.newBuilder()
                        .setTableName(it.linkerTableTable.name)
                        .setTableDefinition(it.linkerTableTable)
            }
            if (it.hasOtherTableDefinition() && !recordsMap.containsKey(it.otherName)) {
                recordsMap[it.otherName] = YaormModel.TableRecords.newBuilder()
                        .setTableName(it.otherName)
                        .setTableDefinition(it.otherTableDefinition)
            }
        }

        // get base record
        val baseRecord = YaormModel.Record.newBuilder()
        definitions[message.descriptorForType.name]!!
                .mainTableDefinition
                .columnDefinitionsList
                .filter { it.columnType == YaormModel.ColumnDefinition.ColumnType.SCALAR }
                .forEach { column ->
                    val foundField = message.allFields.keys.firstOrNull { column.name == it.name }
                    if (foundField == null) {
                        val generatedColumn = YaormUtils.buildColumn(null, column)
                        baseRecord.addColumns(generatedColumn)
                    }
                    else {
                        val generatedColumn = YaormUtils.buildColumn(message.allFields[foundField], column)
                        baseRecord.addColumns(generatedColumn)
                    }
                }

        // let's do enums first
        definitions[message.descriptorForType.name]!!
                .mainTableDefinition
                .columnDefinitionsList
                .filter { it.columnType == YaormModel.ColumnDefinition.ColumnType.ENUM_NAME }
                .forEach { columnDefinition ->
                    val foundMessageField = message.allFields.keys.firstOrNull { it.name == columnDefinition.name }
                    if (foundMessageField == null) {
                        val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Default, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                    }
                    else {
                        val foundField = message.allFields[foundMessageField]
                        if (foundField is Descriptors.EnumValueDescriptor) {
                            val generatedNameColumn = YaormUtils.buildColumn(foundField.name, columnDefinition)
                            baseRecord.addColumns(generatedNameColumn)
                        }
                        else {
                            val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Default, columnDefinition)
                            baseRecord.addColumns(generatedNameColumn)
                        }
                    }
                }

        // now messages, we'll add a column for the foreign key, but then new records for the child object
        definitions[message.descriptorForType.name]!!
                .mainTableDefinition
                .columnDefinitionsList
                .filter { it.columnType == YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY }
                .forEach { columnDefinition ->
                    val foundMessageField = message.allFields.keys.firstOrNull { it.name == columnDefinition.name }
                    if (foundMessageField == null) {
                        val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Empty, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                    }
                    else {
                        val foundField = message.allFields[foundMessageField]
                        if (foundField is Message && ProtobufUtils.isMessageOk(foundField)) {
                            val generatedNameColumn = YaormUtils.buildColumn(
                                    ProtobufUtils.getIdFromMessage(foundField),
                                    columnDefinition)
                            baseRecord.addColumns(generatedNameColumn)

                            if (!foundMessageField.options.weak) {
                                val childMessageRecords = this.build(foundField)
                                childMessageRecords.keys.forEach {
                                    if (recordsMap.containsKey(it)) {
                                        recordsMap[it]!!.mergeRecords(childMessageRecords[it]!!.records)
                                    }
                                    else {
                                        recordsMap[it] = childMessageRecords[it]!!
                                    }
                                }
                            }
                        }
                        else {
                            val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Empty, columnDefinition)
                            baseRecord.addColumns(generatedNameColumn)
                        }
                    }
                }

        // add base
        if (recordsMap.containsKey(definitions[message.descriptorForType.name]!!.mainTableDefinition.name)) {
            recordsMap[definitions[message.descriptorForType.name]!!.mainTableDefinition.name]!!.mergeRecords(YaormModel.Records.newBuilder().addRecords(baseRecord).build())
        }
        else {
            recordsMap[definitions[message.descriptorForType.name]!!.mainTableDefinition.name] = YaormModel.TableRecords.newBuilder()
                    .setTableDefinition(definitions[message.descriptorForType.name]!!.mainTableDefinition)
                    .setTableName(definitions[message.descriptorForType.name]!!.mainTableDefinition.name)
                    .setRecords(YaormModel.Records.newBuilder().addRecords(baseRecord))
        }

        // let's add the enum children now
        val repeatedEnumMap = ProtobufUtils.handleRepeatedEnumFields(
                message,
                mainMessageId,
                this.definitions[message.descriptorForType.name]!!)
        repeatedEnumMap.keys.forEach {
            if (recordsMap.containsKey(it)) {
                recordsMap[it]!!.mergeRecords(repeatedEnumMap[it]!!.records)
            }
            else {
                recordsMap[it] = repeatedEnumMap[it]!!
            }
        }

        // add in messageType children now
        val repeatedMessageMap = ProtobufUtils.handleRepeatedMessageFields(message, mainMessageId, this)
        repeatedMessageMap.keys.forEach {
            if (recordsMap.containsKey(it)) {
                recordsMap[it]!!.mergeRecords(repeatedMessageMap[it]!!.records)
            }
            else {
                recordsMap[it] = repeatedMessageMap[it]!!
            }
        }

        return recordsMap
    }

}
