package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.proto.IEntityProtoService
import org.roylance.yaorm.services.proto.IProtoGeneratedMessageBuilder
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

    fun buildDefinitionFromDescriptor(descriptor:Descriptors.Descriptor,
                                      customIndexes: MutableMap<String, YaormModel.Index>):YaormModel.TableDefinition? {
        // make sure we have an id, or return nothing
        descriptor.fields.firstOrNull { YaormUtils.IdName.equals(it.name) && ProtoStringName.equals(it.type.name) } ?: return null

        val definition = YaormModel.TableDefinition.newBuilder()
                .setName(descriptor.name)

        if (customIndexes.containsKey(descriptor.name)) {
            definition.index = customIndexes[descriptor.name]
        }
        descriptor.fields
                .forEach {
                    if (ProtoNameToProtoTypeMap.containsKey(it.type.name)) {
                        val newProperty = YaormModel.ColumnDefinition.newBuilder()
                                .setName(it.name)
                                .setType(ProtoNameToProtoTypeMap[it.type.name])
                                .setOrder(it.number)
                        definition.addColumnDefinitions(newProperty)
                    }
                    else {
                        if (it.isRepeated) {
                            // linker table
                            return@forEach
                        }
                        else if (ProtoEnumType.equals(it.type.name)) {
                            definition.addColumnDefinitions(buildEnumNameColumnName(it.name, it.number))
                        }
                        else if (ProtoMessageType.equals(it.type.name)) {
                            definition.addColumnDefinitions(buildMessageColumnName(it.name, it.number))
                        }
                    }
        }

        return definition.build()
    }

    fun buildDefinitionGraph(descriptor:Descriptors.Descriptor,
                             customIndexes: MutableMap<String, YaormModel.Index>,
                             seenTables: MutableSet<String> = HashSet()):YaormModel.TableDefinitionGraphs {
        seenTables.add(descriptor.name)

        val mainDefinition = buildDefinitionFromDescriptor(descriptor, customIndexes) ?: return YaormModel.TableDefinitionGraphs.getDefaultInstance()
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
                    val otherDefinition = buildDefinitionFromDescriptor(it.messageType, customIndexes) ?: return@forEach
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

                    if (!seenTables.contains(it.messageType.name)) {
                        seenTables.add(it.messageType.name)
                        val childDefinitions = buildDefinitionGraph(it.messageType, customIndexes, seenTables)
                        returnGraph.addAllTableDefinitionGraphs(childDefinitions.tableDefinitionGraphsList)
                    }
                }
            }

        return returnGraph.build()
    }

    // wrap in an object
    fun <T:Message> getProtoObjectFromBuilderSingle(builder: T,
                                                    entityService: IEntityProtoService,
                                                    entityId:String,
                                                    generatedMessageBuilder: IProtoGeneratedMessageBuilder,
                                                    definitions: MutableMap<String, YaormModel.TableDefinitionGraphs>,
                                                    customIndexes: MutableMap<String, YaormModel.Index>): T? {
        return GetProtoObject(entityService, generatedMessageBuilder, definitions, customIndexes)
                .build(builder, entityId)
    }

    fun convertProtobufObjectToRecords(message:Message,
                                       definitions: MutableMap<String, YaormModel.TableDefinitionGraphs> = HashMap(),
                                       customIndexes: MutableMap<String, YaormModel.Index> = HashMap()): YaormModel.AllTableRecords {
        val resultMap = ConvertProtobufToRecords(definitions, customIndexes).build(message)
        val returnRecords = YaormModel.AllTableRecords.newBuilder()

        resultMap.keys.forEach {
            returnRecords.addTableRecords(resultMap[it]!!)
        }

        return returnRecords.build()
    }

    fun getIdFromMessage(message:Message):String {
        val foundIdField = message.allFields.keys.firstOrNull { it.name.equals(YaormUtils.IdName) } ?: Empty
        val foundId = message.allFields[foundIdField] ?: return Empty
        return foundId.toString()
    }

    fun isMessageOk(message: Message):Boolean {
        return message.descriptorForType.fields.any { it.name.equals(YaormUtils.IdName) }
    }

    internal fun handleRepeatedMessageFields(message:Message,
                                             mainMessageId: String,
                                             convertProto:ConvertProtobufToRecords): Map<String, YaormModel.TableRecords.Builder> {
        val returnMap = HashMap<String, YaormModel.TableRecords.Builder>()
        message.allFields.keys.filter { it.type.name.equals(ProtoMessageType) && it.isRepeated }
                .forEach { fieldKey ->
                    val foundItem = message.allFields[fieldKey]

                    var foundTableDefinition:YaormModel.TableDefinition? = null
                    val linkerTableRecords = YaormModel.TableRecords.newBuilder()

                    // this is recursive
                    if (foundItem == null || foundItem !is List<*> || foundItem.size == 0) {
                        return@forEach
                    }
                    else {
                        foundItem.filter { it is Message }.forEach {
                            val subMessage = it as Message
                            if (foundTableDefinition == null) {
                                foundTableDefinition = buildMessageRepeatedRecordTableDefinition(message.descriptorForType.name,
                                        subMessage.descriptorForType.name,
                                        fieldKey.name)
                                linkerTableRecords.tableDefinition = foundTableDefinition
                                linkerTableRecords.tableName = foundTableDefinition!!.name
                            }

                            val messageId = getIdFromMessage(subMessage)
                            if (messageId != Empty) {
                                val record = buildMessageRepeatedRecord(message.descriptorForType.name,
                                        subMessage.descriptorForType.name,
                                        mainMessageId,
                                        messageId)

                                linkerTableRecords.recordsBuilder.addRecords(record)

                                // recursively call for child...
                                val subRecords = convertProto.build(subMessage)
                                subRecords.keys.forEach { subRecordKey ->
                                    if (returnMap.containsKey(subRecordKey)) {
                                        returnMap[subRecordKey]!!.mergeRecords(subRecords[subRecordKey]!!.records)
                                    }
                                    else {
                                        returnMap[subRecordKey] = subRecords[subRecordKey]!!
                                    }
                                }
                                if (foundTableDefinition != null) {
                                    returnMap[linkerTableRecords.tableName] = linkerTableRecords
                                }
                            }
                        }
                    }
                }

        return returnMap
    }

    internal fun handleRepeatedEnumFields(message:Message,
                                         mainMessageId:String,
                                         definitions:YaormModel.TableDefinitionGraphs):Map<String, YaormModel.TableRecords.Builder> {
        val returnRecords = HashMap<String, YaormModel.TableRecords.Builder>()
        message.allFields.keys.filter { it.type.name.equals(ProtoEnumType) && it.isRepeated }
                .forEach { fieldKey ->
                    val foundLinkerTable = definitions.tableDefinitionGraphsList.firstOrNull {
                        it.otherName.equals(fieldKey.enumType.name) &&
                                it.columnName.equals(fieldKey.name)
                    } ?: return@forEach

                    val foundItem = message.allFields[fieldKey]

                    // nothing there, nothing to insert
                    if (foundItem == null || foundItem !is List<*> || foundItem.size == 0) {
                        return@forEach
                    }
                    else {
                        val records = YaormModel.Records.newBuilder()
                        foundItem.filter { it is Descriptors.EnumValueDescriptor }.forEach {
                            val castedDescriptor = it as Descriptors.EnumValueDescriptor
                            val record = buildEnumRepeatedRecord(
                                    message.descriptorForType.name,
                                    fieldKey.enumType.name,
                                    mainMessageId,
                                    castedDescriptor)
                            records.addRecords(record)
                        }
                        val tableRecords = YaormModel.TableRecords.newBuilder()
                                .setTableDefinition(foundLinkerTable.linkerTableTable)
                                .setTableName(foundLinkerTable.linkerTableTable.name)
                                .setRecords(records)
                        returnRecords[foundLinkerTable.linkerTableTable.name] = tableRecords
                    }
                }

        return returnRecords
    }

    internal fun buildMessageRepeatedRecordTableDefinition(mainName:String,
                                                          otherName:String,
                                                          columnName:String):YaormModel.TableDefinition {
        val tableDefinition = YaormModel.TableDefinition.newBuilder()
            .setTableType(YaormModel.TableDefinition.TableType.LINKER_MESSAGE)
            .setName(buildLinkerTableNameStr(mainName, otherName, columnName))

        val actualMainColumnName = buildLinkerMessageMainTableColumnName(mainName)
        val actualOtherColumnName= buildLinkerMessageOtherTableColumnName(otherName)

        tableDefinition.addColumnDefinitions(YaormModel.ColumnDefinition.newBuilder()
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.SCALAR)
                .setType(YaormModel.ProtobufType.STRING)
                .setName(YaormUtils.IdName))
        tableDefinition.addColumnDefinitions(YaormModel.ColumnDefinition.newBuilder()
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)
                .setLinkerType(YaormModel.ColumnDefinition.LinkerType.PARENT)
                .setType(YaormModel.ProtobufType.STRING)
                .setName(actualMainColumnName))
        tableDefinition.addColumnDefinitions(YaormModel.ColumnDefinition.newBuilder()
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)
                .setLinkerType(YaormModel.ColumnDefinition.LinkerType.CHILD)
                .setType(YaormModel.ProtobufType.STRING)
                .setName(actualOtherColumnName))

        return tableDefinition.build()
    }

    internal fun buildMessageRepeatedRecord(mainColumnName:String,
                                           otherColumnName:String,
                                           mainColumnId:String,
                                           otherColumnId:String):YaormModel.Record {
        val record = YaormModel.Record.newBuilder()

        val actualMainTableName = buildLinkerMessageMainTableColumnName(mainColumnName)
        val actualOtherTableName= buildLinkerMessageOtherTableColumnName(otherColumnName)

        val idColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(YaormUtils.IdName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.SCALAR))
                .setStringHolder("$mainColumnId~$otherColumnId")

        val mainIdColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(actualMainTableName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setLinkerType(YaormModel.ColumnDefinition.LinkerType.PARENT)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY))
                .setStringHolder(mainColumnId)

        val otherIdColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(actualOtherTableName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setLinkerType(YaormModel.ColumnDefinition.LinkerType.CHILD)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY))
                .setStringHolder(otherColumnId)

        record.addColumns(idColumn)
        record.addColumns(mainIdColumn)
        record.addColumns(otherIdColumn)

        return record.build()
    }


    internal fun buildEnumRepeatedRecord(mainColumnName:String,
                                        enumName:String,
                                        mainColumnId:String,
                                        enumValueDescriptor: Descriptors.EnumValueDescriptor):YaormModel.Record {
        val record = YaormModel.Record.newBuilder()

        val idColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(YaormUtils.IdName)
                        .setType(YaormModel.ProtobufType.STRING))
                .setStringHolder("$mainColumnId~${enumValueDescriptor.name}")

        val mainIdColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(mainColumnName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY))
                .setStringHolder(mainColumnId)

        val enumColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(enumName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME))
                .setStringHolder(enumValueDescriptor.name)

        record.addColumns(idColumn)
        record.addColumns(mainIdColumn)
        record.addColumns(enumColumn)

        return record.build()
    }

    internal fun buildLinkerTableEnum(mainTableName:String, repeatedEnumName:String, repeatedEnumColumnName:String):YaormModel.TableDefinition {
        val returnDefinition = YaormModel.TableDefinition.newBuilder()
                .setTableType(YaormModel.TableDefinition.TableType.LINKER_ENUM)
                .setName("${mainTableName}_${repeatedEnumName}_$repeatedEnumColumnName")

        val idProperty = YaormModel.ColumnDefinition.newBuilder()
                .setName(YaormUtils.IdName)
                .setType(YaormModel.ProtobufType.STRING)
                .build()
        val mainTableIdProperty = YaormModel.ColumnDefinition.newBuilder()
                .setName(mainTableName)
                .setType(YaormModel.ProtobufType.STRING)
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)
                .build()
        val repeatedEnumNameProperty = YaormModel.ColumnDefinition.newBuilder()
                .setName(repeatedEnumName)
                .setType(YaormModel.ProtobufType.STRING)
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME)
                .build()

        returnDefinition.addColumnDefinitions(idProperty)
        returnDefinition.addColumnDefinitions(mainTableIdProperty)
        returnDefinition.addColumnDefinitions(repeatedEnumNameProperty)

        return returnDefinition.build()
    }

    internal fun buildLinkerTableMessage(mainTableName:String,
                                         linkerTableName:String,
                                         linkerTableColumnName:String):YaormModel.TableDefinition {
        val returnDefinition = YaormModel.TableDefinition.newBuilder()
                .setName(buildLinkerTableNameStr(mainTableName, linkerTableName, linkerTableColumnName))
                .setTableType(YaormModel.TableDefinition.TableType.LINKER_MESSAGE)

        val mainTableColumnName = buildLinkerMessageMainTableColumnName(mainTableName)
        val otherTableColumnName = buildLinkerMessageOtherTableColumnName(linkerTableName)

        val idProperty = YaormModel.ColumnDefinition.newBuilder()
                .setName(YaormUtils.IdName)
                .setType(YaormModel.ProtobufType.STRING)
                .build()
        val mainTableIdProperty = YaormModel.ColumnDefinition.newBuilder()
                .setName(mainTableColumnName)
                .setType(YaormModel.ProtobufType.STRING)
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)
                .build()
        val otherTableIdProperty = YaormModel.ColumnDefinition.newBuilder()
                .setName(otherTableColumnName)
                .setType(YaormModel.ProtobufType.STRING)
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)
                .build()

        returnDefinition.addColumnDefinitions(idProperty)
        returnDefinition.addColumnDefinitions(mainTableIdProperty)
        returnDefinition.addColumnDefinitions(otherTableIdProperty)

        return returnDefinition.build()
    }

    internal fun buildMessageColumnName(name: String, order:Int):YaormModel.ColumnDefinition {
        return YaormModel.ColumnDefinition.newBuilder()
                .setName(name)
                .setType(YaormModel.ProtobufType.STRING)
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)
                .setOrder(order)
                .build()
    }

    internal fun buildEnumNameColumnName(name:String, order:Int):YaormModel.ColumnDefinition {
        return YaormModel.ColumnDefinition.newBuilder()
                .setName(name)
                .setType(YaormModel.ProtobufType.STRING)
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME)
                .setOrder(order)
                .build()
    }

    internal fun buildLinkerTableNameStr(first:String, second:String, columnName:String):String {
        return "${first}_${second}_$columnName"
    }

    internal fun buildLinkerMessageMainTableColumnName(tableName:String):String {
        return "$tableName$MainSuffix"
    }

    internal fun buildLinkerMessageOtherTableColumnName(tableName: String):String {
        return "$tableName$OtherSuffix"
    }

    internal fun buildUniqueKey(builder: Message, entityId:String):String {
        return "${builder.descriptorForType.name}~$entityId"
    }

    const val MainSuffix = "_main"
    const val OtherSuffix = "_other"
}
