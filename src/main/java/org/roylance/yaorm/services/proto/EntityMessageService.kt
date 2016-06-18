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
        // get id
        val mainRecordId = ProtobufUtils.getIdFromMessage(sourceOfTruthMessage)

        // build records
        val records = ProtobufUtils.convertProtobufObjectToRecords(sourceOfTruthMessage)

        // create the main one
        val mainRecords = records.tableRecordsList.firstOrNull {
            it.tableName.equals(sourceOfTruthMessage.descriptorForType.name)
        } ?: return false

        if (!mainRecords.hasRecords()) {
            return false
        }

        this.entityService.createOrUpdate(
                mainRecords.records.recordsList.first(),
                mainRecords.tableDefinition)

        // todo - order this as a dag, also create relational constraints between the tables
        // go through children now, this is the source of truth
        records.tableRecordsList
                .filter { !it.tableName.equals(sourceOfTruthMessage.descriptorForType.name) &&
                        it.tableDefinition.columnDefinitionsList.any { column ->
                            sourceOfTruthMessage.descriptorForType.name.equals(column.name)
                        }
                }
                .forEach { tableRecords ->
                    val customWhereClause = YaormModel.WhereClause.newBuilder()
                                .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                                .setNameAndProperty(
                                        YaormModel.Column.newBuilder()
                                                    .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                                                            .setColumnType(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)
                                                            .setType(YaormModel.ProtobufType.STRING)
                                                            .setName(sourceOfTruthMessage.descriptorForType.name))
                                                    .setStringHolder(mainRecordId))
                            .build()

                    val recordsFromClientMap = HashMap<String, YaormModel.Record>()
                    val recordsFromDatabaseMap = HashMap<String, YaormModel.Record>()
                    tableRecords.records.recordsList.forEach {
                        val idColumn = it.columnsList.firstOrNull {
                            it.definition.name.equals(CommonUtils.IdName)
                        }
                        if (idColumn != null) {
                            recordsFromClientMap[idColumn.stringHolder] = it
                        }
                    }

                    entityService.where(customWhereClause, tableRecords.tableDefinition)
                            .recordsList.forEach {
                        val idColumn = it.columnsList.firstOrNull {
                            it.definition.name.equals(CommonUtils.IdName)
                        }
                        if (idColumn != null) {
                            recordsFromDatabaseMap[idColumn.stringHolder] = it
                        }
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
                        if (!recordsFromClientMap.containsKey(it)) {
                            // delete
                            val foundIdColumn = recordsFromDatabaseMap[it]!!.columnsList.firstOrNull { it.definition.name.equals(CommonUtils.IdName) }
                            if (foundIdColumn != null) {
                                entityService.delete(foundIdColumn.stringHolder,
                                        tableRecords.tableDefinition)
                            }
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
                    val idColumn = record.columnsList.firstOrNull { CommonUtils.IdName.equals(it.definition.name) } ?: return@forEach
                    val completedMessage = this.get(messageType, idColumn.stringHolder)
                    returnList.add(completedMessage)
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
                    val idColumn = record.columnsList.firstOrNull { CommonUtils.IdName.equals(it.definition.name) } ?: return@forEach
                    val completedMessage = this.get(messageType, idColumn.stringHolder)
                    streamer.stream(completedMessage)
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
                    val idColumn = record.columnsList.firstOrNull { CommonUtils.IdName.equals(it.definition.name) } ?: return@forEach
                    val completedMessage = this.get(messageType, idColumn.stringHolder)
                    returnList.add(completedMessage)
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
                    val idColumn = record.columnsList.firstOrNull { CommonUtils.IdName.equals(it.definition.name) } ?: return@forEach
                    val completedMessage = this.get(messageType, idColumn.stringHolder)
                    streamer.stream(completedMessage)
                }
    }

    override fun <T : Message> getCount(messageType: T): Long {
        if (!ProtobufUtils.isMessageOk(messageType)) {
            return -1L
        }
        val tableDefinition = ProtobufUtils.buildIdOnlyTableDefinition(messageType.descriptorForType)
        return this.entityService.getCount(tableDefinition)
    }
}
