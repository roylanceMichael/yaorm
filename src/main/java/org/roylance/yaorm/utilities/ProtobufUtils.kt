package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel
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

    fun buildIdOnlyTableDefinition(descriptor: Descriptors.Descriptor):YaormModel.TableDefinition {
        val idColumnDefinition = YaormModel.ColumnDefinition.newBuilder()
                .setName(CommonUtils.IdName)
                .setType(YaormModel.ProtobufType.STRING)

        return YaormModel.TableDefinition.newBuilder().setName(descriptor.name).addColumnDefinitions(idColumnDefinition).build()
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

                    val childDefinitions = buildDefinitionGraph(it.messageType)
                    returnGraph.addAllTableDefinitionGraphs(childDefinitions.tableDefinitionGraphsList)
                }
            }

        return returnGraph.build()
    }

    fun <T:Message> getProtoObjectFromBuilderSingle(builder: T, entityService: IEntityProtoService, entityId:String, generatedMessageBuilder: IProtoGeneratedMessageBuilder): T {
        val tableDefinitionGraph = this.buildDefinitionGraph(builder.descriptorForType)
        val builderForType = builder.newBuilderForType()

        // main fields
        val foundRecord = entityService.get(entityId, tableDefinitionGraph.mainTableDefinition) ?: return builderForType.build() as T
        builder.descriptorForType.fields.filter { !it.isRepeated }.forEach { fieldKey ->
            val foundColumn = foundRecord.columnsList.firstOrNull { it.definition.name.equals(fieldKey.name) } ?: return@forEach

            if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.SCALAR)) {
                builderForType.setField(fieldKey, CommonUtils.getAnyObject(foundColumn))
            }
            else if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME)) {
                builderForType.setField(fieldKey, fieldKey.enumType.findValueByName(foundColumn.stringHolder.toUpperCase()))
            }
            else if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)) {
                // recursively get the child object
                val childObject = generatedMessageBuilder.buildGeneratedMessage(fieldKey.messageType.name)

                if (!isMessageOk(childObject)) {
                    return@forEach
                }

                val childId = foundColumn.stringHolder
                val reconciledObject = getProtoObjectFromBuilderSingle<Message>(childObject, entityService, childId, generatedMessageBuilder)
                builderForType.setField(fieldKey, reconciledObject)
            }
        }

        // repeated enums
        builder.descriptorForType.fields.filter { it.type.name.equals(ProtoEnumType) && it.isRepeated }.forEach { fieldKey ->
            val definitionForLinkerTable = tableDefinitionGraph.tableDefinitionGraphsList
                    .firstOrNull { it.columnName.equals(fieldKey.name) && it.otherName.equals(fieldKey.enumType.name) } ?: return@forEach

            val customWhereClause = YaormModel.WhereClause.newBuilder()
                .setNameAndProperty(YaormModel.Column.newBuilder()
                        .setDefinition(YaormModel.ColumnDefinition.newBuilder().setName(builder.descriptorForType.name).setType(YaormModel.ProtobufType.STRING))
                    .setStringHolder(entityId))
                    .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                    .build()

            val foundRecords = entityService.where(customWhereClause, definitionForLinkerTable.linkerTableTable)
            foundRecords.recordsList.forEach { record ->
                val nameColumn = record.columnsList.firstOrNull { it.definition.name.equals(fieldKey.enumType.name) }
                if (nameColumn != null) {
                    val enumToAdd = fieldKey.enumType.findValueByName(nameColumn.stringHolder.toUpperCase())
                    builderForType.addRepeatedField(fieldKey, enumToAdd)
                }
            }
        }

        // repeated messages
        builder.descriptorForType.fields.filter { it.type.name.equals(ProtoMessageType) && it.isRepeated }.forEach { fieldKey ->
            val definitionForLinkerTable = tableDefinitionGraph.tableDefinitionGraphsList
                    .firstOrNull { it.columnName.equals(fieldKey.name) && it.otherName.equals(fieldKey.messageType.name) } ?: return@forEach

            val customWhereClause = YaormModel.WhereClause.newBuilder()
                    .setNameAndProperty(YaormModel.Column.newBuilder()
                            .setDefinition(YaormModel.ColumnDefinition.newBuilder().setName(builder.descriptorForType.name).setType(YaormModel.ProtobufType.STRING))
                            .setStringHolder(entityId))
                    .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                    .build()

            val childBuilder = generatedMessageBuilder.buildGeneratedMessage(fieldKey.messageType.name)

            val foundRecords = entityService.where(customWhereClause, definitionForLinkerTable.linkerTableTable)
            foundRecords.recordsList.forEach { record ->
                val nameColumn = record.columnsList.firstOrNull { it.definition.name.equals(fieldKey.messageType.name) }
                if (nameColumn != null) {
                    val constructedMessage = getProtoObjectFromBuilderSingle(childBuilder, entityService, nameColumn.stringHolder, generatedMessageBuilder)
                    builderForType.addRepeatedField(fieldKey, constructedMessage)
                }
            }
        }

        return builderForType.build() as T
    }

    fun convertProtobufObjectToRecords(message:Message):YaormModel.AllTableRecords {
        val resultMap = convertProtobufObjectToRecordsInternal(message)
        val returnRecords = YaormModel.AllTableRecords.newBuilder()

        resultMap.keys.forEach {
            returnRecords.addTableRecords(resultMap[it]!!)
        }

        return returnRecords.build()
    }

    private fun convertProtobufObjectToRecordsInternal(message:Message):MutableMap<String, YaormModel.TableRecords.Builder> {
        if (!isMessageOk(message)) {
            return HashMap()
        }

        val mainMessageId = getIdFromMessage(message)
        val recordsMap = HashMap<String, YaormModel.TableRecords.Builder>()
        val definitions = buildDefinitionGraph(message.descriptorForType)
        // add in all the definitions we found
        definitions.tableDefinitionGraphsList.forEach {
            if (!recordsMap.containsKey(it.mainName)) {
                recordsMap[it.mainName] = YaormModel.TableRecords.newBuilder().setTableName(it.mainName).setTableDefinition(it.mainTableDefinition)
            }
            if (it.hasLinkerTableTable() && !recordsMap.containsKey(it.linkerTableTable.name)) {
                recordsMap[it.linkerTableTable.name] = YaormModel.TableRecords.newBuilder().setTableName(it.linkerTableTable.name).setTableDefinition(it.linkerTableTable)
            }
            if (it.hasOtherTableDefinition() && !recordsMap.containsKey(it.otherName)) {
                recordsMap[it.otherName] = YaormModel.TableRecords.newBuilder().setTableName(it.otherName).setTableDefinition(it.otherTableDefinition)
            }
        }

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

        // now messages, we'll add a column for the foreign key, but then new records for the child object
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
                    if (foundField is Message && isMessageOk(foundField)) {
                        val generatedNameColumn = CommonUtils.buildColumn(getIdFromMessage(foundField), columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                        val childMessageRecords = convertProtobufObjectToRecordsInternal(foundField)
                        childMessageRecords.keys.forEach {
                            if (recordsMap.containsKey(it)) {
                                recordsMap[it]!!.mergeRecords(childMessageRecords[it]!!.records)
                            }
                            else {
                                recordsMap[it] = childMessageRecords[it]!!
                            }
                        }
                    }
                    else {
                        val generatedNameColumn = CommonUtils.buildColumn(Empty, columnDefinition)
                        baseRecord.addColumns(generatedNameColumn)
                    }
                }

        // add base
        recordsMap[definitions.mainTableDefinition.name] = YaormModel.TableRecords.newBuilder()
                .setTableDefinition(definitions.mainTableDefinition)
                .setTableName(definitions.mainTableDefinition.name)
                .setRecords(YaormModel.Records.newBuilder().addRecords(baseRecord))

        // let's add the enum children now
        val repeatedEnumMap = handleRepeatedEnumFields(message, mainMessageId, definitions)
        repeatedEnumMap.keys.forEach {
            if (recordsMap.containsKey(it)) {
                recordsMap[it]!!.mergeRecords(repeatedEnumMap[it]!!.records)
            }
            else {
                recordsMap[it] = repeatedEnumMap[it]!!
            }
        }

        // add in message children now
        val repeatedMessageMap = handleRepeatedMessageFields(message, mainMessageId)
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

    fun getIdFromMessage(message:Message):String {
        val foundIdField = message.allFields.keys.firstOrNull { it.name.equals(CommonUtils.IdName) } ?: Empty
        return message.allFields[foundIdField]!!.toString()
    }

    fun isMessageOk(message: Message):Boolean {
        return message.descriptorForType.fields.any { it.name.equals(CommonUtils.IdName) }
    }

    private fun handleRepeatedMessageFields(message:Message, mainMessageId: String): Map<String, YaormModel.TableRecords.Builder> {
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
                                val subRecords = convertProtobufObjectToRecordsInternal(subMessage)
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

    private fun handleRepeatedEnumFields(message:Message,
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

    private fun buildMessageRepeatedRecordTableDefinition(mainName:String,
                                                          otherName:String,
                                                          columnName:String):YaormModel.TableDefinition {
        return YaormModel.TableDefinition.newBuilder()
            .setTableType(YaormModel.TableDefinition.TableType.LINKER_MESSAGE)
            .setName(buildLinkerTableNameStr(mainName, otherName, columnName))
            .addColumnDefinitions(YaormModel.ColumnDefinition.newBuilder().setColumnType(YaormModel.ColumnDefinition.ColumnType.SCALAR).setType(YaormModel.ProtobufType.STRING).setName(CommonUtils.IdName))
            .addColumnDefinitions(YaormModel.ColumnDefinition.newBuilder().setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY).setLinkerType(YaormModel.ColumnDefinition.LinkerType.PARENT).setType(YaormModel.ProtobufType.STRING).setName(mainName))
            .addColumnDefinitions(YaormModel.ColumnDefinition.newBuilder().setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY).setLinkerType(YaormModel.ColumnDefinition.LinkerType.CHILD).setType(YaormModel.ProtobufType.STRING).setName(otherName))
            .build()
    }

    private fun buildMessageRepeatedRecord(mainColumnName:String,
                                           otherColumnName:String,
                                           mainColumnId:String,
                                           otherColumnId:String):YaormModel.Record {
        val record = YaormModel.Record.newBuilder()

        val idColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(CommonUtils.IdName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.SCALAR))
                .setStringHolder("$mainColumnId~$otherColumnId")

        val mainIdColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(mainColumnName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setLinkerType(YaormModel.ColumnDefinition.LinkerType.PARENT)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY))
                .setStringHolder(mainColumnId)

        val otherIdColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(otherColumnName)
                        .setType(YaormModel.ProtobufType.STRING)
                        .setLinkerType(YaormModel.ColumnDefinition.LinkerType.CHILD)
                        .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY))
                .setStringHolder(otherColumnId)

        record.addColumns(idColumn)
        record.addColumns(mainIdColumn)
        record.addColumns(otherIdColumn)

        return record.build()
    }


    private fun buildEnumRepeatedRecord(mainColumnName:String,
                                        enumName:String,
                                        mainColumnId:String,
                                        enumValueDescriptor: Descriptors.EnumValueDescriptor):YaormModel.Record {
        val record = YaormModel.Record.newBuilder()

        val idColumn = YaormModel.Column.newBuilder()
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setName(CommonUtils.IdName)
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

    private fun buildLinkerTableEnum(mainTableName:String, repeatedEnumName:String, repeatedEnumColumnName:String):YaormModel.TableDefinition {
        val returnDefinition = YaormModel.TableDefinition.newBuilder()
                .setTableType(YaormModel.TableDefinition.TableType.LINKER_ENUM)
                .setName("${mainTableName}_${repeatedEnumName}_$repeatedEnumColumnName")

        val idProperty = YaormModel.ColumnDefinition.newBuilder().setName(CommonUtils.IdName).setType(YaormModel.ProtobufType.STRING).build()
        val mainTableIdProperty = YaormModel.ColumnDefinition.newBuilder().setName(mainTableName).setType(YaormModel.ProtobufType.STRING).setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY).build()
        val repeatedEnumNameProperty = YaormModel.ColumnDefinition.newBuilder().setName(repeatedEnumName).setType(YaormModel.ProtobufType.STRING).setColumnType(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME).build()
        return returnDefinition.addColumnDefinitions(idProperty)
                .addColumnDefinitions(mainTableIdProperty)
                .addColumnDefinitions(repeatedEnumNameProperty)
                .build()
    }

    private fun buildLinkerTableMessage(mainTableName:String, linkerTableName:String, linkerTableColumnName:String):YaormModel.TableDefinition {
        val returnDefinition = YaormModel.TableDefinition.newBuilder()
                .setName(buildLinkerTableNameStr(mainTableName, linkerTableName, linkerTableColumnName))
                .setTableType(YaormModel.TableDefinition.TableType.LINKER_MESSAGE)

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