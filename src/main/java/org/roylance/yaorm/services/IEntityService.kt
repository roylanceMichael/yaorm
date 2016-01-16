package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.IndexModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel

interface IEntityService<K, T: IEntity<K>> {
    var entityContext:EntityContext?

    val entityDefinition:Class<T>
    val indexDefinition: IndexModel?

    fun setToLoadForeignObjects()
    fun setToUnloadForeignObjects()

    fun createTable(): Boolean
    fun dropTable(): Boolean

    fun createIndex(indexModel: IndexModel): Boolean
    fun dropIndex(indexModel: IndexModel): Boolean

    fun createColumn(propertyDefinitionModel: PropertyDefinitionModel): Boolean
    fun dropColumn(propertyDefinitionModel: PropertyDefinitionModel): Boolean

    fun getCount(): Long

    fun getCustom(customSql: String): List<T>
    fun get(id: K): T?
    fun getMany(n: Int=1000): List<T>
    fun where(whereClauseItem: WhereClauseItem): List<T>

    fun bulkInsert(instances: List<T>): Boolean
    fun createOrUpdate(entity: T): Boolean
    fun create(entity: T): Boolean
    fun update(entity: T): Boolean
    fun updateWithCriteria(
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): Boolean

    fun updateCustom(customSql: String): Boolean

    fun delete(id: K): Boolean
    fun deleteAll(): Boolean

    fun clearCache()
}
