package org.roylance.yaorm.services.proto

import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.db.GenericModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonUtils
import java.util.*

class EntityProtoService(override val indexDefinition: YaormModel.Index?,
                         private val granularDatabaseService: IGranularDatabaseProtoService,
                         private val sqlGeneratorService: ISqlGeneratorService) : IEntityProtoService {
    override fun getIdsStream(definition: YaormModel.TableDefinition, streamer: IProtoStreamer) {
        if (!this.granularDatabaseService.isAvailable()) {
            return
        }
        val selectSql = this.sqlGeneratorService.buildSelectIds(definition)

        this.granularDatabaseService.executeSelectQuery(definition, selectSql).getRecords().recordsList.map {
            streamer.stream(it)
        }
    }

    override fun getIds(definition: YaormModel.TableDefinition): List<String> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }
        val selectSql = this.sqlGeneratorService.buildSelectIds(definition)

        return this.granularDatabaseService.executeSelectQuery(definition, selectSql).getRecords().recordsList.map {
            it.columns[CommonUtils.IdName]!!.stringHolder
        }
    }

    override fun getManyStream(n:Int, definition: YaormModel.TableDefinition, streamer: IProtoStreamer) {
        if (!this.granularDatabaseService.isAvailable()) {
            return
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(definition, n)

        this.granularDatabaseService.executeSelectQueryStream(definition, allSql, streamer)
    }

    override fun createTable(definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createTableSql = this.sqlGeneratorService
                .buildCreateTable(definition) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createTableSql)
                .successful
    }

    override fun dropTable(definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropTableSql = this.sqlGeneratorService
                .buildDropTable(definition)

        return this.granularDatabaseService
                .executeUpdateQuery(dropTableSql)
                .successful
    }

    override fun createIndex(indexModel: YaormModel.Index, definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createIndexSql = this.sqlGeneratorService.buildCreateIndex(
                definition,
                indexModel.columnNames,
                indexModel.includeNames) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createIndexSql)
                .successful
    }

    override fun dropIndex(indexModel: YaormModel.Index, definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropIndexSql = this.sqlGeneratorService.buildDropIndex(
                definition,
                indexModel.columnNames) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(dropIndexSql)
                .successful
    }

    override fun createColumn(propertyDefinition: YaormModel.ColumnDefinition, definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val addColumnSql = this.sqlGeneratorService.buildCreateColumn(
                definition,
                propertyDefinition) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(addColumnSql)
                .successful
    }

    override fun dropColumn(propertyDefinition: YaormModel.ColumnDefinition, definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropTableSqlStatements = this.sqlGeneratorService.buildDropColumn(
                definition,
                propertyDefinition) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(dropTableSqlStatements)
                .successful
    }

    override fun getCount(definition: YaormModel.TableDefinition): Long {
        if (!this.granularDatabaseService.isAvailable()) {
            return 0L
        }

        val countSql = this.sqlGeneratorService.buildCountSql(definition)

        val cursor = this.granularDatabaseService.executeSelectQuery(GenericModel.buildProtoDefinitionModel(), countSql)
        val allRecords = cursor.getRecords()

        if (allRecords.recordsCount > 0) {
            val foundRecord = allRecords.recordsList[0]
            if (foundRecord.columns.containsKey(GenericModel.LongValName)) {
                return foundRecord.columns[GenericModel.LongValName]!!.int64Holder
            }
        }
        return -1
    }

    override fun getCustom(customSql: String, definition: YaormModel.TableDefinition): YaormModel.Records {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Records.newBuilder().build()
        }

        return this.granularDatabaseService
                .executeSelectQuery(definition, customSql)
                .getRecords()
    }

    override fun get(id: String, definition: YaormModel.TableDefinition): YaormModel.Record {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Record.getDefaultInstance()
        }

        val propertyHolder = YaormModel.Column.newBuilder()
            .setStringHolder(id)
            .setDefinition(YaormModel.ColumnDefinition.newBuilder().setType(YaormModel.ProtobufType.STRING).setName(CommonUtils.IdName).setIsKey(true))
            .build()

        val whereClause = YaormModel.WhereClause.newBuilder()
            .setNameAndProperty(propertyHolder)
            .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
            .build()

        val whereSql = this.sqlGeneratorService
                .buildWhereClause(definition, whereClause) ?: return YaormModel.Record.newBuilder().build()

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                definition,
                whereSql)

        val records = resultSet.getRecords()

        if (records.recordsCount > 0) {
            return records.recordsList.first()
        }

        return YaormModel.Record.getDefaultInstance()
    }

    override fun getMany(n: Int, definition: YaormModel.TableDefinition): YaormModel.Records {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Records.getDefaultInstance()
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(definition, n)

        return this.granularDatabaseService.executeSelectQuery(definition, allSql).getRecords()
    }

    override fun where(whereClauseItem: YaormModel.WhereClause, definition: YaormModel.TableDefinition): YaormModel.Records {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Records.getDefaultInstance()
        }

        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        definition,
                        whereClauseItem) ?: return YaormModel.Records.getDefaultInstance()

        return this.granularDatabaseService.executeSelectQuery(definition, whereSql).getRecords()
    }

    override fun bulkInsert(instances: YaormModel.Records, definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        // let's split this into items of n each... for now
        val temporaryList = YaormModel.Records.newBuilder()
        val results = ArrayList<Boolean>()

        instances
                .recordsList
                .forEach {
                    temporaryList.addRecords(it)

                    if (temporaryList.recordsCount >= this.sqlGeneratorService.bulkInsertSize) {
                        val bulkInsertSql = this.sqlGeneratorService
                                .buildBulkInsert(definition, temporaryList.build())

                        val result = this.granularDatabaseService
                                .executeUpdateQuery(bulkInsertSql)
                        results.add(result.successful)
                        temporaryList.clearRecords()
                    }
                }

        if (temporaryList.recordsCount > 0) {
            val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(definition, temporaryList.build())

            val result = this.granularDatabaseService
                    .executeUpdateQuery(bulkInsertSql)
            results.add(result.successful)
        }

        return results.all { it }
    }

    override fun createOrUpdate(entity: YaormModel.Record, definition: YaormModel.TableDefinition): Boolean {
        if (!entity.columns.containsKey(CommonUtils.IdName)) {
            return false
        }

        val idColumn = entity.columns[CommonUtils.IdName]!!
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val foundItemInDatabase = this.get(idColumn.stringHolder, definition)
        if (foundItemInDatabase.columns.size > 0) {
            return this.update(entity, definition)
        }

        return this.create(entity, definition)
    }

    override fun create(entity: YaormModel.Record, definition: YaormModel.TableDefinition): Boolean {
        if (!entity.columns.containsKey(CommonUtils.IdName)) {
            return false
        }

        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        // create
        val insertSql = this.sqlGeneratorService
                .buildInsertIntoTable(definition, entity) ?: return false

        val result = this.granularDatabaseService
                .executeUpdateQuery(insertSql)

        return result.successful
    }

    override fun update(entity: YaormModel.Record, definition: YaormModel.TableDefinition): Boolean {
        if (!entity.columns.containsKey(CommonUtils.IdName)) {
            return false
        }

        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        // update
        val updateSql = this
                .sqlGeneratorService
                .buildUpdateTable(definition, entity) ?: return false

        val result = this.granularDatabaseService
                .executeUpdateQuery(updateSql)

        return result.successful
    }

    override fun updateWithCriteria(newValues: YaormModel.Record, whereClauseItem: YaormModel.WhereClause, definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val updateSql = this.sqlGeneratorService.buildUpdateWithCriteria(
                definition,
                newValues,
                whereClauseItem) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(updateSql)
                .successful
    }

    override fun updateCustom(customSql: String): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }
        this.granularDatabaseService.executeUpdateQuery(customSql)
        return true
    }

    override fun delete(id: String, definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }
        val propertyHolder = YaormModel.Column.newBuilder()
                .setStringHolder(id)
                .setDefinition(YaormModel.ColumnDefinition.newBuilder().setType(YaormModel.ProtobufType.STRING).setName(CommonUtils.IdName).setIsKey(true))
                .build()


        val deleteSql =
                this.sqlGeneratorService
                        .buildDeleteTable(definition, propertyHolder) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(deleteSql)
                .successful
    }

    override fun deleteAll(definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val sql = this.sqlGeneratorService.buildDeleteAll(definition)
        return this.granularDatabaseService
                .executeUpdateQuery(sql)
                .successful
    }
}
