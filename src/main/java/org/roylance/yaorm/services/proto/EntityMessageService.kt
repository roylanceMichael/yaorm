package org.roylance.yaorm.services.proto

import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.utilities.CommonUtils
import org.roylance.yaorm.utilities.ProtobufUtils
import java.util.*

class EntityMessageService(
        private val protoGeneratedMessageBuilder: IProtoGeneratedMessageBuilder,
        private val entityService: IEntityProtoService):IEntityMessageService {

    override fun <T : Message> createEntireSchema(message: T): Boolean {
        if (!ProtobufUtils.isMessageOk(message)) {
            return false
        }

        val records = ProtobufUtils.buildDefinitionGraph(message.descriptorForType)

        val seenTables = HashSet<String>()
        records.tableDefinitionGraphsList.forEach {
            if(!seenTables.contains(it.mainName)) {
                seenTables.add(it.mainName)
                this.entityService.createTable(it.mainTableDefinition)
            }
            if (it.hasLinkerTableTable() && !seenTables.contains(it.linkerTableTable.name)) {
                seenTables.add(it.linkerTableTable.name)
                this.entityService.createTable(it.linkerTableTable)
            }
            if (it.hasOtherTableDefinition() && !seenTables.contains(it.otherName)) {
                seenTables.add(it.otherName)
                this.entityService.createTable(it.otherTableDefinition)
            }
        }

        return true
    }

    override fun <T : Message> dropAndRecreateEntireSchema(message: T): Boolean {
        if (!ProtobufUtils.isMessageOk(message)) {
            return false
        }
        val records = ProtobufUtils.buildDefinitionGraph(message.descriptorForType)

        val seenTables = HashSet<String>()
        records.tableDefinitionGraphsList.forEach {
            if(!seenTables.contains(it.mainName)) {
                seenTables.add(it.mainName)
                this.entityService.dropTable(it.mainTableDefinition)
                this.entityService.createTable(it.mainTableDefinition)
            }
            if (it.hasLinkerTableTable() && !seenTables.contains(it.linkerTableTable.name)) {
                seenTables.add(it.linkerTableTable.name)
                this.entityService.dropTable(it.linkerTableTable)
                this.entityService.createTable(it.linkerTableTable)
            }
            if (it.hasOtherTableDefinition() && !seenTables.contains(it.otherName)) {
                seenTables.add(it.otherName)
                this.entityService.dropTable(it.otherTableDefinition)
                this.entityService.createTable(it.otherTableDefinition)
            }
        }

        return true
    }

    override fun <T : Message> merge(sourceOfTruthMessage: T): Boolean {
        if (!ProtobufUtils.isMessageOk(sourceOfTruthMessage)) {
            return false
        }

        // build records
        val records = ProtobufUtils.convertProtobufObjectToRecords(sourceOfTruthMessage)

        // create the main one
        if (!records.tableRecords.containsKey(sourceOfTruthMessage.descriptorForType.name)) {
            return false
        }

        val mainRecords = records.tableRecords[sourceOfTruthMessage.descriptorForType.name]!!

        if (!mainRecords.hasRecords()) {
            return false
        }

        // todo - order this as a dag, also create relational constraints between the tables
        // go through children now, this is the source of truth
        records.tableRecords
                .values
                .forEach { tableRecords ->
                    val recordsFromClientMap = HashMap<String, YaormModel.Record>()
                    val recordsFromDatabaseMap:HashMap<String, YaormModel.Record>

                    // where clause with all the ids
                    tableRecords.records.recordsList.forEach {
                        if (it.columns.containsKey(CommonUtils.IdName)) {
                            val idColumn = it.columns[CommonUtils.IdName]!!
                            recordsFromClientMap[idColumn.stringHolder] = it
                        }
                    }

                    // if this is a linker table, then we want compare all the items with parent vs what we have
                    if (tableRecords.tableDefinition.tableType.equals(YaormModel.TableDefinition.TableType.NORMAL)) {
                        recordsFromDatabaseMap = this.handleNormalCase(tableRecords)
                    }
                    else {
                        recordsFromDatabaseMap = this.handleLinkerMessageCase(tableRecords)
                    }

                    // merge stuff here
                    recordsFromClientMap.keys.forEach {
                        if (recordsFromDatabaseMap.containsKey(it)) {
                            // update
                            entityService.update(recordsFromClientMap[it]!!,
                                    tableRecords.tableDefinition)
                        }
                        else {
                            // insert
                            entityService.create(recordsFromClientMap[it]!!,
                                    tableRecords.tableDefinition)
                        }
                    }

                    recordsFromDatabaseMap.keys.forEach {
                        if (!recordsFromClientMap.containsKey(it) && recordsFromDatabaseMap[it]!!.columns.containsKey(CommonUtils.IdName)) {
                            // delete
                            entityService.delete(
                                    recordsFromDatabaseMap[it]!!.columns[CommonUtils.IdName]!!.stringHolder,
                                    tableRecords.tableDefinition)
                        }
                    }
                }

        return true
    }

    override fun <T : Message> delete(sourceOfTruthMessage: T): Boolean {
        if (!ProtobufUtils.isMessageOk(sourceOfTruthMessage)) {
            return false
        }

        val tableDefinition = ProtobufUtils.buildDefinitionFromDescriptor(sourceOfTruthMessage.descriptorForType) ?:
                return false

        val id = ProtobufUtils.getIdFromMessage(sourceOfTruthMessage)
        return this.entityService.delete(id, tableDefinition)
    }

    override fun <T : Message> get(messageType:T, id: String): T {
        return ProtobufUtils.getProtoObjectFromBuilderSingle(messageType, this.entityService, id, this.protoGeneratedMessageBuilder)
    }

    override fun <T : Message> getMany(messageType: T, maxAmount: Int): List<T> {
        val returnList = ArrayList<T>()
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return returnList
        }

        // get ids first
        val tableDefinition = ProtobufUtils.buildIdOnlyTableDefinition(messageType.descriptorForType)
        this.entityService.getMany(maxAmount, tableDefinition).recordsList
                .forEach { record ->
                    if (record.columns.containsKey(CommonUtils.IdName)) {
                        val idColumn = record.columns[CommonUtils.IdName]!!
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        returnList.add(completedMessage)
                    }
        }

        // get message and all children
        return returnList
    }

    override fun <T : Message> getManyStream(messageType: T, streamer: IMessageStreamer) {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return
        }
        val tableDefinition = ProtobufUtils.buildIdOnlyTableDefinition(messageType.descriptorForType)
        this.entityService.getMany(definition = tableDefinition).recordsList
                .forEach { record ->
                    if (record.columns.containsKey(CommonUtils.IdName)) {
                        val idColumn = record.columns[CommonUtils.IdName]!!
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        streamer.stream(completedMessage)
                    }
                }
    }

    override fun <T : Message> where(messageType: T, whereClause: YaormModel.WhereClause): List<T> {
        val returnList = ArrayList<T>()
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return returnList
        }
        val tableDefinition = ProtobufUtils.buildIdOnlyTableDefinition(messageType.descriptorForType)
        this.entityService.where(whereClause, tableDefinition).recordsList
                .forEach { record ->
                    if (record.columns.containsKey(CommonUtils.IdName)) {
                        val idColumn = record.columns[CommonUtils.IdName]!!
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        returnList.add(completedMessage)
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
        val tableDefinition = ProtobufUtils.buildIdOnlyTableDefinition(messageType.descriptorForType)
        this.entityService.where(whereClause, tableDefinition).recordsList
                .forEach { record ->
                    if (record.columns.containsKey(CommonUtils.IdName)) {
                        val idColumn = record.columns[CommonUtils.IdName]!!
                        val completedMessage = this.get(messageType, idColumn.stringHolder)
                        streamer.stream(completedMessage)
                    }
                }
    }

    override fun <T : Message> getCount(messageType: T): Long {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return -1L
        }
        val tableDefinition = ProtobufUtils.buildIdOnlyTableDefinition(messageType.descriptorForType)
        return this.entityService.getCount(tableDefinition)
    }

    private fun handleLinkerMessageCase(tableRecords: YaormModel.TableRecords):HashMap<String, YaormModel.Record> {
        // where clause with all the main parent items
        val recordsFromDatabaseMap = HashMap<String, YaormModel.Record>()
        val parentColumn = tableRecords.tableDefinition.columnDefinitions.values
                .firstOrNull { it.linkerType.equals(YaormModel.ColumnDefinition.LinkerType.PARENT) } ?: return recordsFromDatabaseMap

        var customWhereClauseLinker = YaormModel.WhereClause.newBuilder()
                .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                .setConnectingAndOr(YaormModel.WhereClause.ConnectingAndOr.OR)

        var firstWhereClause:YaormModel.WhereClause.Builder? = null

        tableRecords.records.recordsList.forEach { record ->
            if (record.columns.containsKey(parentColumn.name)) {
                val parentIdColumn = record.columns[parentColumn.name]!!
                customWhereClauseLinker.nameAndProperty = parentIdColumn

                val newWhereClause = YaormModel.WhereClause.newBuilder()
                        .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                        .setConnectingWhereClause(customWhereClauseLinker)
                        .setConnectingAndOr(YaormModel.WhereClause.ConnectingAndOr.OR)

                if (firstWhereClause == null) {
                    firstWhereClause = customWhereClauseLinker
                }

                customWhereClauseLinker = newWhereClause
            }

        }

        if (firstWhereClause == null) {
            return recordsFromDatabaseMap
        }

        entityService.where(firstWhereClause!!.build(), tableRecords.tableDefinition)
                .recordsList.forEach {
            if (it.columns.containsKey(CommonUtils.IdName)) {
                val idColumn = it.columns[CommonUtils.IdName]!!
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
            if (record.columns.containsKey(CommonUtils.IdName)) {
                val idColumn = record.columns[CommonUtils.IdName]!!
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
            if (it.columns.containsKey(CommonUtils.IdName)) {
                val idColumn = it.columns[CommonUtils.IdName]!!
                recordsFromDatabaseMap[idColumn.stringHolder] = it
            }
        }
        return recordsFromDatabaseMap
    }
}
