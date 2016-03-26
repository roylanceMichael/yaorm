package org.roylance.yaorm.services

import org.roylance.yaorm.models.EntityCollection
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.db.GenericModel
import org.roylance.yaorm.models.entity.EntityDefinitionModel
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.IndexModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.EntityUtils
import org.roylance.yaorm.utilities.SqlOperators
import java.util.*

class EntityService<T: IEntity>(
        override val entityDefinition: Class<T>,
        private val granularDatabaseService: IGranularDatabaseService,
        private val sqlGeneratorService: ISqlGeneratorService,
        override val indexDefinition: IndexModel? = null
) : IEntityService<T> {

    private val definition: DefinitionModel
    private val currentDefinitions: List<EntityDefinitionModel<*>>
    private val foreignObjects:List<EntityDefinitionModel<*>>
    private val cachedStore: MutableMap<String, T> = HashMap()
    private val currentlyExecutingItems = HashSet<Int>()
    private var loadForeignObjects = false

    init {
        // get definitions
        this.currentDefinitions = EntityUtils.getProperties(this.entityDefinition.newInstance())

        this.foreignObjects = EntityUtils
                .getAllForeignObjects(this.entityDefinition)

        this.definition = EntityUtils.getDefinition(this.entityDefinition)
    }

    override var entityContext: EntityContext? = null

    override fun setToLoadForeignObjects() {
        this.loadForeignObjects = true
    }

    override fun setToUnloadForeignObjects() {
        this.loadForeignObjects = false
    }

    override fun updateCustom(customSql: String): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }
        this.granularDatabaseService.executeUpdateQuery(customSql)
        return true
    }

    override fun clearCache() {
        this.cachedStore.clear()
    }

    override fun getCount(): Long {
        if (!this.granularDatabaseService.isAvailable()) {
            return 0L
        }

        val countSql = this.sqlGeneratorService.buildCountSql(this.definition)

        val cursor = this.granularDatabaseService
                .executeSelectQuery(GenericModel::class.java, countSql)
        val allRecords:List<GenericModel> = cursor.getRecords()

        if (allRecords.size > 0) {
            return allRecords[0].longVal
        }
        return -1
    }

    override fun createTable(): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createTableSql = this.sqlGeneratorService
                .buildCreateTable(this.definition) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createTableSql)
                .successful
    }

    override fun dropTable(): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropTableSql = this.sqlGeneratorService
            .buildDropTable(this.definition)

        return this.granularDatabaseService
                .executeUpdateQuery(dropTableSql)
                .successful
    }

    override fun createIndex(indexModel: IndexModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val createIndexSql = this.sqlGeneratorService.buildCreateIndex(
                this.definition,
                indexModel.columnNames,
                indexModel.includeNames) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(createIndexSql)
                .successful
    }

    override fun dropIndex(indexModel: IndexModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropIndexSql = this.sqlGeneratorService.buildDropIndex(
                this.definition,
                indexModel.columnNames) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(dropIndexSql)
                .successful
    }

    override fun createColumn(propertyDefinitionModel: PropertyDefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val addColumnSql = this.sqlGeneratorService.buildCreateColumn(
                this.definition,
                propertyDefinitionModel) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(addColumnSql)
                .successful
    }

    override fun dropColumn(propertyDefinitionModel: PropertyDefinitionModel): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val dropTableSqlStatements = this.sqlGeneratorService.buildDropColumn(
                this.definition,
                propertyDefinitionModel) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(dropTableSqlStatements)
                .successful
    }

    override fun getCustom(customSql: String): List<T> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }

        val foundRecords: List<T> = this.granularDatabaseService
                .executeSelectQuery(this.entityDefinition, customSql)
                .getRecords()

        val returnRecords = ArrayList<T>()
        foundRecords
            .forEach {
                if (this.cachedStore.containsKey(it.id)) {
                    returnRecords.add(this.cachedStore[it.id]!!)
                }
                else {
                    this.cachedStore[it.id] = it
                    this.updateRecordWithForeignObjects(it)
                    returnRecords.add(it)
                    this.cachedStore[it.id] = it
                }
            }

        return returnRecords
    }

    override fun get(id: String): T? {
        if (!this.granularDatabaseService.isAvailable()) {
            return null
        }

        if (this.cachedStore.containsKey(id)) {
            return this.cachedStore[id]
        }

        val whereClause = WhereClauseItem(
                this.sqlGeneratorService.javaIdName,
                SqlOperators.Equals,
                id as Any)

        val whereSql = this.sqlGeneratorService
                .buildWhereClause(this.definition, whereClause) ?: return null

        val resultSet = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                whereSql)

        val records:List<T> = resultSet.getRecords()

        if (records.size > 0) {
            val returnRecord = records[0]

            this.cachedStore[returnRecord.id] = returnRecord
            this.updateRecordWithForeignObjects(returnRecord)
            this.cachedStore[returnRecord.id] = returnRecord

            return returnRecord
        }

        return null
    }

    override fun getMany(n: Int): List<T> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }

        val allSql =
                this.sqlGeneratorService.buildSelectAll(this.definition, n)

        val allObjects:List<T> = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition, allSql)
                .getRecords()

        val returnObjects = ArrayList<T>()
        allObjects
            .forEach {
                if (!this.cachedStore.containsKey(it.id)) {
                    this.cachedStore[it.id] = it
                    this.updateRecordWithForeignObjects(it)
                    returnObjects.add(it)
                    this.cachedStore[it.id] = it
                }
                else {
                    returnObjects.add(this.cachedStore[it.id]!!)
                }
            }

        return returnObjects
    }

    override fun where(whereClauseItem: WhereClauseItem): List<T> {
        if (!this.granularDatabaseService.isAvailable()) {
            return ArrayList()
        }

        val whereSql =
                this.sqlGeneratorService.buildWhereClause(
                        this.definition,
                        whereClauseItem) ?: return arrayListOf()

        val allObjects:List<T> = this.granularDatabaseService.executeSelectQuery(
                this.entityDefinition,
                whereSql)
                .getRecords()

        val returnRecords = ArrayList<T>()
        allObjects
            .forEach {
                if (this.cachedStore.containsKey(it.id)) {
                    returnRecords.add(this.cachedStore[it.id]!!)
                }
                else {
                    this.cachedStore[it.id] = it
                    this.updateRecordWithForeignObjects(it)
                    returnRecords.add(it)
                    this.cachedStore[it.id] = it
                }
            }

        return allObjects
    }

    // let's tackle this later
    override fun bulkInsert(instances: List<T>): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        // let's split this into items of n each... for now
        val temporaryList = ArrayList<T>()
        val results = ArrayList<Boolean>()

        instances
            .forEach {
                temporaryList.add(it)

                if (temporaryList.size >= this.sqlGeneratorService.bulkInsertSize) {
                    val mapsFromObjects = EntityUtils.getMapsFromObjects(this.currentDefinitions, temporaryList)

                    val bulkInsertSql = this.sqlGeneratorService
                            .buildBulkInsert(this.definition, mapsFromObjects)

                    val result = this.granularDatabaseService
                            .executeUpdateQuery(bulkInsertSql)
                    results.add(result.successful)
                    temporaryList.clear()
                }
            }

        if (!temporaryList.isEmpty()) {
            val mapsFromObjects = EntityUtils.getMapsFromObjects(this.currentDefinitions, temporaryList)
            val bulkInsertSql = this.sqlGeneratorService.buildBulkInsert(this.definition, mapsFromObjects)

            val result = this.granularDatabaseService
                    .executeUpdateQuery(bulkInsertSql)
            results.add(result.successful)
        }

        return results.all { it }
    }

    override fun createOrUpdate(entity: T): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val foundItemInDatabase = this.get(entity.id)
        if (foundItemInDatabase != null) {
            return this.update(entity)
        }
        return this.create(entity)
    }

    override fun create(entity: T): Boolean {

        if (this.currentlyExecutingItems.contains(entity.hashCode())) {
            return true
        }

        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        this.currentlyExecutingItems.add(entity.hashCode())

        try {
            // handle foreign objects first
            this.createOrUpdateForeignObject(entity)

            val mappedObject = EntityUtils.getMapFromObject(this.currentDefinitions, entity)

            // create
            val insertSql = this.sqlGeneratorService
                    .buildInsertIntoTable(this.definition, mappedObject) ?: return false

            val result = this.granularDatabaseService
                    .executeUpdateQuery(insertSql)

            if (result.generatedKeys != null &&
                    result.generatedKeys!!.size > 0) {
                entity.id = result.generatedKeys!![0]
            }

            this.createOrUpdateForeignObjectCollections(entity)
            this.cachedStore[entity.id] = entity

            return result.successful
        }
        finally {
            this.currentlyExecutingItems.remove(entity.hashCode())
        }
    }

    override fun update(entity: T): Boolean {
        if (this.currentlyExecutingItems.contains(entity.hashCode())) {
            return true
        }

        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        this.currentlyExecutingItems.add(entity.hashCode())

        try {
            this.createOrUpdateForeignObject(entity)

            val objectMap = EntityUtils.getMapFromObject(this.currentDefinitions, entity)

            // update
            val updateSql = this
                    .sqlGeneratorService
                    .buildUpdateTable(this.definition, objectMap) ?: return false

            val result = this.granularDatabaseService
                    .executeUpdateQuery(updateSql)

            this.createOrUpdateForeignObjectCollections(entity)
            this.cachedStore[entity.id] = entity

            return result.successful
        }
        finally {
            this.currentlyExecutingItems.remove(entity.hashCode())
        }
    }

    override fun updateWithCriteria(
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        val updateSql = this.sqlGeneratorService.buildUpdateWithCriteria(
                this.definition,
                newValues,
                whereClauseItem) ?: return false

        this.cachedStore.clear()

        return this.granularDatabaseService
                .executeUpdateQuery(updateSql)
                .successful
    }

    override fun delete(id: String): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        if (this.cachedStore.containsKey(id)) {
            this.cachedStore.remove(id)
        }

        val deleteSql =
                this.sqlGeneratorService
                        .buildDeleteTable(this.definition, id as Any) ?: return false

        return this.granularDatabaseService
                .executeUpdateQuery(deleteSql)
                .successful
    }

    override fun deleteAll(): Boolean {
        if (!this.granularDatabaseService.isAvailable()) {
            return false
        }

        this.cachedStore.clear()
        val sql = this.sqlGeneratorService.buildDeleteAll(this.definition)
        return this.granularDatabaseService
                .executeUpdateQuery(sql)
                .successful
    }

    private fun createOrUpdateForeignObjectCollections(actualObject: T) {
        if (!this.loadForeignObjects) {
            return
        }

        if (this.entityContext != null) {
            this.foreignObjects
                .filter { EntityDefinitionModel.List.equals(it.type) }
                .forEach {
                        // we're dealing with a list
                        val entityCollection = it.getMethod.invoke(actualObject) as EntityCollection<IEntity>? ?: return@forEach

                        // get first object
                        if (entityCollection.size == 0) {
                            return@forEach
                        }

                        val firstObject = entityCollection.first()
                        val foreignService = this.entityContext!!
                                .getForeignService(firstObject.javaClass) ?: return@forEach

                        // get the setter method for the child
                        val commonFirstWord = CommonSqlDataTypeUtilities.getFirstWordInProperty(it.propertyName)

                        val foundSetter = firstObject.javaClass
                                .methods
                                .filter {
                                    if(!it.name.startsWith(CommonSqlDataTypeUtilities.Set)) {
                                        false
                                    }
                                    else {
                                        val nameWithoutSet = it.name.substring(
                                                CommonSqlDataTypeUtilities.GetSetLength)
                                        val commonWord = CommonSqlDataTypeUtilities.getFirstWordInProperty(nameWithoutSet)
                                        if (commonFirstWord.equals(commonWord)) {
                                            true
                                        }
                                        else {
                                            false
                                        }
                                    }
                                }
                                .firstOrNull() ?: return@forEach

                        entityCollection
                                .forEach { childItem ->
                                    foundSetter.invoke(childItem, actualObject)
                                    foreignService.createOrUpdate(childItem)
                                }
                }
        }
    }
    // todo: this needs more love
    private fun createOrUpdateForeignObject(actualObject: T) {
        if (!this.loadForeignObjects) {
            return
        }

        if (this.entityContext != null) {
            this.foreignObjects
                .filter { EntityDefinitionModel.Single.equals(it.type) }
                .forEach {
                    // not going to update single items.. for now
                    val castToAny = it.entityDefinition as Class<IEntity>

                    val foreignService = this.entityContext!!
                            .getForeignService(castToAny) ?: return@forEach

                    val foreignObject = it.getMethod.invoke(actualObject) as IEntity?
                    if (foreignObject != null) {
                        foreignService.createOrUpdate(foreignObject)
                    }
                }
        }
    }

    // todo: this needs more love
    private fun updateRecordWithForeignObjects(actualObject:T) {
        if (!this.loadForeignObjects) {
            return
        }

        // todo: optimize
        if (this.entityContext != null) {
            this.foreignObjects
                .forEach {
                    if (EntityDefinitionModel.Single.equals(it.type)) {
                        val foreignObject = it.getMethod.invoke(actualObject)
                                ?: return@forEach

                        val castToAny = it.entityDefinition as Class<IEntity>
                        val foreignService = this.entityContext
                                ?.getForeignService(castToAny) ?: return@forEach

                        val builtWhereClause = EntityUtils
                                .buildWhereClauseOnId((foreignObject as IEntity))
                        val foreignObjects = foreignService.where(builtWhereClause)

                        if (foreignObjects.size > 0) {
                            it.setMethod.invoke(actualObject, foreignObjects[0])
                        }
                    }
                    else {
                        val foreignObject = it.getMethod.invoke(actualObject)
                                as EntityCollection<IEntity>? ?: return@forEach
                        // should never be null, but just in case

                        val foreignService = this.entityContext
                                ?.getForeignService(foreignObject.entityDefinition) ?: return@forEach

                        // how does this collection tie to the children?
                        // the children will have a reference to this id
                        // but what name...
                        // let's enforce a common string between then
                        val propertyToLookFor = CommonSqlDataTypeUtilities
                                .getFirstWordInProperty(it.propertyName)

                        val foundCorrespondingProperty = foreignObject.entityDefinition
                                .methods
                                .filter {
                                    val containsGet = it.name.startsWith(CommonSqlDataTypeUtilities.Get)
                                    if (!containsGet) {
                                        false
                                    } else {
                                        val propertyName = it.name
                                                .substring(CommonSqlDataTypeUtilities.GetSetLength)
                                        propertyToLookFor.equals(
                                                CommonSqlDataTypeUtilities
                                                        .getFirstWordInProperty(propertyName))
                                    }
                                }
                                .firstOrNull() ?: return@forEach

                        val cleansedPropertyName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                foundCorrespondingProperty
                                .name
                                .substring(CommonSqlDataTypeUtilities.GetSetLength))

                        // need to get the name of this property
                        val whereClause = WhereClauseItem(
                                cleansedPropertyName,
                                WhereClauseItem.Equals,
                                actualObject.id as Any)

                        val childObjects = foreignService.where(whereClause)
                        foreignObject.addAll(childObjects)
                    }
                }
        }
    }
}
