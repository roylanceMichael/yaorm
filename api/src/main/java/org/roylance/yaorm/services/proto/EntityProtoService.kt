package org.roylance.yaorm.services.proto

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.db.GenericModel
import org.roylance.yaorm.services.IConnectionSourceFactory
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class EntityProtoService(private val granularDatabaseService: IGranularDatabaseProtoService,
                         private val sqlGeneratorService: ISQLGeneratorService) : IEntityProtoService {
    override val connectionSourceFactory: IConnectionSourceFactory
        get() = granularDatabaseService.connectionSourceFactory

    override val insertSameAsUpdate: Boolean
        get() = this.sqlGeneratorService.insertSameAsUpdate

    override fun buildDefinitionFromSql(customSql: String, rowCount: Int): YaormModel.TableDefinition {
        return this.granularDatabaseService.buildTableDefinitionFromQuery(customSql, rowCount)
    }

    override fun getIdsStream(definition: YaormModel.TableDefinition,
                              streamer: IProtoStreamer) {
        if (!this.granularDatabaseService.isAvailable()) {
            return
        }
        val selectSql = this.sqlGeneratorService.buildSelectIds(definition)

        this.granularDatabaseService
                .executeSelectQuery(definition, selectSql)
                .getRecords()
                .recordsList
                .map {
            streamer.stream(it)
        }
    }

    override fun getIds(definition: YaormModel.TableDefinition): List<String> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }
        val selectSql = this.sqlGeneratorService.buildSelectIds(definition)

        return this.granularDatabaseService.executeSelectQuery(definition, selectSql)
                .getRecords().recordsList.map {
            YaormUtils.getIdColumn(it.columnsList)!!.stringHolder
        }
    }

    override fun getManyStream(definition: YaormModel.TableDefinition,
                               streamer: IProtoStreamer,
                               limit: Int,
                               offset: Int) {
        if (!this.granularDatabaseService.isAvailable()) {
            return
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(definition, limit, offset)

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

    override fun createIndex(indexModel: YaormModel.Index,
                             definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createIndexSql = this.sqlGeneratorService.buildCreateIndex(
                definition,
                indexModel.columnNamesList.associateBy { it.name },
                indexModel.includeNamesList.associateBy { it.name }) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createIndexSql)
                .successful
    }

    override fun dropIndex(indexModel: YaormModel.Index,
                           definition: YaormModel.TableDefinition): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropIndexSql = this.sqlGeneratorService.buildDropIndex(
                definition,
                indexModel.columnNamesList.associateBy { it.name }) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(dropIndexSql)
                .successful
    }

    override fun createColumn(propertyDefinition: YaormModel.ColumnDefinition,
                              definition: YaormModel.TableDefinition): Boolean {
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

    override fun dropColumn(propertyDefinition: YaormModel.ColumnDefinition,
                            definition: YaormModel.TableDefinition): Boolean {
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

        val cursor = this.granularDatabaseService.
                executeSelectQuery(GenericModel.buildProtoDefinitionModel(), countSql)
        val allRecords = cursor.getRecords()

        if (allRecords.recordsCount > 0) {
            val foundRecord = allRecords.recordsList[0]
            val longValColumn = foundRecord.columnsList.firstOrNull { it.definition.name == GenericModel.LongValName }
            if (longValColumn != null) {
                return longValColumn.int64Holder
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

    override fun getCustomStream(customSql: String, definition: YaormModel.TableDefinition, streamer: IProtoStreamer) {
        if (!this.granularDatabaseService.isAvailable()) {
            return
        }

        this.granularDatabaseService.executeSelectQueryStream(definition, customSql, streamer)
    }

    override fun get(id: String, definition: YaormModel.TableDefinition): YaormModel.Record? {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Record.getDefaultInstance()
        }

        val propertyHolder = YaormModel.Column.newBuilder()
            .setStringHolder(id)
            .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                    .setType(YaormModel.ProtobufType.STRING)
                    .setName(YaormUtils.IdName).setIsKey(true))
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

        return null
    }

    override fun getMany(definition: YaormModel.TableDefinition,
                         limit: Int,
                         offset: Int): YaormModel.Records {
        if (!this.granularDatabaseService.isAvailable()) {
            return YaormModel.Records.getDefaultInstance()
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(definition, limit, offset)

        return this.granularDatabaseService.executeSelectQuery(definition, allSql).getRecords()
    }

    override fun where(whereClauseItem: YaormModel.WhereClause,
                       definition: YaormModel.TableDefinition): YaormModel.Records {
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

        // let's split this into items of limit each... for now
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
        val idColumn = YaormUtils.getIdColumn(entity.columnsList) ?: return false

        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val foundItemInDatabase = this.get(idColumn.stringHolder, definition)
        if (foundItemInDatabase != null) {
            return this.update(entity, definition)
        }

        return this.create(entity, definition)
    }

    override fun create(entity: YaormModel.Record, definition: YaormModel.TableDefinition): Boolean {
        YaormUtils.getIdColumn(entity.columnsList) ?: return false

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
        if (YaormUtils.getIdColumn(entity.columnsList) == null) {
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

    override fun updateWithCriteria(newValues: YaormModel.Record,
                                    whereClauseItem: YaormModel.WhereClause,
                                    definition: YaormModel.TableDefinition): Boolean {
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
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setType(YaormModel.ProtobufType.STRING)
                        .setName(YaormUtils.IdName).setIsKey(true))
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

    override fun getSchemaNames(): List<String> {
        val schemaNamesSql = this.sqlGeneratorService.getSchemaNames()
        val columnNameRecords = this.granularDatabaseService.executeSelectQuery(schemaNamesSql)
        return columnNameRecords.recordsList.map {
            val firstColumn = it.columnsList.firstOrNull()
            if (firstColumn == null) {
                YaormUtils.EmptyString
            }
            else {
                firstColumn.stringHolder
            }
        }.filter(String::isNotEmpty)
    }

    override fun getTableNames(schemaName: String): List<String> {
        val tableNamesSql = this.sqlGeneratorService.getTableNames(schemaName)
        val tableNameRecords = this.granularDatabaseService.executeSelectQuery(tableNamesSql)
        return tableNameRecords.recordsList.map {
            val firstColumn = it.columnsList.firstOrNull()
            if (firstColumn == null) {
                YaormUtils.EmptyString
            }
            else {
                firstColumn.stringHolder
            }
        }.filter(String::isNotEmpty)
    }

    override fun getTableDefinition(schemaName: String, tableName: String): YaormModel.TableDefinition {
        val tableDefinitionSql = this.sqlGeneratorService.buildTableDefinitionSQL(schemaName, tableName)
        val tableDefinitionRecords = this.granularDatabaseService.executeSelectQuery(tableDefinitionSql)
        val tableDefinition = this.sqlGeneratorService.buildTableDefinition(tableName, tableDefinitionRecords)

        return tableDefinition
    }

    override fun close() {
        this.granularDatabaseService.close()
    }
}
