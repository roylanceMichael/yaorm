package org.roylance.yaorm.services.entity

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.YaormModel

interface IEntityService<T: IEntity> {
    var entityContext:EntityContext?

    val entityDefinition:Class<T>
    val indexDefinition: YaormModel.Index?

    fun setToLoadForeignObjects()
    fun setToUnloadForeignObjects()

    fun createTable(): Boolean
    fun dropTable(): Boolean

    fun createIndex(index: YaormModel.Index): Boolean
    fun dropIndex(index: YaormModel.Index): Boolean

    fun createColumn(propertyDefinition: YaormModel.PropertyDefinition): Boolean
    fun dropColumn(propertyDefinition: YaormModel.PropertyDefinition): Boolean

    fun getCount(): Long

    fun getCustom(customSql: String): List<T>
    fun get(id: String): T?
    fun getMany(n: Int=1000): List<T>
    fun where(whereClauseItem: YaormModel.WhereClauseItem): List<T>

    fun bulkInsert(instances: List<T>): Boolean
    fun createOrUpdate(entity: T): Boolean
    fun create(entity: T): Boolean
    fun update(entity: T): Boolean
    fun updateWithCriteria(
            newValues: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClauseItem): Boolean

    fun updateCustom(customSql: String): Boolean

    fun delete(id: String): Boolean
    fun deleteAll(): Boolean

    fun clearCache()
}
