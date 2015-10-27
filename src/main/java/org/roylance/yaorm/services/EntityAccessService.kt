package org.roylance.yaorm.services

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.utilities.SqlOperators
import java.util.*
import java.util.logging.Logger

public class EntityAccessService(
        private val granularDatabaseService: IGranularDatabaseService,
        private val sqlGeneratorService: ISqlGeneratorService) : IEntityAccessService {
    override fun <K, T : IEntity<K>> updateWithCriteria(
            classModel: Class<T>,
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): Boolean {
        val updateSql = this.sqlGeneratorService.buildUpdateWithCriteria(classModel, newValues, whereClauseItem)
        if (!updateSql.isPresent) {
            return false
        }
        return this.granularDatabaseService.executeUpdateQuery(updateSql.get())
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
        val insertSql = this.sqlGeneratorService.buildInsertIntoTable(classModel, entity)

        if (!insertSql.isPresent) {
            return false
        }

        return this.granularDatabaseService.executeUpdateQuery(insertSql.get())
    }

    override fun <K, T: IEntity<K>> update(classModel: Class<T>, entity: T): Boolean {
        // update
        val updateSql = this.sqlGeneratorService.buildUpdateTable(classModel, entity)
        if (!updateSql.isPresent) {
            return false
        }

        return this.granularDatabaseService.executeUpdateQuery(updateSql.get())
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
        if (foundItemInDatabase.isPresent) {
            return this.update(classModel, entity)
        }
        return this.create(classModel, entity)
    }

    override fun <K, T: IEntity<K>> get(classModel: Class<T>, id: K): Optional<T> {
        val whereClause = WhereClauseItem(this.sqlGeneratorService.javaIdName, SqlOperators.Equals, id as Any)
        val whereSql = this.sqlGeneratorService.buildWhereClause(classModel, whereClause)

        if (!whereSql.isPresent) {
            return Optional.absent()
        }

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                classModel,
                whereSql.get())

        if (resultSet.moveNext()) {
            return Optional.of(resultSet.getRecord())
        }
        return Optional.absent()
    }

    override fun <K, T: IEntity<K>> getAll(classModel: Class<T>): List<T> {
        val allSql =
                this.sqlGeneratorService.buildSelectAll(classModel)

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                classModel,
                allSql)

        val returnItems = ArrayList<T>()

        while (resultSet.moveNext()) {
            returnItems.add(resultSet.getRecord())
        }

        return returnItems
    }

    override fun <K, T: IEntity<K>> where(classModel: Class<T>, whereClauseItem: WhereClauseItem): List<T> {
        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        classModel,
                        whereClauseItem)

        if (!whereSql.isPresent) {
            return arrayListOf()
        }
        val resultSet = this.granularDatabaseService.executeSelectQuery(
                classModel,
                whereSql.get())
        val returnItems = ArrayList<T>()
        while (resultSet.moveNext()) {
            returnItems.add(resultSet.getRecord())
        }
        return returnItems
    }

    override fun <K, T: IEntity<K>> delete(classModel: Class<T>, id: K): Boolean {
        val deleteSql =
                this.sqlGeneratorService.buildDeleteTable(classModel, id)

        if (!deleteSql.isPresent) {
            return false
        }

        return this.granularDatabaseService.executeUpdateQuery(deleteSql.get())
    }

    override fun <K, T: IEntity<K>> instantiate(classModel: Class<T>): Boolean {
        val createTableSql =
                this.sqlGeneratorService.buildInitialTableCreate(classModel)
        if (!createTableSql.isPresent) {
            return false
        }
        this.granularDatabaseService.executeUpdateQuery(createTableSql.get())
        return true
    }

    // for now, do nothing
    override fun close() {
    }

    companion object {
        val logger = Logger.getLogger(EntityAccessService::class.java.simpleName)
    }
}