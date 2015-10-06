package org.roylance.yaorm.services

import com.google.common.base.Optional
import org.roylance.yaorm.utilities.SqlUtilities
import java.lang.reflect.Method
import java.util.*

public class EntityAccessService(
        private val granularDatabaseService: IGranularDatabaseService) : IEntityAccessService {

    private val maxTempListSize = 100

    override fun <T> deleteAll(classModel: Class<T>): Boolean {
        val sql = SqlUtilities.buildDeleteAll(classModel)
        return this.granularDatabaseService.executeUpdateQuery<T>(sql)
    }

    override fun <T> bulkInsert(classModel: Class<T>, instances: List<T>): Boolean {
        // let's split this into items of 100 each... for now
        val temporaryList = ArrayList<T>()
        val results = ArrayList<Boolean>()

        instances
                .forEach {
                    temporaryList.add(it)

                    if (temporaryList.size() >= maxTempListSize) {
                        val bulkInsertSql = SqlUtilities.buildBulkInsert(classModel, temporaryList)
                        val result = this.granularDatabaseService.executeUpdateQuery<T>(bulkInsertSql)
                        results.add(result)
                        temporaryList.clear()
                    }
                }

        if (!temporaryList.isEmpty()) {
            val bulkInsertSql = SqlUtilities.buildBulkInsert(classModel, temporaryList)
            val result = this.granularDatabaseService.executeUpdateQuery<T>(bulkInsertSql)
            results.add(result)
        }

        return results.all { it }
    }

    override fun <T> createOrUpdate(classModel: Class<T>, entity: T): Boolean {
        // this is tedious for now, I know...
        var idProperty: Method? = classModel
                .methods
                .filter {
                    it.name.equals(SqlUtilities.JavaGetIdName)
                }
                .firstOrNull() ?: return false

        val actualId = idProperty!!.invoke(entity)
        val foundItemInDatabase = this.get(classModel, actualId)

        if (foundItemInDatabase.isPresent) {
            // update
            val updateSql = SqlUtilities.buildUpdateTable(classModel, entity)
            if (!updateSql.isPresent) {
                return false
            }

            return this.granularDatabaseService.executeUpdateQuery<T>(updateSql.get())
        }

        // create
        val insertSql = SqlUtilities.buildInsertIntoTable(classModel, entity)

        if (!insertSql.isPresent) {
            return false
        }

        return this.granularDatabaseService.executeUpdateQuery<T>(insertSql.get())
    }

    override fun <T, K> get(classModel: Class<T>, id: K): Optional<T> {
        val whereClause = HashMap<String, K>()
        whereClause.put(SqlUtilities.JavaIdName, id)

        val whereSql = SqlUtilities.buildWhereClauseAnd(classModel, whereClause)

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

    override fun <T> getAll(classModel: Class<T>): List<T> {
        val allSql =
                SqlUtilities.buildSelectAll(classModel)

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                classModel,
                allSql)

        val returnItems = ArrayList<T>()

        while (resultSet.moveNext()) {
            returnItems.add(resultSet.getRecord())
        }

        return returnItems
    }

    override fun <T> where(classModel: Class<T>, whereClause: Map<String, Any>, operator: String): List<T> {
        val whereSql =
                SqlUtilities.buildWhereClauseAnd(classModel, whereClause, operator)

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

    override fun <T, K> delete(classModel: Class<T>, id: K): Boolean {
        val deleteSql =
                SqlUtilities.buildDeleteTable(classModel, id)

        if (!deleteSql.isPresent) {
            return false
        }

        return this.granularDatabaseService.executeUpdateQuery<T>(deleteSql.get())
    }

    override fun <T> instantiate(classModel: Class<T>): Boolean {
        val createTableSql =
                SqlUtilities.buildInitialTableCreate(classModel)

        if (!createTableSql.isPresent) {
            return false
        }

        this.granularDatabaseService.executeUpdateQuery<T>(createTableSql.get())

        return true
    }

    // for now, do nothing
    override fun close() {
    }
}