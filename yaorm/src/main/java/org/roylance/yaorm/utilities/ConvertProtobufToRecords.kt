package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.YaormModel.Record.Builder
import org.roylance.yaorm.YaormModel.TableRecords
import java.util.*

internal class ConvertProtobufToRecords(
    private val definitions: MutableMap<String, YaormModel.TableDefinitionGraphs>,
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

    handleBuildingSchema(message, recordsMap)

    // get base record
    val baseRecord = YaormModel.Record.newBuilder()

    // update the main record
    handleMainScalars(message, baseRecord)
    handleMainEnums(message, baseRecord)
    handleMainMessages(message, baseRecord, recordsMap)

    // add the records to record map
    handleAddingBaseRecordToRecrds(message, baseRecord, recordsMap)
    handleAddingInRepeatedEnums(message, mainMessageId, recordsMap)
    handleAddingInRepeatedMessages(message, mainMessageId, recordsMap)

    return recordsMap
  }

  private fun handleBuildingSchema(message: Message,
      recordsMap: HashMap<String, YaormModel.TableRecords.Builder>) {
    definitions[message.descriptorForType.name]
        ?.tableDefinitionGraphsList
        ?.forEach {
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
  }

  private fun handleMainScalars(message: Message,
      baseRecord: YaormModel.Record.Builder) {
    definitions[message.descriptorForType.name]
        ?.mainTableDefinition
        ?.columnDefinitionsList
        ?.filter { it.columnType == YaormModel.ColumnDefinition.ColumnType.SCALAR }
        ?.forEach { column ->
          val foundField = message.allFields.keys.firstOrNull { column.name == it.name }
          if (foundField == null) {
            val generatedColumn = YaormUtils.buildColumn(null, column)
            baseRecord.addColumns(generatedColumn)
          } else {
            val generatedColumn = YaormUtils.buildColumn(message.allFields[foundField], column)
            baseRecord.addColumns(generatedColumn)
          }
        }
  }

  private fun handleMainEnums(message: Message, baseRecord: Builder) {
    // let's do enums first
    definitions[message.descriptorForType.name]
        ?.mainTableDefinition
        ?.columnDefinitionsList
        ?.filter { it.columnType == YaormModel.ColumnDefinition.ColumnType.ENUM_NAME }
        ?.forEach { columnDefinition ->
          val foundMessageField = message.allFields.keys.firstOrNull { it.name == columnDefinition.name }
          if (foundMessageField == null) {
            val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Default,
                columnDefinition)
            baseRecord.addColumns(generatedNameColumn)
          } else {
            val foundField = message.allFields[foundMessageField]
            if (foundField is Descriptors.EnumValueDescriptor) {
              val generatedNameColumn = YaormUtils.buildColumn(foundField.name, columnDefinition)
              baseRecord.addColumns(generatedNameColumn)
            } else {
              val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Default,
                  columnDefinition)
              baseRecord.addColumns(generatedNameColumn)
            }
          }
        }
  }

  private fun handleMainMessages(message: Message,
      baseRecord: Builder,
      recordsMap: HashMap<String, TableRecords.Builder>) {
    // now messages, we'll add a column for the foreign key, but then new records for the child object
    definitions[message.descriptorForType.name]
        ?.mainTableDefinition
        ?.columnDefinitionsList
        ?.filter { it.columnType == YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY }
        ?.forEach { columnDefinition ->
          val foundMessageField = message.allFields.keys.firstOrNull { it.name == columnDefinition.name }
          if (foundMessageField == null) {
            val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Empty, columnDefinition)
            baseRecord.addColumns(generatedNameColumn)
          } else {
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
                    recordsMap[it]
                        ?.recordsBuilder
                        ?.addAllRecords(childMessageRecords[it]?.records?.recordsList)
                  } else {
                    recordsMap[it] = childMessageRecords[it]!!
                  }
                }
              }
            } else {
              val generatedNameColumn = YaormUtils.buildColumn(ProtobufUtils.Empty,
                  columnDefinition)
              baseRecord.addColumns(generatedNameColumn)
            }
          }
        }
  }

  private fun handleAddingBaseRecordToRecrds(message: Message,
      baseRecord: Builder,
      recordsMap: HashMap<String, TableRecords.Builder>) {
    // add base
    if (recordsMap.containsKey(
        definitions[message.descriptorForType.name]!!.mainTableDefinition.name)) {
      recordsMap[definitions[message.descriptorForType.name]?.mainTableDefinition?.name]
          ?.recordsBuilder?.addRecords(baseRecord)
    } else {
      recordsMap[definitions[message.descriptorForType.name]?.mainTableDefinition?.name!!] = YaormModel.TableRecords.newBuilder()
          .setTableDefinition(definitions[message.descriptorForType.name]!!.mainTableDefinition)
          .setTableName(definitions[message.descriptorForType.name]!!.mainTableDefinition.name)
          .setRecords(YaormModel.Records.newBuilder().addRecords(baseRecord))
    }
  }

  private fun handleAddingInRepeatedEnums(message: Message,
      mainMessageId: String,
      recordsMap: HashMap<String, TableRecords.Builder>) {
    // let's add the enum children now
    val repeatedEnumMap = ProtobufUtils.handleRepeatedEnumFields(
        message,
        mainMessageId,
        this.definitions[message.descriptorForType.name]!!)
    repeatedEnumMap.keys.forEach {
      if (recordsMap.containsKey(it)) {
        recordsMap[it]?.recordsBuilder
            ?.addAllRecords(repeatedEnumMap[it]?.records?.recordsList)
      } else {
        recordsMap[it] = repeatedEnumMap[it]!!
      }
    }
  }

  private fun handleAddingInRepeatedMessages(message: Message,
      mainMessageId: String,
      recordsMap: HashMap<String, TableRecords.Builder>) {
    // add in messageType children now
    val repeatedMessageMap = ProtobufUtils.handleRepeatedMessageFields(
        message,
        mainMessageId,
        this)
    repeatedMessageMap.keys.forEach {
      if (recordsMap.containsKey(it)) {
        recordsMap[it]?.recordsBuilder
            ?.addAllRecords(repeatedMessageMap[it]?.records?.recordsList)
      } else {
        recordsMap[it] = repeatedMessageMap[it]!!
      }
    }
  }
}
