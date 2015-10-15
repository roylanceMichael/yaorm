package org.roylance.yaorm.services

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.utilities.SqlOperators
import java.util.*

public class EntityAccessService(
        private val granularDatabaseService: IGranularDatabaseService,
        private val sqlGeneratorService: ISqlGeneratorService) : IEntityAccessService {

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
        // let's split this into items of 100 each... for now
        val temporaryList = ArrayList<T>()
        val results = ArrayList<Boolean>()

        instances
                .forEach {
                    temporaryList.add(it)

                    if (temporaryList.size() >= this.sqlGeneratorService.bulkInsertSize) {
                        val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(classModel, temporaryList)
                        val result = this.granularDatabaseService.executeUpdateQuery(bulkInsertSql)
                        results.add(result)
                        temporaryList.clear()
                    }
                }

        if (!temporaryList.isEmpty()) {
            val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(classModel, temporaryList)
            val result = this.granularDatabaseService.executeUpdateQuery(bulkInsertSql)
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
        val whereClause = HashMap<String, Any>()
        whereClause.put(this.sqlGeneratorService.javaIdName, id as Any)

        val whereSql = this.sqlGeneratorService.buildWhereClauseAnd(classModel, whereClause, SqlOperators.Equals)

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

    override fun <K, T: IEntity<K>> where(classModel: Class<T>, whereClause: Map<String, Any>, operator: String): List<T> {
        val whereSql =
                this.sqlGeneratorService.buildWhereClauseAnd(
                        classModel,
                        whereClause,
                        operator)

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
}