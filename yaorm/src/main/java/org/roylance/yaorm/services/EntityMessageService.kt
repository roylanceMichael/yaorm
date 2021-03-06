package org.roylance.yaorm.services

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.utilities.ConvertRecordsToProtobuf
import org.roylance.yaorm.utilities.GetProtoObjects
import org.roylance.yaorm.utilities.YaormUtils
import org.roylance.yaorm.utilities.ProtobufUtils
import java.util.*

@Suppress("UNCHECKED_CAST")
class EntityMessageService(
        override val entityService: IEntityService,
        private val customIndexes: HashMap<String, YaormModel.Index>): IEntityMessageService {
    override fun getReport(): YaormModel.DatabaseExecutionReport {
        return entityService.getReport()
    }

    private val definitions = HashMap<String, YaormModel.TableDefinitionGraphs>()

    override fun <T : Message> getMany(messageType: T, ids: List<String>): List<T> {
        val returnList = ArrayList<T>()
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return returnList
        }

        val objects = GetProtoObjects(
                this.entityService,
                this.definitions,
                this.customIndexes)

        return objects.build(messageType, ids)
    }

    override fun <T : Message> getManySingleLevel(messageType: T, limit:Int, offset:Int): List<T> {
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(messageType.descriptorForType, this.customIndexes)
                ?: return ArrayList()

        val records = this.entityService.getMany(definition, limit, offset)
        return records.recordsList.map {
            val newBuilder = messageType.toBuilder()
            ConvertRecordsToProtobuf.build(newBuilder, it)
            newBuilder.build() as T
        }
    }

    override fun <T : Message> getManySingleLevel(messageType: T, ids: List<String>): List<T> {
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(messageType.descriptorForType, this.customIndexes)
                ?: return ArrayList()

        val whereClause = YaormModel.WhereClause.newBuilder()
                .setNameAndProperty(YaormModel.Column.newBuilder()
                        .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                                .setName(YaormUtils.IdName)
                                .setType(YaormModel.ProtobufType.STRING)))
                .addAllInItems(ids)
                .build()

        val records = this.entityService.where(whereClause, definition)
        return records.recordsList.map {
            val newBuilder = messageType.toBuilder()
            ConvertRecordsToProtobuf.build(newBuilder, it)
            newBuilder.build() as T
        }
    }

    override fun <T : Message> mergeTable(messages: List<T>, message: T): Boolean {
        if (!ProtobufUtils.isMessageOk(message)) {
            return false
        }

        val existingMessagesHash = this.getKeys(message).toHashSet()
        val currentMessagesHash = HashSet<String>()
        messages.forEach {
            val id = ProtobufUtils.getIdFromMessage(it)
            currentMessagesHash.add(id)
            this.merge(it)
        }

        existingMessagesHash
                .filter { !currentMessagesHash.contains(it) }
                .forEach { key ->
                    val foundMessage = this.get(message, key)
                    this.delete(foundMessage as Message)
                }

        return true
    }

    override fun <T : Message> bulkInsert(messages: List<T>): Boolean {
        if (messages.isEmpty()) {
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
            this.entityService.bulkInsert(actualInsertRecords, recordsDefinitions[recordsKey]!!)
        }

        return true
    }

    override fun <T : Message> getCustomSingleLevel(messageType: T, customSql: String): List<T> {
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(messageType.descriptorForType, this.customIndexes)
                ?: return ArrayList()

        val records = this.entityService.getCustom(customSql, definition)
        return records.recordsList.map {
            val newBuilder = messageType.toBuilder()
            ConvertRecordsToProtobuf.build(newBuilder, it)
            newBuilder.build() as T
        }
    }

    override fun <T : Message> getCustomSingleLevelStream(messageType: T, customSql: String, stream: IMessageStreamer) {
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(messageType.descriptorForType, this.customIndexes)
                ?: return

        val protoStream = object: IStreamer {
            override fun stream(record: YaormModel.Record) {
                val newBuilder = messageType.toBuilder()
                ConvertRecordsToProtobuf.build(newBuilder, record)
                stream.stream(newBuilder.build())
            }
        }

        this.entityService.getCustomStream(customSql, definition, protoStream)
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

        val idField = messageType.descriptorForType.fields.first { it.name == YaormUtils.IdName }
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.getIdsStream(tableDefinition, object: IStreamer {
            override fun stream(record: YaormModel.Record) {
                val builder = messageType.toBuilder()
                builder.setField(idField, record.columnsList.first { it.definition.name == YaormUtils.IdName }!!.stringHolder)
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
        if (!records.tableRecordsList.any { message.descriptorForType.name == it.tableName }) {
            return false
        }

        val mainRecords = records.tableRecordsList.first { it.tableName == message.descriptorForType.name }
        if (!mainRecords.hasRecords()) {
            return false
        }

        val keyFromMessage = ProtobufUtils.getIdFromMessage(message)
        val existingDatabaseAllRecords: YaormModel.AllTableRecords
        val existingRecord = this.get(message, keyFromMessage)
        if (existingRecord == null) {
            existingDatabaseAllRecords = YaormModel.AllTableRecords.getDefaultInstance()
        }
        else {
            existingDatabaseAllRecords = ProtobufUtils.convertProtobufObjectToRecords(existingRecord, this.definitions, this.customIndexes)
        }

        // todo - order this as a dag, also create relational constraints between the tables
        // go through children now, this is the source of truth
        records.tableRecordsList
                .forEach { tableRecords ->
                    val recordsFromClientMap = HashMap<String, YaormModel.Record>()
                    val recordsFromDatabaseMap = HashMap<String, YaormModel.Record>()

                    // where clause with all the ids
                    tableRecords.records.recordsList.forEach {
                        val idColumn = it.columnsList.firstOrNull { it.definition.name == YaormUtils.IdName }
                        if (idColumn != null) {
                            recordsFromClientMap[idColumn.stringHolder] = it
                        }
                    }

                    val existingTableRecords = existingDatabaseAllRecords.tableRecordsList.firstOrNull { it.tableName == tableRecords.tableName }
                    if (existingTableRecords != null) {
                        existingTableRecords.records.recordsList.forEach {
                            val idColumn = YaormUtils.getIdColumn(it.columnsList)
                            if (idColumn != null) {
                                recordsFromDatabaseMap[idColumn.stringHolder] = it
                            }
                        }
                    }

                    // merge here
                    val tempRecordsToInsert = YaormModel.Records.newBuilder()
                    if (this.entityService.insertSameAsUpdate) {
                        tempRecordsToInsert.addAllRecords(tableRecords.records.recordsList)
                    }
                    else {
                        recordsFromClientMap.keys.forEach {
                            if (recordsFromDatabaseMap.containsKey(it)) {
                                entityService.update(recordsFromClientMap[it]!!, tableRecords.tableDefinition)
                            }
                            else {
                                tempRecordsToInsert.addRecords(recordsFromClientMap[it]!!)
                            }
                        }
                    }

                    entityService.bulkInsert(tempRecordsToInsert.build(), tableRecords.tableDefinition)

                    recordsFromDatabaseMap.keys.forEach {
                        val idColumn = YaormUtils.getIdColumn(recordsFromDatabaseMap[it]!!.columnsList)
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

        val messageId = ProtobufUtils.getIdFromMessage(message)
        val databaseMessage = this.get(message, messageId) ?: return false
        val databaseRecords = ProtobufUtils.convertProtobufObjectToRecords(databaseMessage, this.definitions, this.customIndexes)

        // todo - order this as a dag, also create relational constraints between the tables
        // go through children now, this is the source of truth
        databaseRecords.tableRecordsList
                .forEach { tableRecords ->
                    tableRecords.records.recordsList.forEach {
                        val idColumn = YaormUtils.getIdColumn(it.columnsList)
                        if (idColumn != null) {
                            entityService.delete(idColumn.stringHolder, tableRecords.tableDefinition)
                        }
                    }
                }

        return true
    }

    override fun <T : Message> get(messageType:T, id: String): T? {
        val objects = GetProtoObjects(
                this.entityService,
                this.definitions,
                this.customIndexes)

        val foundItems = objects.build(messageType, listOf(id))

        if (foundItems.isNotEmpty()) {
            return foundItems.first()
        }
        return null
    }

    override fun <T : Message> getMany(messageType: T, limit: Int, offset: Int): List<T> {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return ArrayList()
        }

        val ids = ArrayList<String>()

        // get ids first
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.getMany(tableDefinition, limit, offset).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {
                        ids.add(idColumn.stringHolder)
                    }
        }

        val objects = GetProtoObjects(
                this.entityService,
                this.definitions,
                this.customIndexes)

        // get messageType and all children
        return objects.build(messageType, ids)
    }

    override fun <T : Message> getManyStream(messageType: T, streamer: IMessageStreamer, limit:Int, offset: Int) {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return
        }

        val ids = ArrayList<String>()
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.getMany(definition = tableDefinition, limit = limit, offset = offset).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {

                        ids.add(idColumn.stringHolder)

                        if (ids.size > MaxQuerySize) {
                            val objects = GetProtoObjects(
                                    this.entityService,
                                    this.definitions,
                                    this.customIndexes)

                            objects.build(messageType, ids).forEach { completedMessage ->
                                streamer.stream(completedMessage)
                            }
                            ids.clear()
                        }
                    }
                }

        if (ids.size > 0) {
            val objects = GetProtoObjects(
                    this.entityService,
                    this.definitions,
                    this.customIndexes)

            objects.build(messageType, ids).forEach { completedMessage ->
                streamer.stream(completedMessage)
            }
        }
    }

    override fun <T : Message> where(messageType: T, whereClause: YaormModel.WhereClause): List<T> {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return ArrayList()
        }
        val ids = ArrayList<String>()

        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.where(whereClause, tableDefinition).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {
                        ids.add(idColumn.stringHolder)
                    }
                }

        val objects = GetProtoObjects(
                this.entityService,
                this.definitions,
                this.customIndexes)

        return objects.build(messageType, ids)
    }

    override fun <T : Message> whereStream(messageType: T,
                                           whereClause: YaormModel.WhereClause,
                                           streamer: IMessageStreamer) {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return
        }
        val ids = ArrayList<String>()

        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        this.entityService.where(whereClause, tableDefinition).recordsList
                .forEach { record ->
                    val idColumn = YaormUtils.getIdColumn(record.columnsList)
                    if (idColumn != null) {
                        ids.add(idColumn.stringHolder)

                        if (ids.size > MaxQuerySize) {
                            val objects = GetProtoObjects(
                                    this.entityService,
                                    this.definitions,
                                    this.customIndexes)

                            objects.build(messageType, ids).forEach { completedMessage ->
                                streamer.stream(completedMessage)
                            }
                            ids.clear()
                        }
                    }
                }

        if (ids.size > 0) {
            val objects = GetProtoObjects(
                    this.entityService,
                    this.definitions,
                    this.customIndexes)

            objects.build(messageType, ids).forEach { completedMessage ->
                streamer.stream(completedMessage)
            }
            ids.clear()
        }
    }

    override fun <T : Message> getCount(messageType: T): Long {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return -1L
        }
        val tableDefinition = this.buildTableDefinitionWithOnlyId(messageType)
        return this.entityService.getCount(tableDefinition)
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
            private val entityService: IEntityService,
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

    override fun close() {
        this.entityService.close()
    }

    companion object {
        private const val MaxQuerySize = 500
    }
}
