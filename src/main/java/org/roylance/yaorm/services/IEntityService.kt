package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.IndexModel

public interface IEntityService<K, T: IEntity<K>> {
    val entityDefinition:Class<T>
    val indexDefinition: IndexModel?

    fun getCustom(customSql: String): List<T>
    fun get(id: K): T?
    fun getAll(): List<T>
    fun where(whereClauseItem: WhereClauseItem): List<T>

    fun bulkInsert(instances: List<T>): Boolean
    fun createOrUpdate(entity: T): Boolean
    fun create(entity: T): Boolean
    fun update(entity: T): Boolean
    fun updateWithCriteria(newValues: Map<String, Any>, whereClauseItem: WhereClauseItem): Boolean

    fun delete(id: K): Boolean
    fun deleteAll(): Boolean
}
