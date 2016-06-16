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

    override fun <T : Message> bulkCreate(sourceOfTruthMessages: List<T>): Boolean {
        sourceOfTruthMessages.forEach {
            this.createOrUpdate(it)
        }
        return true
    }

    override fun <T : Message> createOrUpdate(sourceOfTruthMessage: T): Boolean {
        if (!ProtobufUtils.isMessageOk(sourceOfTruthMessage)) {
            return false
        }
        // get id
        val mainRecordId = ProtobufUtils.getIdFromMessage(sourceOfTruthMessage)

        // build records
        val records = ProtobufUtils.convertProtobufObjectToRecords(sourceOfTruthMessage)

        // create the main one
        val mainRecords = records.tableRecordsList.firstOrNull { it.tableName.equals(sourceOfTruthMessage.descriptorForType.name) } ?: return false
        if (!mainRecords.hasRecords()) {
            return false
        }

        this.entityService.createOrUpdate(mainRecords.records.recordsList.first(), mainRecords.tableDefinition)

        // go through children now, this is the source of truth
        records.tableRecordsList
                .filter { !it.tableName.equals(sourceOfTruthMessage.descriptorForType.name) }
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
                        val idColumn = it.columnsList.firstOrNull { it.definition.name.equals(CommonUtils.IdName) }
                        if (idColumn != null) {
                            recordsFromClientMap[idColumn.stringHolder] = it
                        }
                    }

                    entityService.where(customWhereClause, tableRecords.tableDefinition).recordsList.forEach {
                        val idColumn = it.columnsList.firstOrNull { it.definition.name.equals(CommonUtils.IdName) }
                        if (idColumn != null) {
                            recordsFromDatabaseMap[idColumn.stringHolder] = it
                        }
                    }

                    // merge stuff here
                    recordsFromClientMap.keys.forEach {
                        if (recordsFromDatabaseMap.containsKey(it)) {
                            // update
                            entityService.update(recordsFromClientMap[it]!!, tableRecords.tableDefinition)
                        }
                        else {
                            // insert
                            entityService.create(recordsFromClientMap[it]!!, tableRecords.tableDefinition)
                        }
                    }

                    recordsFromDatabaseMap.keys.forEach {
                        if (!recordsFromClientMap.containsKey(it)) {
                            // delete
                            val foundIdColumn = recordsFromDatabaseMap[it]!!.columnsList.firstOrNull { it.definition.name.equals(CommonUtils.IdName) }
                            if (foundIdColumn != null) {
                                entityService.delete(foundIdColumn.stringHolder, tableRecords.tableDefinition)
                            }
                        }
                    }
                }

        return true
    }

    override fun <T : Message> delete(message: T): Boolean {
        // delete all children too
        throw UnsupportedOperationException()
    }

    override fun <T : Message> deleteAll(message: List<T>): Boolean {
        // delete message and all children
        throw UnsupportedOperationException()
    }

    override fun <T : Message> getMany(messageType: T, maxAmount: Int): List<T> {
        // get message and all children
//        ProtobufUtils.getProtoObjectFromBuilderSingle()
        throw UnsupportedOperationException()
    }

    override fun <T : Message> getManyStream(messageType: T, streamer: IMessageStreamer) {
        throw UnsupportedOperationException()
    }

    override fun <T : Message> where(messageType: T, whereClause: YaormModel.WhereClause, streamer: IMessageStreamer) {
        throw UnsupportedOperationException()
    }

    override fun <T : Message> getCount(messageType: T): Long {
        throw UnsupportedOperationException()
    }
}
