package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.models.YaormModel
import java.util.*

object ProtobufUtils {
    const val ProtoStringName = "STRING"
    const val ProtoInt32Name = "INT32"
    const val ProtoInt64Name = "INT64"
    const val ProtoUInt32Name = "UINT32"
    const val ProtoUInt64Name = "UINT64"
    const val ProtoSInt32Name = "SINT32"
    const val ProtoSInt64Name = "SINT64"
    const val ProtoFixed32Name = "FIXED32"
    const val ProtoFixed64Name = "FIXED64"
    const val ProtoSFixed32Name = "SFIXED32"
    const val ProtoSFixed64Name = "SFIXED64"
    const val ProtoBoolName = "BOOL"
    const val ProtoBytesName = "BYTES"
    const val ProtoDoubleName = "DOUBLE"
    const val ProtoFloatName = "FLOAT"
    const val ProtoEnumType = "ENUM"
    const val ProtoMessageType = "MESSAGE"
    const val Default = "DEFAULT"
    const val Empty = ""

    val ProtoNameToProtoTypeMap = object: HashMap<String, YaormModel.ProtobufType>() {
        init {
            put(ProtoStringName, YaormModel.ProtobufType.STRING)
            put(ProtoInt32Name, YaormModel.ProtobufType.INT32)
            put(ProtoInt64Name, YaormModel.ProtobufType.INT64)
            put(ProtoUInt32Name, YaormModel.ProtobufType.UINT32)
            put(ProtoUInt64Name, YaormModel.ProtobufType.UINT64)
            put(ProtoSInt32Name, YaormModel.ProtobufType.SINT32)
            put(ProtoSInt64Name, YaormModel.ProtobufType.SINT64)
            put(ProtoFixed32Name, YaormModel.ProtobufType.FIXED32)
            put(ProtoFixed64Name, YaormModel.ProtobufType.FIXED64)
            put(ProtoSFixed32Name, YaormModel.ProtobufType.SFIXED32)
            put(ProtoSFixed64Name, YaormModel.ProtobufType.SFIXED64)
            put(ProtoBoolName, YaormModel.ProtobufType.BOOL)
            put(ProtoBytesName, YaormModel.ProtobufType.BYTES)
            put(ProtoDoubleName, YaormModel.ProtobufType.DOUBLE)
            put(ProtoFloatName, YaormModel.ProtobufType.FLOAT)
        }
    }

    fun buildDefinitionFromDescriptor(descriptor:Descriptors.Descriptor):YaormModel.TableDefinition? {
        // make sure we have an id, or return nothing
        descriptor.fields.firstOrNull { CommonUtils.IdName.equals(it.name) && ProtoStringName.equals(it.type.name) } ?: return null

        val definition = YaormModel.TableDefinition.newBuilder().setName(descriptor.name)
        descriptor.fields
                .forEach {
                    if (ProtoNameToProtoTypeMap.containsKey(it.type.name)) {
                        val newProperty = YaormModel.ColumnDefinition.newBuilder()
                                .setName(it.name)
                                .setType(ProtoNameToProtoTypeMap[it.type.name])
                        definition.addColumnDefinitions(newProperty)
                    }
                    else {
                        if (it.isRepeated) {
                            // linker table
                            return@forEach
                        }
                        else if (ProtoEnumType.equals(it.type.name)) {
                            definition.addColumnDefinitions(buildEnumNameColumnName(it.name))
                        }
                        else if (ProtoMessageType.equals(it.type.name)) {
                            definition.addColumnDefinitions(buildMessageColumnName(it.name))
                        }
                    }
        }

        return definition.build()
    }

    fun buildDefinitionGraph(descriptor:Descriptors.Descriptor):YaormModel.TableDefinitionGraphs {
        val mainDefinition = buildDefinitionFromDescriptor(descriptor) ?: return YaormModel.TableDefinitionGraphs.getDefaultInstance()
        val returnGraph = YaormModel.TableDefinitionGraphs.newBuilder().setMainTableDefinition(mainDefinition)

        descriptor.fields
            .filter { it.isRepeated }
            .forEach {
                if (ProtoEnumType.equals(it.type.name)) {
                    val definitionGraph = YaormModel.TableDefinitionGraph.newBuilder()
                            .setDefinitionGraphType(YaormModel.TableDefinitionGraph.TableDefinitionGraphType.ENUM_TYPE)
                            .setMainName(mainDefinition.name)
                            .setOtherName(it.enumType.name)
                            .setColumnName(it.name)
                    val linkerTableDefinition = buildLinkerTableEnum(mainDefinition.name, it.enumType.name, it.name)
                    definitionGraph.linkerTableTable = linkerTableDefinition
                    definitionGraph.mainTableDefinition = mainDefinition
                    returnGraph.addTableDefinitionGraphs(definitionGraph)
                }
                else if (ProtoMessageType.equals(it.type.name)) {
                    val otherDefinition = buildDefinitionFromDescriptor(it.messageType) ?: return@forEach
                    val definitionGraph = YaormModel.TableDefinitionGraph.newBuilder()
                            .setDefinitionGraphType(YaormModel.TableDefinitionGraph.TableDefinitionGraphType.MESSAGE_TYPE)
                            .setMainName(mainDefinition.name)
                            .setOtherName(otherDefinition.name)
                            .setColumnName(it.name)

                    val linkerTableDefinition = buildLinkerTableMessage(mainDefinition.name, otherDefinition.name, it.name)

                    definitionGraph.mainTableDefinition = mainDefinition
                    definitionGraph.linkerTableTable = linkerTableDefinition
                    definitionGraph.otherTableDefinition = otherDefinition
                    returnGraph.addTableDefinitionGraphs(definitionGraph)
                }
            }

        return returnGraph.build()
    }

