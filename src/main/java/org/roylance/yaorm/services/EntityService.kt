package org.roylance.yaorm.services

import org.roylance.yaorm.models.EntityCollection
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.db.GenericModel
import org.roylance.yaorm.models.entity.EntityDefinitionModel
import org.roylance.yaorm.models.migration.IndexModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.EntityUtils
import org.roylance.yaorm.utilities.SqlOperators
import java.util.*

class EntityService<K, T: IEntity<K>>(
        public override val entityDefinition: Class<T>,
        private val granularDatabaseService: IGranularDatabaseService,
        private val sqlGeneratorService: ISqlGeneratorService,
        public override val indexDefinition: IndexModel? = null
) : IEntityService<K, T> {

    private val foreignObjects:List<EntityDefinitionModel<*>>
    private val cachedStore: MutableMap<K, T> = HashMap()

    init {
        this.foreignObjects = EntityUtils
                .getAllForeignObjects(this.entityDefinition)
    }

    override var entityContext: EntityContext? = null

    override fun clearCache() {
        this.cachedStore.clear()
    }

    override fun getCount(): Long {
        val countSql = this.sqlGeneratorService.buildCountSql(this.entityDefinition)

        val cursor = this.granularDatabaseService
                .executeSelectQuery(GenericModel::class.java, countSql)
        val allRecords:List<GenericModel> = cursor.getRecords()

        if (allRecords.size > 0) {
            return allRecords[0].longVal
        }
        return -1
    }

    override fun createTable(): Boolean {
        val createTableSql = this.sqlGeneratorService
                .buildCreateTable(this.entityDefinition) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery<K>(createTableSql)
                .successful
    }

    override fun dropTable(): Boolean {
        val dropTableSql = this.sqlGeneratorService
            .buildDropTable(this.entityDefinition)

        return this.granularDatabaseService
                .executeUpdateQuery<K>(dropTableSql)
                .successful
    }

    override fun createIndex(indexModel: IndexModel): Boolean {
        val createIndexSql = this.sqlGeneratorService.buildCreateIndex(
                this.entityDefinition,
                indexModel.columnNames,
                indexModel.includeNames) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery<K>(createIndexSql)
                .successful
    }

    override fun dropIndex(indexModel: IndexModel): Boolean {
        val dropIndexSql = this.sqlGeneratorService.buildDropIndex(
                this.entityDefinition,
                indexModel.columnNames) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery<K>(dropIndexSql)
                .successful
    }

    override fun createColumn(propertyDefinitionModel: PropertyDefinitionModel): Boolean {
        val addColumnSql = this.sqlGeneratorService.buildCreateColumn(
                this.entityDefinition,
                propertyDefinitionModel.name,
                propertyDefinitionModel.type)
        if (addColumnSql != null) {
            return this.granularDatabaseService
                    .executeUpdateQuery<K>(addColumnSql)
                    .successful
        }
        return false
    }

    override fun dropColumn(propertyDefinitionModel: PropertyDefinitionModel): Boolean {
        val dropTableSqlStatements = this.sqlGeneratorService.buildDropColumn(
                this.entityDefinition,
                propertyDefinitionModel.name) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery<K>(dropTableSqlStatements)
                .successful
    }

    override fun getCustom(customSql: String): List<T> {
        return this.granularDatabaseService
                .executeSelectQuery(this.entityDefinition, customSql)
                .getRecords()
    }

    override fun get(id: K): T? {
        if (this.cachedStore.containsKey(id)) {
            return this.cachedStore[id]
        }

        val whereClause = WhereClauseItem(
                this.sqlGeneratorService.javaIdName,
                SqlOperators.Equals,
                id as Any)

        val whereSql = this.sqlGeneratorService
                .buildWhereClause(this.entityDefinition, whereClause) ?: return null

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                whereSql)

        val records:List<T> = resultSet.getRecords()

        if (records.size > 0) {
            val returnRecord = records[0]
            
            this.updateRecordWithForeignObjects(returnRecord)
            this.cachedStore[returnRecord.id] = returnRecord

            return returnRecord
        }

        return null
    }

    override fun getAll(): List<T> {
        val allSql =
                this.sqlGeneratorService.buildSelectAll(this.entityDefinition)

        val allObjects:List<T> = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                allSql)
                .getRecords()

        // todo: optimize
        allObjects
            .forEach { this.updateRecordWithForeignObjects(it) }

        return allObjects
    }

    override fun where(whereClauseItem: WhereClauseItem): List<T> {
        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        this.entityDefinition,
                        whereClauseItem) ?: return arrayListOf()

        val allObjects:List<T> = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                whereSql)
                .getRecords()

        allObjects
            .forEach {
                this.updateRecordWithForeignObjects(it)
            }

        return allObjects
    }

    // let's tackle this later
    override fun bulkInsert(instances: List<T>): Boolean {
        // let's split this into items of n each... for now
        val temporaryList = ArrayList<T>()
        val results = ArrayList<Boolean>()

        instances
                .forEach {
                    temporaryList.add(it)

                    if (temporaryList.size >= this.sqlGeneratorService.bulkInsertSize) {
                        val bulkInsertSql = this.sqlGeneratorService
                                .buildBulkInsert(this.entityDefinition, temporaryList)
                        val result = this.granularDatabaseService
                                .executeUpdateQuery<K>(bulkInsertSql)
                        results.add(result.successful)
                        temporaryList.clear()
                    }
                }

        if (!temporaryList.isEmpty()) {
            val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(this.entityDefinition, temporaryList)
            val result = this.granularDatabaseService
                    .executeUpdateQuery<K>(bulkInsertSql)
            results.add(result.successful)
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
        // handle foreign objects first
        this.createOrUpdateForeignObject(entity)

        // create
        val insertSql = this.sqlGeneratorService
                .buildInsertIntoTable(this.entityDefinition, entity) ?: return false
        val result = this.granularDatabaseService
                .executeUpdateQuery<K>(insertSql)

        if (result.generatedKeys != null &&
            result.generatedKeys!!.size > 0) {
            entity.id = result.generatedKeys!![0]
        }
        return result.successful
    }

    override fun update(entity: T): Boolean {
        this.createOrUpdateForeignObject(entity)
        // update
        val updateSql = this
                .sqlGeneratorService
                .buildUpdateTable(this.entityDefinition, entity) ?: return false

        val result = this.granularDatabaseService
                .executeUpdateQuery<K>(updateSql)

        return result.successful
    }

    override fun updateWithCriteria(
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): Boolean {
        val updateSql = this.sqlGeneratorService.buildUpdateWithCriteria(
                this.entityDefinition,
                newValues,
                whereClauseItem) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery<K>(updateSql)
                .successful
    }

    override fun delete(id: K): Boolean {
        val deleteSql =
                this.sqlGeneratorService
                        .buildDeleteTable(this.entityDefinition, id) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery<K>(deleteSql)
                .successful
    }

    override fun deleteAll(): Boolean {
        val sql = this.sqlGeneratorService.buildDeleteAll(this.entityDefinition)
        return this.granularDatabaseService
                .executeUpdateQuery<K>(sql)
                .successful
    }

    // todo: this needs more love
    private fun createOrUpdateForeignObject(actualObject: T) {
        if (this.entityContext != null) {
            this.foreignObjects
                .forEach {
                    val castToAny = it.entityDefinition as Class<IEntity<Any>>
                    val foreignService = this.entityContext!!
                            .getForeignService(castToAny) ?: return@forEach

                    val foreignObject = it.getMethod.invoke(actualObject) as IEntity<Any>?
                    if (foreignObject != null) {
                        foreignService.createOrUpdate(foreignObject)
                    }
                }
        }
    }

    // todo: this needs more love
    private fun updateRecordWithForeignObjects(actualObject:T) {
        // todo: optimize
        if (this.entityContext != null) {
            this.foreignObjects
                .forEach {
                    val foreignObject = it.getMethod.invoke(actualObject)
                            ?: return@forEach

                    val castToAny = it.entityDefinition as Class<IEntity<Any>>
                    val foreignService = this.entityContext
                            ?.getForeignService(castToAny) ?: return@forEach

                    if (foreignObject is EntityCollection<*>) {
                        // things I need to do:
                        // get the object on the foreign collection
                        var rootWhereClause:WhereClauseItem? = null
                        var cyclingWhereClause:WhereClauseItem? = null

                        foreignObject
                            .entityDefinition
                            .methods
                            .filter {
                                this.entityDefinition.equals(it.returnType)
                            }
                            .forEach {
                                val methodNameWithoutGet = it.name.substring(
                                        CommonSqlDataTypeUtilities.GetSetLength)

                                val newWhereClause = WhereClauseItem(
                                        CommonSqlDataTypeUtilities
                                                .lowercaseFirstChar(methodNameWithoutGet),
                                        WhereClauseItem.Equals,
                                        actualObject.id as Any)

                                if (rootWhereClause == null) {
                                    rootWhereClause = newWhereClause
                                    cyclingWhereClause = newWhereClause
                                }
                                else {
                                    cyclingWhereClause!!.connectingAndOr = WhereClauseItem.Or
                                    cyclingWhereClause!!.connectingWhereClause = newWhereClause
                                    cyclingWhereClause = newWhereClause
                                }
                            }

                        if (rootWhereClause == null) {
                            return@forEach
                        }
                        // find out which one column has a mapping to this one
                        // build a where statement and select all
                        // TODO: in memory store...
                        val allForeignObjects = foreignService.where(rootWhereClause!!)

                        return@forEach
                    }

                    val builtWhereClause = EntityUtils
                            .buildWhereClauseOnId((foreignObject as IEntity<*>))
                    val foreignObjects = foreignService.where(builtWhereClause)

                    if (foreignObjects.size > 0) {
                        it.setMethod.invoke(actualObject, foreignObjects[0])
                    }
                }
        }
    }
}
