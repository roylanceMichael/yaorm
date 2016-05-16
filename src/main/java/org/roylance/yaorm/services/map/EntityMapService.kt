package org.roylance.yaorm.services.map

import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.db.GenericModel
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.IndexModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlOperators
import java.util.*

class EntityMapService(
        override val indexDefinition: IndexModel?,
        private val granularDatabaseService: IGranularDatabaseMapService,
        private val sqlGeneratorService: ISqlGeneratorService) : IEntityMapService {

    override fun createTable(definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createTableSql = this.sqlGeneratorService
                .buildCreateTable(definition) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createTableSql)
                .successful
    }

    override fun dropTable(definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropTableSql = this.sqlGeneratorService
                .buildDropTable(definition)

        return this.granularDatabaseService
                .executeUpdateQuery(dropTableSql)
                .successful
    }

    override fun createIndex(indexModel: IndexModel, definition: DefinitionModel): Boolean {
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

    override fun dropIndex(indexModel: IndexModel, definition: DefinitionModel): Boolean {
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

    override fun createColumn(propertyDefinitionModel: PropertyDefinitionModel, definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val addColumnSql = this.sqlGeneratorService.buildCreateColumn(
                definition,
                propertyDefinitionModel) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(addColumnSql)
                .successful
    }

    override fun dropColumn(propertyDefinitionModel: PropertyDefinitionModel, definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropTableSqlStatements = this.sqlGeneratorService.buildDropColumn(
                definition,
                propertyDefinitionModel) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(dropTableSqlStatements)
                .successful
    }

    override fun getCount(definition: DefinitionModel): Long {
        if (!this.granularDatabaseService.isAvailable()) {
            return 0L
        }

        val countSql = this.sqlGeneratorService.buildCountSql(definition)

        val cursor = this.granularDatabaseService.executeSelectQuery(GenericModel.buildDefinitionModel(), countSql)
        val allRecords:List<Map<String, Any>> = cursor.getRecords()

        if (allRecords.size > 0) {
            return allRecords[0][GenericModel.LongValName] as Long
        }
        return -1
    }

    override fun getCustom(customSql: String, definition: DefinitionModel): List<Map<String, Any>> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }

        return this.granularDatabaseService
                .executeSelectQuery(definition, customSql)
                .getRecords()
    }

    override fun get(id: String, definition: DefinitionModel): Map<String, Any> {
        if (!this.granularDatabaseService.isAvailable()) {
            return HashMap()
        }

        val whereClause = WhereClauseItem(
                this.sqlGeneratorService.javaIdName,
                SqlOperators.Equals,
                id as Any)

        val whereSql = this.sqlGeneratorService
                .buildWhereClause(definition, whereClause) ?: return HashMap()

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                definition,
                whereSql)

        val records = resultSet.getRecords()

        if (records.size > 0) {
            return records.first()
        }

        return HashMap()
    }

    override fun getMany(n: Int, definition: DefinitionModel): List<Map<String, Any>> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(definition, n)

        return this.granularDatabaseService.executeSelectQuery(definition, allSql).getRecords()
    }

    override fun where(whereClauseItem: WhereClauseItem, definition: DefinitionModel): List<Map<String, Any>> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }

        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        definition,
                        whereClauseItem) ?: return arrayListOf()

        return this.granularDatabaseService.executeSelectQuery(definition, whereSql).getRecords()
    }

    override fun bulkInsert(instances: List<Map<String, Any>>, definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        // let's split this into items of n each... for now
        val temporaryList = ArrayList<Map<String, Any>>()
        val results = ArrayList<Boolean>()

        instances
                .forEach {
                    temporaryList.add(it)

                    if (temporaryList.size >= this.sqlGeneratorService.bulkInsertSize) {
                        val bulkInsertSql = this.sqlGeneratorService
                                .buildBulkInsert(definition, temporaryList)

                        val result = this.granularDatabaseService
                                .executeUpdateQuery(bulkInsertSql)
                        results.add(result.successful)
                        temporaryList.clear()
                    }
                }

        if (!temporaryList.isEmpty()) {
            val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(definition, temporaryList)

            val result = this.granularDatabaseService
                    .executeUpdateQuery(bulkInsertSql)
            results.add(result.successful)
        }

        return results.all { it }
    }

    override fun createOrUpdate(entity: Map<String, Any>, definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable() ||
            !entity.containsKey(this.sqlGeneratorService.javaIdName)) {
            return false
        }

        val foundItemInDatabase = this.get(entity[this.sqlGeneratorService.javaIdName] as String, definition)
        if (foundItemInDatabase.size > 0) {
            return this.update(entity, definition)
        }

        return this.create(entity, definition)
    }

    override fun create(entity: Map<String, Any>, definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable() ||
            !entity.containsKey(this.sqlGeneratorService.javaIdName)) {
            return false
        }
        // create
        val insertSql = this.sqlGeneratorService
                .buildInsertIntoTable(definition, entity) ?: return false

        val result = this.granularDatabaseService
                .executeUpdateQuery(insertSql)

        return result.successful
    }

    override fun update(entity: Map<String, Any>, definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()||
            !entity.containsKey(this.sqlGeneratorService.javaIdName)) {
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

    override fun updateWithCriteria(newValues: Map<String, Any>, whereClauseItem: WhereClauseItem, definition: DefinitionModel): Boolean {
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

    override fun delete(id: String, definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val deleteSql =
                this.sqlGeneratorService
                        .buildDeleteTable(definition, id as Any) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(deleteSql)
                .successful
    }

    override fun deleteAll(definition: DefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val sql = this.sqlGeneratorService.buildDeleteAll(definition)
        return this.granularDatabaseService
                .executeUpdateQuery(sql)
                .successful
    }
}
