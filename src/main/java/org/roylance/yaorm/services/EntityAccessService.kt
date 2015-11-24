package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.utilities.SqlOperators
import java.util.*
import java.util.logging.Logger

class EntityAccessService(
        private val granularDatabaseService: IGranularDatabaseService,
        private val sqlGeneratorService: ISqlGeneratorService) : IEntityAccessService {

    override fun <K, T : IEntity<K>> getCustom(classModel: Class<T>, customSql: String): List<T> {
        return this.granularDatabaseService
                .executeSelectQuery(classModel, customSql)
                .getRecords()
    }

    override fun <K, T : IEntity<K>> createIndex(classModel: Class<T>, columns: List<String>, includes: List<String>): Boolean {
        val createIndexSql = this.sqlGeneratorService.buildCreateIndex(classModel, columns, includes)
        if (createIndexSql != null) {
            this.granularDatabaseService.executeUpdateQuery(createIndexSql)
            return true
        }
        return false
    }

    override fun <K, T : IEntity<K>> dropIndex(classModel: Class<T>, columns: List<String>): Boolean {
        val dropIndexSql = this.sqlGeneratorService.buildDropIndex(classModel, columns)
        if (dropIndexSql != null) {
            this.granularDatabaseService.executeUpdateQuery(dropIndexSql)
            return true
        }
        return false
    }

    override fun <K, T : IEntity<K>> updateWithCriteria(
            classModel: Class<T>,
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): Boolean {
        val updateSql = this.sqlGeneratorService.buildUpdateWithCriteria(
                classModel,
                newValues,
                whereClauseItem) ?: return false
        return this.granularDatabaseService.executeUpdateQuery(updateSql)
    }

    override fun <K, T : IEntity<K>> drop(classModel: Class<T>): Boolean {
        val sql = this.sqlGeneratorService.buildDropTable(classModel)
        return this.granularDatabaseService.executeUpdateQuery(sql)
    }

    override fun <K, T: IEntity<K>> deleteAll(classModel: Class<T>): Boolean {
        val sql = this.sqlGeneratorService.buildDeleteAll(classModel)
        return this.granularDatabaseService.executeUpdateQuery(sql)
    }

    override fun <K, T: IEntity<K>> create(classModel: Class<T>, entity: T): Boolean {
        // create
        val insertSql = this.sqlGeneratorService.buildInsertIntoTable(classModel, entity) ?: return false

        return this.granularDatabaseService.executeUpdateQuery(insertSql)
    }

    override fun <K, T: IEntity<K>> update(classModel: Class<T>, entity: T): Boolean {
        // update
        val updateSql = this
                .sqlGeneratorService
                .buildUpdateTable(classModel, entity) ?: return false

        return this.granularDatabaseService.executeUpdateQuery(updateSql)
    }

    override fun <K, T: IEntity<K>> bulkInsert(classModel: Class<T>, instances: List<T>): Boolean {
        // let's split this into items of n each... for now
        val temporaryList = ArrayList<T>()
        val results = ArrayList<Boolean>()

        instances
                .forEach {
                    temporaryList.add(it)

                    if (temporaryList.size >= this.sqlGeneratorService.bulkInsertSize) {
                        logger.info { "inserting ${temporaryList.size}" }
                        val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(classModel, temporaryList)
                        val result = this.granularDatabaseService.executeUpdateQuery(bulkInsertSql)
                        logger.info { "inserted ${temporaryList.size}: result" }
                        results.add(result)
                        temporaryList.clear()
                    }
                }

        if (!temporaryList.isEmpty()) {
            logger.info { "inserting ${temporaryList.size}" }
            val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(classModel, temporaryList)
            val result = this.granularDatabaseService.executeUpdateQuery(bulkInsertSql)
            logger.info { "inserted ${temporaryList.size}: $result" }
            results.add(result)
        }

        return results.all { it }
    }

    override fun <K, T: IEntity<K>> createOrUpdate(classModel: Class<T>, entity: T): Boolean {
        val foundItemInDatabase = this.get(classModel, entity.id)
        if (foundItemInDatabase != null) {
            return this.update(classModel, entity)
        }
        return this.create(classModel, entity)
    }

    override fun <K, T: IEntity<K>> get(classModel: Class<T>, id: K): T? {
        val whereClause = WhereClauseItem(this.sqlGeneratorService.javaIdName, SqlOperators.Equals, id as Any)
        val whereSql = this.sqlGeneratorService
                .buildWhereClause(classModel, whereClause) ?: return null

        val records:List<T> = this.granularDatabaseService.executeSelectQuery(
                classModel,
                whereSql)
                .getRecords()

        if (records.size > 0) {
            return records[0]
        }
        return null
    }

    override fun <K, T: IEntity<K>> getAll(classModel: Class<T>): List<T> {
        val allSql =
                this.sqlGeneratorService.buildSelectAll(classModel)

        return this.granularDatabaseService.executeSelectQuery(
                classModel,
                allSql)
                .getRecords()
    }

    override fun <K, T: IEntity<K>> where(classModel: Class<T>, whereClauseItem: WhereClauseItem): List<T> {
        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        classModel,
                        whereClauseItem) ?: return arrayListOf()

        return this.granularDatabaseService.executeSelectQuery(
                classModel,
                whereSql)
                .getRecords()
    }

    override fun <K, T: IEntity<K>> delete(classModel: Class<T>, id: K): Boolean {
        val deleteSql =
                this.sqlGeneratorService
                        .buildDeleteTable(classModel, id) ?: return false

        return this.granularDatabaseService.executeUpdateQuery(deleteSql)
    }

    override fun <K, T: IEntity<K>> instantiate(classModel: Class<T>): Boolean {
        val createTableSql =
                this.sqlGeneratorService
                        .buildCreateTable(classModel) ?: return false
        this.granularDatabaseService.executeUpdateQuery(createTableSql)
        return true
    }

    // for now, do nothing
    override fun close() {
    }

    companion object {
        val logger = Logger.getLogger(EntityAccessService::class.java.simpleName)
    }
}