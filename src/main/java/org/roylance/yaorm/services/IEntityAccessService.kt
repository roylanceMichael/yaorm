package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import java.io.Closeable

public interface IEntityAccessService : Closeable {
    public fun <K, T: IEntity<K>> instantiate(classModel: Class<T>): Boolean
    public fun <K, T: IEntity<K>> drop(classModel: Class<T>): Boolean

    public fun <K, T: IEntity<K>> createIndex(classModel: Class<T>, columns: List<String>, includes: List<String>): Boolean
    public fun <K, T: IEntity<K>> dropIndex(classModel: Class<T>, columns: List<String>): Boolean

    public fun <K, T: IEntity<K>> bulkInsert(classModel: Class<T>, instances: List<T>): Boolean
    public fun <K, T: IEntity<K>> createOrUpdate(classModel: Class<T>, entity: T): Boolean
    public fun <K, T: IEntity<K>> create(classModel: Class<T>, entity: T): Boolean
    public fun <K, T: IEntity<K>> update(classModel: Class<T>, entity: T): Boolean
    public fun <K, T: IEntity<K>> updateWithCriteria(classModel: Class<T>, newValues: Map<String, Any>, whereClauseItem: WhereClauseItem): Boolean

    public fun <K, T: IEntity<K>> getCustom(classModel: Class<T>, customSql: String): List<T>
    public fun <K, T: IEntity<K>> get(classModel: Class<T>, id: K): T?
    public fun <K, T: IEntity<K>> getAll(classModel: Class<T>): List<T>
    public fun <K, T: IEntity<K>> where(classModel: Class<T>, whereClauseItem: WhereClauseItem): List<T>

    public fun <K, T: IEntity<K>> delete(classModel: Class<T>, id: K): Boolean
    public fun <K, T: IEntity<K>> deleteAll(classModel: Class<T>): Boolean
}