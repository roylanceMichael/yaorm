package org.roylance.yaorm.services.proto

import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.db.GenericModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonUtils
import java.util.*

class EntityProtoService(override val indexDefinition: YaormModel.Index?,
                         private val granularDatabaseService: IGranularDatabaseProtoService,
                         private val sqlGeneratorService: ISqlGeneratorService) : IEntityProtoService {
    override fun getManyStream(n:Int, definition: YaormModel.Definition, streamer: IProtoStreamer) {
        if (!this.granularDatabaseService.isAvailable()) {
            return
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(definition, n)

        this.granularDatabaseService.executeSelectQueryStream(definition, allSql, streamer)
    }

    override fun createTable(definition: YaormModel.Definition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createTableSql = this.sqlGeneratorService
                .buildCreateTable(definition) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createTableSql)
                .successful
    }

    override fun dropTable(definition: YaormModel.Definition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropTableSql = this.sqlGeneratorService
                .buildDropTable(definition)

        return this.granularDatabaseService
                .executeUpdateQuery(dropTableSql)
                .successful
    }

    override fun createIndex(indexModel: YaormModel.Index, definition: YaormModel.Definition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createIndexSql = this.sqlGeneratorService.buildCreateIndex(
                definition,
                indexModel.columnNamesList,
                indexModel.includeNamesList) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createIndexSql)
                .successful
    }

    override fun dropIndex(indexModel: YaormModel.Index, definition: YaormModel.Definition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropIndexSql = this.sqlGeneratorService.buildDropIndex(
                definition,
                indexModel.columnNamesList) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(dropIndexSql)
                .successful
    }

    override fun createColumn(propertyDefinition: YaormModel.PropertyDefinition, definition: YaormModel.Definition): Boolean {
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

    override fun dropColumn(propertyDefinition: YaormModel.PropertyDefinition, definition: YaormModel.Definition): Boolean {
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

    override fun getCount(definition: YaormModel.Definition): Long {
        if (!this.granularDatabaseService.isAvailable()) {
            return 0L
        }

        val countSql = this.sqlGeneratorService.buildCountSql(definition)

        val cursor = this.granularDatabaseService.executeSelectQuery(GenericModel.buildProtoDefinitionModel(), countSql)
        val allRecords = cursor.getRecords()

        if (allRecords.recordsCount > 0) {
            val foundRecord = allRecords.recordsList[0]
            val foundColumn = foundRecord.columnsList.firstOrNull { it.propertyDefinition.name.equals(GenericModel.LongValName) }
            if (foundColumn != null) {
                return foundColumn.int64Holder
            }
        }
        return -1
    }

    override fun getCustom(customSql: String, definition: YaormModel.Definition): YaormModel.Records {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Records.newBuilder().build()
        }

        return this.granularDatabaseService
                .executeSelectQuery(definition, customSql)
                .getRecords()
    }

    override fun get(id: String, definition: YaormModel.Definition): YaormModel.Record {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Record.getDefaultInstance()
        }

        val propertyHolder = YaormModel.Column.newBuilder()
            .setStringHolder(id)
            .setPropertyDefinition(YaormModel.PropertyDefinition.newBuilder().setType(YaormModel.ProtobufType.STRING).setName(CommonUtils.IdName).setIsKey(true))
            .build()

        val whereClause = YaormModel.WhereClauseItem.newBuilder()
            .setNameAndProperty(propertyHolder)
            .setOperatorType(YaormModel.WhereClauseItem.OperatorType.EQUALS)
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

    override fun getMany(n: Int, definition: YaormModel.Definition): YaormModel.Records {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Records.getDefaultInstance()
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(definition, n)

        return this.granularDatabaseService.executeSelectQuery(definition, allSql).getRecords()
    }

    override fun where(whereClauseItem: YaormModel.WhereClauseItem, definition: YaormModel.Definition): YaormModel.Records {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Records.getDefaultInstance()
        }

        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        definition,
                        whereClauseItem) ?: return YaormModel.Records.getDefaultInstance()

        return this.granularDatabaseService.executeSelectQuery(definition, whereSql).getRecords()
    }

    override fun bulkInsert(instances: YaormModel.Records, definition: YaormModel.Definition): Boolean {
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

    override fun createOrUpdate(entity: YaormModel.Record, definition: YaormModel.Definition): Boolean {
        val idColumn = entity.columnsList.firstOrNull { it.propertyDefinition.name.equals(sqlGeneratorService.javaIdName) }
        if (!this.granularDatabaseService.isAvailable() || idColumn == null) {
            return false
        }



        val foundItemInDatabase = this.get(idColumn.stringHolder, definition)
        if (foundItemInDatabase.columnsCount > 0) {
            return this.update(entity, definition)
        }

        return this.create(entity, definition)
    }

    override fun create(entity: YaormModel.Record, definition: YaormModel.Definition): Boolean {
        val idColumn = entity.columnsList.firstOrNull { it.propertyDefinition.name.equals(sqlGeneratorService.javaIdName) }
        if (!this.granularDatabaseService.isAvailable() || idColumn == null) {
            return false
        }
        // create
        val insertSql = this.sqlGeneratorService
                .buildInsertIntoTable(definition, entity) ?: return false

        val result = this.granularDatabaseService
                .executeUpdateQuery(insertSql)

        return result.successful
    }

    override fun update(entity: YaormModel.Record, definition: YaormModel.Definition): Boolean {
        val idColumn = entity.columnsList.firstOrNull { it.propertyDefinition.name.equals(sqlGeneratorService.javaIdName) }
        if (!this.granularDatabaseService.isAvailable()|| idColumn == null) {
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

    override fun updateWithCriteria(newValues: YaormModel.Record, whereClauseItem: YaormModel.WhereClauseItem, definition: YaormModel.Definition): Boolean {
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

    override fun delete(id: String, definition: YaormModel.Definition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }
        val propertyHolder = YaormModel.Column.newBuilder()
                .setStringHolder(id)
                .setPropertyDefinition(YaormModel.PropertyDefinition.newBuilder().setType(YaormModel.ProtobufType.STRING).setName(CommonUtils.IdName).setIsKey(true))
                .build()


        val deleteSql =
                this.sqlGeneratorService
                        .buildDeleteTable(definition, propertyHolder) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(deleteSql)
                .successful
    }

    override fun deleteAll(definition: YaormModel.Definition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val sql = this.sqlGeneratorService.buildDeleteAll(definition)
        return this.granularDatabaseService
                .executeUpdateQuery(sql)
                .successful
    }
}
