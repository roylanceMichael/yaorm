package org.roylance.yaorm.services.proto

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.utilities.ConvertRecordsToProtobuf
import org.roylance.yaorm.utilities.YaormUtils
import org.roylance.yaorm.utilities.ProtobufUtils
import java.util.*

class EntityMessageService(
        private val protoGeneratedMessageBuilder: IProtoGeneratedMessageBuilder,
        private val entityService: IEntityProtoService,
        private val customIndexes: HashMap<String, YaormModel.Index>):IEntityMessageService {

    private val definitions = HashMap<String, YaormModel.TableDefinitionGraphs>()

    override fun <T : Message> bulkInsert(messages: List<T>): Boolean {
        if (messages.size == 0) {
            return false
        }

        val firstMessage = messages.first()
        if (!ProtobufUtils.isMessageOk(firstMessage)) {
            return false
        }

        // build records
        val recordsMap = HashMap<String, YaormModel.Records.Builder>()
        val recordsDefinitions = HashMap<String, YaormModel.TableDefinition>()
        messages.forEach { message ->
            val records = ProtobufUtils.convertProtobufObjectToRecords(message, this.definitions, this.customIndexes)

            records.tableRecordsList.forEach { tableRecord ->
                if (recordsMap.containsKey(tableRecord.tableName)) {
                    recordsMap[tableRecord.tableName]!!.addAllRecords(tableRecord.records.recordsList)
                }
                else {
                    recordsMap[tableRecord.tableName] = tableRecord.records.toBuilder()
                    recordsDefinitions[tableRecord.tableName] = tableRecord.tableDefinition
                }
            }
        }

        // todo: build a dag of dependencies and execute in order
        recordsMap.keys.forEach { recordsKey ->
            val uniqueRecords = HashMap<String, YaormModel.Record>()
            recordsMap[recordsKey]!!.recordsList.forEach { record ->
                val idColumn = YaormUtils.getIdColumn(record.columnsList)
                if (idColumn != null) {
                    uniqueRecords[idColumn.stringHolder] = record
                }
            }

            val actualInsertRecords = YaormModel.Records.newBuilder().addAllRecords(uniqueRecords.values).build()
            this.entityService.bulkInsert(actualInsertRecords,
                    recordsDefinitions[recordsKey]!!)
        }

        return true
    }

    override fun <T : Message> getCustomSingleLevel(messageType: T, customSql: String): List<T> {
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(messageType.descriptorForType, this.customIndexes)
                ?: return ArrayList()

        val builder = this.protoGeneratedMessageBuilder.buildGeneratedMessage(messageType.descriptorForType.name)
        val records = this.entityService.getCustom(customSql, definition)
        return records.recordsList.map {
            val newBuilder = builder.newBuilderForType()
            ConvertRecordsToProtobuf.build(newBuilder, it)
            newBuilder.build() as T
        }
    }

    override fun <T : Message> getCustomSingleLevelStream(messageType: T, customSql: String, stream: IMessageStreamer) {
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(messageType.descriptorForType, this.customIndexes)
                ?: return

        val builder = this.protoGeneratedMessageBuilder.buildGeneratedMessage(messageType.descriptorForType.name)
        val records = this.entityService.getCustom(customSql, definition)
        records.recordsList.forEach {
            val newBuilder = builder.newBuilderForType()
            ConvertRecordsToProtobuf.build(newBuilder, it)
            stream.stream(newBuilder.build())
        }
    }

    override fun createEntireSchema(fileDescriptor: Descriptors.FileDescriptor): Boolean {
        CreateSchema(this, this.entityService, this.customIndexes).handleFile(fileDescriptor, false)
        return true
    }

    override fun dropAndCreateEntireSchema(fileDescriptor: Descriptors.FileDescriptor): Boolean {
        CreateSchema(this, this.entityService, this.customIndexes).handleFile(fileDescriptor, true)
        return true
    }

    override fun <T : Message> createEntireSchema(messageType: T): Boolean {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return false
        }
        CreateSchema(this, this.entityService, customIndexes).handleMessage(messageType.descriptorForType, false)
        return true
    }

    override fun <T : Message> dropAndCreateEntireSchema(messageType: T): Boolean {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return false
        }
        CreateSchema(this, this.entityService, customIndexes).handleMessage(messageType.descriptorForType, true)
        return true
    }

    override fun <T : Message> getKeysStream(messageType: T, streamer: IMessageStreamer) {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return
        }

        val idField = messageType.descriptorForType.fields.first { it.name.equals(YaormUtils.IdName) }
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.getIdsStream(tableDefinition, object: IProtoStreamer {
            override fun stream(record: YaormModel.Record) {
                val builder = protoGeneratedMessageBuilder.buildGeneratedMessage(messageType.descriptorForType.name).toBuilder()
                builder.setField(idField, record.columnsList.first { it.definition.name.equals(YaormUtils.IdName) }!!.stringHolder)
                streamer.stream(builder.build())
            }
        })
    }

    override fun <T : Message> getKeys(messageType: T): List<String> {
        val returnList = ArrayList<String>()
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return returnList
        }

        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        return this.entityService.getIds(tableDefinition)
    }

    override fun <T : Message> merge(message: T): Boolean {
        if (!ProtobufUtils.isMessageOk(message)) {
            return false
        }

        // build records
        val records = ProtobufUtils.convertProtobufObjectToRecords(message, this.definitions, this.customIndexes)

        // create the main one
        if (!records.tableRecordsList.any { message.descriptorForType.name.equals(it.tableName) }) {
            return false
        }

        val mainRecords = records.tableRecordsList.first { it.tableName.equals(message.descriptorForType.name) }!!
        if (!mainRecords.hasRecords()) {
            return false
        }

        // todo - order this as a dag, also create relational constraints between the tables
        // go through children now, this is the source of truth
        records.tableRecordsList
                .forEach { tableRecords ->
                    val recordsFromClientMap = HashMap<String, YaormModel.Record>()
                    val recordsFromDatabaseMap:HashMap<String, YaormModel.Record>

                    // where clause with all the ids
                    tableRecords.records.recordsList.forEach {
                        val idColumn = it.columnsList.firstOrNull { it.definition.name.equals(YaormUtils.IdName) }
                        if (idColumn != null) {
                            recordsFromClientMap[idColumn.stringHolder] = it
                        }
                    }

                    // if this is a linker table, then we want to compare all the items with parent vs what we have
                    if (tableRecords.tableDefinition.tableType.equals(YaormModel.TableDefinition.TableType.NORMAL)) {
                        recordsFromDatabaseMap = this.handleNormalCase(tableRecords)
                    }
                    else {
                        recordsFromDatabaseMap = this.handleLinkerMessageCase(tableRecords)
                    }

                    // merge here
                    recordsFromClientMap.keys.forEach {
                        entityService.createOrUpdate(recordsFromClientMap[it]!!, tableRecords.tableDefinition)
                    }

                    recordsFromDatabaseMap.keys.forEach {
                        val idColumn = recordsFromDatabaseMap[it]!!.columnsList.firstOrNull { it.definition.name.equals(YaormUtils.IdName) }
                        if (!recordsFromClientMap.containsKey(it) && idColumn != null) {
                            // delete
                            entityService.delete(
                                    idColumn.stringHolder,
                                    tableRecords.tableDefinition)
                        }
                    }
                }

        return true
    }

    override fun <T : Message> delete(message: T): Boolean {
        if (!ProtobufUtils.isMessageOk(message)) {
            return false
        }

        val records = ProtobufUtils.convertProtobufObjectToRecords(message, this.definitions, this.customIndexes)
        // todo - order this as a dag, also create relational constraints between the tables
        // go through children now, this is the source of truth
        records.tableRecordsList
                .forEach { tableRecords ->
                    val recordsFromClientMap = HashMap<String, YaormModel.Record>()
                    val recordsFromDatabaseMap:HashMap<String, YaormModel.Record>

                    // where clause with all the ids
                    tableRecords.records.recordsList.forEach {
                        val idColumn = it.columnsList.firstOrNull { it.definition.name.equals(YaormUtils.IdName) }
                        if (idColumn != null) {
                            recordsFromClientMap[idColumn.stringHolder] = it
                        }
                    }

                    // if this is a linker table, then we want to compare all the items with parent vs what we have
                    if (tableRecords.tableDefinition.tableType.equals(YaormModel.TableDefinition.TableType.NORMAL)) {
                        recordsFromDatabaseMap = this.handleNormalCase(tableRecords)
                    }
                    else {
                        recordsFromDatabaseMap = this.handleLinkerMessageCase(tableRecords)
                    }

                    recordsFromDatabaseMap.keys.forEach {
                        val idColumn = YaormUtils.getIdColumn(recordsFromDatabaseMap[it]!!.columnsList)
                        if (idColumn != null) {
                            // delete
                            entityService.delete(
                                    idColumn.stringHolder,
                                    tableRecords.tableDefinition)
                        }
                    }
                }

        return true
    }

    override fun <T : Message> get(messageType:T, id: String): T? {
        return ProtobufUtils.getProtoObjectFromBuilderSingle(messageType,
                this.entityService,
                id,
                this.protoGeneratedMessageBuilder,
                definitions,
                this.customIndexes)
    }

    override fun <T : Message> getMany(messageType: T, limit: Int, offset: Int): List<T> {
        val returnList = ArrayList<T>()
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return returnList
        }

        // get ids first
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.getMany(tableDefinition, limit, offset).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        if (completedMessage != null) {
                            returnList.add(completedMessage)
                        }
                    }
        }

        // get messageType and all children
        return returnList
    }

    override fun <T : Message> getManyStream(messageType: T, streamer: IMessageStreamer, limit:Int, offset: Int) {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return
        }
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.getMany(definition = tableDefinition, limit = limit, offset = offset).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        if (completedMessage != null) {
                            streamer.stream(completedMessage)
                        }
                    }
                }
    }

    override fun <T : Message> where(messageType: T, whereClause: YaormModel.WhereClause): List<T> {
        val returnList = ArrayList<T>()
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return returnList
        }
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.where(whereClause, tableDefinition).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        if (completedMessage != null) {
                            returnList.add(completedMessage)
                        }
                    }
                }

        return returnList
    }

    override fun <T : Message> whereStream(messageType: T,
                                           whereClause: YaormModel.WhereClause,
                                           streamer: IMessageStreamer) {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return
        }
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.where(whereClause, tableDefinition).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        if (completedMessage != null) {
                            streamer.stream(completedMessage)
                        }
                    }
                }
    }

    override fun <T : Message> getCount(messageType: T): Long {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return -1L
        }
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        return this.entityService.getCount(tableDefinition)
    }

    private fun handleLinkerMessageCase(tableRecords: YaormModel.TableRecords):HashMap<String, YaormModel.Record> {
        // where clause with all the main parent items
        val recordsFromDatabaseMap = HashMap<String, YaormModel.Record>()
        val parentColumn = tableRecords.tableDefinition.columnDefinitionsList
                .firstOrNull { it.name.endsWith(ProtobufUtils.MainSuffix) } ?: return recordsFromDatabaseMap

        var customWhereClauseLinker = YaormModel.WhereClause.newBuilder()
                .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                .setConnectingAndOr(YaormModel.WhereClause.ConnectingAndOr.OR)

        var firstWhereClause:YaormModel.WhereClause.Builder? = null

        tableRecords.knownParentIdsList.forEach { id ->
            customWhereClauseLinker.nameAndProperty = YaormModel.Column.newBuilder()
                .setDefinition(parentColumn)
                .setStringHolder(id)
                .build()

            val newWhereClause = YaormModel.WhereClause.newBuilder()
                    .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                    .setConnectingWhereClause(customWhereClauseLinker)
                    .setConnectingAndOr(YaormModel.WhereClause.ConnectingAndOr.OR)

            if (firstWhereClause == null) {
                firstWhereClause = customWhereClauseLinker
            }

            customWhereClauseLinker = newWhereClause
        }

        if (firstWhereClause == null) {
            return recordsFromDatabaseMap
        }

        entityService.where(firstWhereClause!!.build(), tableRecords.tableDefinition)
                .recordsList.forEach {
            val idColumn = YaormUtils.getIdColumn(it.columnsList)
            if (idColumn != null) {
                recordsFromDatabaseMap[idColumn.stringHolder] = it
            }
        }
        return recordsFromDatabaseMap
    }

    private fun handleNormalCase(tableRecords:YaormModel.TableRecords):HashMap<String, YaormModel.Record> {
        // if this is a normal table, we want to compare id with what we have
        val recordsFromDatabaseMap = HashMap<String, YaormModel.Record>()
        var customWhereClause = YaormModel.WhereClause.newBuilder()
                .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                .setConnectingAndOr(YaormModel.WhereClause.ConnectingAndOr.OR)

        var firstWhereClause:YaormModel.WhereClause.Builder? = null
        tableRecords.records.recordsList.forEach { record ->
            val idColumn = YaormUtils.getIdColumn(record.columnsList)
            if (idColumn != null) {
                customWhereClause.nameAndProperty = idColumn
                val newWhereClause = YaormModel.WhereClause.newBuilder()
                        .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                        .setConnectingWhereClause(customWhereClause)
                        .setConnectingAndOr(YaormModel.WhereClause.ConnectingAndOr.OR)

                if (firstWhereClause == null) {
                    firstWhereClause = customWhereClause
                }

                customWhereClause = newWhereClause
            }
        }

        if (firstWhereClause == null) {
            return recordsFromDatabaseMap
        }

        entityService.where(firstWhereClause!!.build(), tableRecords.tableDefinition)
                .recordsList.forEach {
            val idColumn = YaormUtils.getIdColumn(it.columnsList)
            if (idColumn != null) {
                recordsFromDatabaseMap[idColumn.stringHolder] = it
            }
        }
        return recordsFromDatabaseMap
    }

    private fun buildTableDefinitionWithOnlyId(messageType:Message):YaormModel.TableDefinition{
        val tableDefinition = YaormModel.TableDefinition.newBuilder()
                .setName(messageType.descriptorForType.name)
        tableDefinition.addColumnDefinitions(YaormModel.ColumnDefinition.newBuilder()
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.SCALAR)
                .setIsKey(true)
                .setName(YaormUtils.IdName)
                .setType(YaormModel.ProtobufType.STRING)
                .build())
        return tableDefinition.build()
    }

    private class CreateSchema(
            private val entityMessageService: EntityMessageService,
            private val entityService: IEntityProtoService,
            private val customIndexes: HashMap<String, YaormModel.Index>) {
        private val seenTables = HashSet<String>()

        fun handleMessage(descriptor: Descriptors.Descriptor, shouldDelete: Boolean) {
            val foundDefinitions = ProtobufUtils.buildDefinitionGraph(descriptor, this.customIndexes)
            entityMessageService.definitions[descriptor.name] = foundDefinitions

            if (!seenTables.contains(foundDefinitions.mainTableDefinition.name) &&
                    YaormUtils.checkIfOk(foundDefinitions.mainTableDefinition)) {
                if (shouldDelete) {
                    this.entityService.dropTable(foundDefinitions.mainTableDefinition)
                }
                this.entityService.createTable(foundDefinitions.mainTableDefinition)

                if (foundDefinitions.mainTableDefinition.hasIndex()) {
                    this.entityService.createIndex(
                            foundDefinitions.mainTableDefinition.index,
                            foundDefinitions.mainTableDefinition)
                }
                seenTables.add(foundDefinitions.mainTableDefinition.name)
            }

            foundDefinitions.tableDefinitionGraphsList.forEach { graph ->
                if (!seenTables.contains(graph.mainName) &&
                    YaormUtils.checkIfOk(graph.mainTableDefinition)) {
                    if (shouldDelete) {
                        this.entityService.dropTable(graph.mainTableDefinition)
                    }
                    this.entityService.createTable(graph.mainTableDefinition)
                    if (graph.mainTableDefinition.hasIndex()) {
                        this.entityService.createIndex(graph.mainTableDefinition.index,
                                graph.mainTableDefinition)
                    }
                    seenTables.add(graph.mainName)
                }
                if (graph.hasLinkerTableTable() &&
                        YaormUtils.checkIfOk(graph.linkerTableTable) &&
                        !seenTables.contains(graph.linkerTableTable.name)) {
                    if (shouldDelete) {
                        this.entityService.dropTable(graph.linkerTableTable)
                    }
                    this.entityService.createTable(graph.linkerTableTable)
                    if (graph.linkerTableTable.hasIndex()) {
                        this.entityService.createIndex(graph.linkerTableTable.index,
                                graph.linkerTableTable)
                    }
                    seenTables.add(graph.linkerTableTable.name)
                }
                if (graph.hasOtherTableDefinition() &&
                        YaormUtils.checkIfOk(graph.otherTableDefinition) &&
                        !seenTables.contains(graph.otherName)) {
                    if (shouldDelete) {
                        this.entityService.dropTable(graph.otherTableDefinition)
                    }
                    this.entityService.createTable(graph.otherTableDefinition)
                    if (graph.otherTableDefinition.hasIndex()) {
                        this.entityService.createIndex(graph.otherTableDefinition.index,
                                graph.otherTableDefinition)
                    }
                    seenTables.add(graph.otherName)
                }
            }
        }

        fun handleFile(fileDescriptor: Descriptors.FileDescriptor, shouldDelete: Boolean) {
            fileDescriptor.messageTypes.forEach { messageDescriptor ->
                handleMessage(messageDescriptor, shouldDelete)
            }
        }
    }
}
