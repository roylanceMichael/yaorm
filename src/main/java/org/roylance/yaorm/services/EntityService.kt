package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.IndexModel
import org.roylance.yaorm.utilities.SqlOperators
import java.util.*

public class EntityService<K, T: IEntity<K>>(
        public override val entityDefinition:Class<T>,
        private val granularDatabaseService: IGranularDatabaseService,
        private val sqlGeneratorService: ISqlGeneratorService,
        public override val indexDefinition: IndexModel? = null
) : IEntityService<K, T> {

    override fun getCustom(customSql: String): List<T> {
        val cursor = this.granularDatabaseService.executeSelectQuery(this.entityDefinition, customSql)

        val returnList = ArrayList<T>()

        while(cursor.moveNext()) {
            returnList.add(cursor.getRecord())
        }

        return returnList
    }

    override fun get(id: K): T? {
        val whereClause = WhereClauseItem(this.sqlGeneratorService.javaIdName, SqlOperators.Equals, id as Any)
        val whereSql = this.sqlGeneratorService
                .buildWhereClause(this.entityDefinition, whereClause) ?: return null

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                whereSql)

        if (resultSet.moveNext()) {
            val t:T = resultSet.getRecord()
            return t
        }

        return null
    }

    override fun getAll(): List<T> {
        val allSql =
                this.sqlGeneratorService.buildSelectAll(this.entityDefinition)

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                allSql)

        val returnItems = ArrayList<T>()

        while (resultSet.moveNext()) {
            returnItems.add(resultSet.getRecord())
        }

        return returnItems
    }

    override fun where(whereClauseItem: WhereClauseItem): List<T> {
        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        this.entityDefinition,
                        whereClauseItem) ?: return arrayListOf()

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                whereSql)

        val returnItems = ArrayList<T>()
        while (resultSet.moveNext()) {
            returnItems.add(resultSet.getRecord())
        }
        return returnItems
    }

    override fun bulkInsert(instances: List<T>): Boolean {
        // let's split this into items of n each... for now
        val temporaryList = ArrayList<T>()
        val results = ArrayList<Boolean>()

        instances
                .forEach {
                    temporaryList.add(it)

                    if (temporaryList.size >= this.sqlGeneratorService.bulkInsertSize) {
                        EntityAccessService.logger.info { "inserting ${temporaryList.size}" }
                        val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(this.entityDefinition, temporaryList)
                        val result = this.granularDatabaseService.executeUpdateQuery(bulkInsertSql)
                        EntityAccessService.logger.info { "inserted ${temporaryList.size}: result" }
                        results.add(result)
                        temporaryList.clear()
                    }
                }

        if (!temporaryList.isEmpty()) {
            EntityAccessService.logger.info { "inserting ${temporaryList.size}" }
            val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(this.entityDefinition, temporaryList)
            val result = this.granularDatabaseService.executeUpdateQuery(bulkInsertSql)
            EntityAccessService.logger.info { "inserted ${temporaryList.size}: $result" }
            results.add(result)
        }

        return results.all { it }
    }

    override fun createOrUpdate(entity: T): Boolean {
        val foundItemInDatabase = this.get(entity.id)
        if (foundItemInDatabase != null) {
            return this.update(entity)
        }
        return this.create(entity)
    }

    override fun create(entity: T): Boolean {
        // create
        val insertSql = this.sqlGeneratorService
                .buildInsertIntoTable(this.entityDefinition, entity) ?: return false
        return this.granularDatabaseService.executeUpdateQuery(insertSql)
    }

    override fun update(entity: T): Boolean {
        // update
        val updateSql = this
                .sqlGeneratorService
                .buildUpdateTable(this.entityDefinition, entity) ?: return false
        return this.granularDatabaseService.executeUpdateQuery(updateSql)
    }

    override fun updateWithCriteria(newValues: Map<String, Any>, whereClauseItem: WhereClauseItem): Boolean {
        val updateSql = this.sqlGeneratorService.buildUpdateWithCriteria(
                this.entityDefinition,
                newValues,
                whereClauseItem) ?: return false

        return this.granularDatabaseService.executeUpdateQuery(updateSql)
    }

    override fun delete(id: K): Boolean {
        val deleteSql =
                this.sqlGeneratorService
                        .buildDeleteTable(this.entityDefinition, id) ?: return false

        return this.granularDatabaseService.executeUpdateQuery(deleteSql)
    }

    override fun deleteAll(): Boolean {
        val sql = this.sqlGeneratorService.buildDeleteAll(this.entityDefinition)
        return this.granularDatabaseService.executeUpdateQuery(sql)
    }
}