    fun buildDefinitionGraphForNameSpace(descriptor:Descriptors.FileDescriptor):List<YaormModel.TableDefinitionGraphs> {
        val returnList = ArrayList<YaormModel.TableDefinitionGraphs>()

        descriptor.messageTypes.forEach {
            val definitionGraph = buildDefinitionGraph(it)
            returnList.add(definitionGraph)
        }

        return returnList
    }

    fun convertProtobufObjectToRecords(message:GeneratedMessage):YaormModel.AllTableRecords {
        if (!isMessageOk(message)) {
            return YaormModel.AllTableRecords.getDefaultInstance()
        }

        val mainMessageId = getIdFromGeneratedMessage(message)

        val definitions = buildDefinitionGraph(message.descriptorForType)
        val returnRecords = YaormModel.AllTableRecords.newBuilder()

        // get base record
        val baseRecord = YaormModel.Record.newBuilder()
        definitions.mainTableDefinition.columnDefinitionsList
                .filter { it.columnType.equals(YaormModel.ColumnDefinition.ColumnType.SCALAR) }
                .forEach { column ->
                    val foundField = message.allFields.keys.firstOrNull { column.name.equals(it.name) }
                    if (foundField == null) {
                        val generatedColumn = CommonUtils.buildColumn(null, column)
                        baseRecord.addColumns(generatedColumn)
                    }
                    else {
                        val generatedColumn = CommonUtils.buildColumn(message.allFields[foundField], column)
                        baseRecord.addColumns(generatedColumn)
                    }
        }

        // let's do enums first
        definitions.mainTableDefinition
                .columnDefinitionsList
                .filter { it.columnType.equals(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME) }
                .forEach { columnDefinition ->
                    val foundMessageField = message.allFields.keys.firstOrNull { it.name.equals(columnDefinition.name) }

                    if (foundMessageField == null) {
                        val generatedNameColumn = CommonUtils.buildColumn(Default, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                        return@forEach
                    }

                    val foundField = message.allFields[foundMessageField]
                    if (foundField is Descriptors.EnumValueDescriptor) {
                        val generatedNameColumn = CommonUtils.buildColumn(foundField.name, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                    }
                    else {
                        val generatedNameColumn = CommonUtils.buildColumn(Default, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                    }
                }

        // now messages
        definitions.mainTableDefinition
                .columnDefinitionsList
                .filter { it.columnType.equals(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY) }
                .forEach { columnDefinition ->
                    val foundMessageField = message.allFields.keys.firstOrNull { it.name.equals(columnDefinition.name) }
                    if (foundMessageField == null) {
                        val generatedNameColumn = CommonUtils.buildColumn(Empty, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                        return@forEach
                    }

                    val foundField = message.allFields[foundMessageField]
                    if (foundField is GeneratedMessage && isMessageOk(foundField)) {
                        val generatedNameColumn = CommonUtils.buildColumn(getIdFromGeneratedMessage(foundField), columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                    }
                    else {
                        val generatedNameColumn = CommonUtils.buildColumn(Empty, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                    }
                }

        returnRecords.addTableRecords(YaormModel.TableRecords.newBuilder().setTableDefinition(definitions.mainTableDefinition).setRecords(YaormModel.Records.newBuilder().addRecords(baseRecord)))

        // let's add the enum children now
        message.allFields.keys.filter { it.type.name.equals(ProtoEnumType) && it.isRepeated }
            .forEach { fieldKey ->
                val foundLinkerTable = definitions.tableDefinitionGraphsList.firstOrNull {
                    it.otherName.equals(fieldKey.enumType.name) &&
                    it.columnName.equals(fieldKey.name)
                } ?: return@forEach

                val tableRecords = YaormModel.TableRecords.newBuilder().setTableDefinition(foundLinkerTable.linkerTableTable)
                val foundItem = message.allFields[fieldKey]

                if (foundItem == null || foundItem !is List<*> || foundItem.size == 0) {
                    return@forEach
                }
                else {
                    val records = YaormModel.Records.newBuilder()
                    foundItem.filter { it is Descriptors.EnumValueDescriptor }.forEach {
                        val castedDescriptor = it as Descriptors.EnumValueDescriptor

                        val record = YaormModel.Record.newBuilder()

                        val idColumn = YaormModel.Column.newBuilder()
                                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                                    .setName(CommonUtils.IdName)
                                    .setType(YaormModel.ProtobufType.STRING))
                                .setStringHolder(UUID.randomUUID().toString())

                        val mainIdColumn = YaormModel.Column.newBuilder()
                                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                                        .setName(message.descriptorForType.name)
                                        .setType(YaormModel.ProtobufType.STRING)
                                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY))
                               .setStringHolder(mainMessageId)

                        val enumColumnn = YaormModel.Column.newBuilder()
                                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                                        .setName(fieldKey.enumType.name)
                                        .setType(YaormModel.ProtobufType.STRING)
                                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME))
                                .setStringHolder(castedDescriptor.name)


                        record.addColumns(idColumn)
                        record.addColumns(mainIdColumn)
                        record.addColumns(enumColumnn)
                        records.addRecords(record)
                    }
                    tableRecords.setRecords(records)
                    returnRecords.addTableRecords(tableRecords)
                }
            }

        // add in message children now

        return returnRecords.build()
    }

    private fun getIdFromGeneratedMessage(message:GeneratedMessage):String {
        val foundIdField = message.allFields.keys.firstOrNull { it.name.equals(CommonUtils.IdName) } ?: Empty
        return message.allFields[foundIdField]!!.toString()
    }

    private fun isMessageOk(message: GeneratedMessage):Boolean {
        return message.allFields.keys.any { it.name.equals(CommonUtils.IdName) }
    }

    private fun buildLinkerTableEnum(mainTableName:String, repeatedEnumName:String, repeatedEnumColumnName:String):YaormModel.TableDefinition {
        val returnDefinition = YaormModel.TableDefinition.newBuilder()
                .setName("${mainTableName}_${repeatedEnumName}_$repeatedEnumColumnName")

        val idProperty = YaormModel.ColumnDefinition.newBuilder().setName(CommonUtils.IdName).setType(YaormModel.ProtobufType.STRING).build()
        val mainTableIdProperty = YaormModel.ColumnDefinition.newBuilder().setName(mainTableName).setType(YaormModel.ProtobufType.STRING).build()
        val repeatedEnumNameProperty = YaormModel.ColumnDefinition.newBuilder().setName(repeatedEnumName).setType(YaormModel.ProtobufType.STRING).setColumnType(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME).build()
        return returnDefinition.addColumnDefinitions(idProperty)
                .addColumnDefinitions(mainTableIdProperty)
                .addColumnDefinitions(repeatedEnumNameProperty)
                .build()
    }

    private fun buildLinkerTableMessage(mainTableName:String, linkerTableName:String, linkerTableColumnName:String):YaormModel.TableDefinition {
        val returnDefinition = YaormModel.TableDefinition.newBuilder().setName(buildLinkerTableNameStr(mainTableName, linkerTableName, linkerTableColumnName))
        val idProperty = YaormModel.ColumnDefinition.newBuilder().setName(CommonUtils.IdName).setType(YaormModel.ProtobufType.STRING).build()
        val mainTableIdProperty = YaormModel.ColumnDefinition.newBuilder().setName(mainTableName).setType(YaormModel.ProtobufType.STRING).setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY).build()
        val otherTableIdProperty = YaormModel.ColumnDefinition.newBuilder().setName(linkerTableName).setType(YaormModel.ProtobufType.STRING).setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY).build()
        return returnDefinition.addColumnDefinitions(idProperty)
                .addColumnDefinitions(mainTableIdProperty)
                .addColumnDefinitions(otherTableIdProperty)
                .build()
    }

    private fun buildMessageColumnName(name: String):YaormModel.ColumnDefinition {
        return YaormModel.ColumnDefinition.newBuilder().setName(name).setType(YaormModel.ProtobufType.STRING).setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY) .build()
    }

    private fun buildEnumNameColumnName(name:String):YaormModel.ColumnDefinition {
        return YaormModel.ColumnDefinition.newBuilder().setName(name).setType(YaormModel.ProtobufType.STRING).setColumnType(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME).build()
    }

    private fun buildLinkerTableNameStr(first:String, second:String, columnName:String):String {
        return "${first}_${second}_$columnName"
    }
}
