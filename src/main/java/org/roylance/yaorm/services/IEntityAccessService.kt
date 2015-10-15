package org.roylance.yaorm.services

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.utilities.SqlOperators
import java.io.Closeable

public interface IEntityAccessService : Closeable {
    public fun <K, T: IEntity<K>> instantiate(classModel: Class<T>): Boolean
    public fun <K, T: IEntity<K>> drop(classModel: Class<T>): Boolean

    public fun <K, T: IEntity<K>> bulkInsert(classModel: Class<T>, instances: List<T>): Boolean
    public fun <K, T: IEntity<K>> createOrUpdate(classModel: Class<T>, entity: T): Boolean
    public fun <K, T: IEntity<K>> create(classModel: Class<T>, entity: T): Boolean
    public fun <K, T: IEntity<K>> update(classModel: Class<T>, entity: T): Boolean

    public fun <K, T: IEntity<K>> get(classModel: Class<T>, id: K): Optional<T>
    public fun <K, T: IEntity<K>> getAll(classModel: Class<T>): List<T>
    public fun <K, T: IEntity<K>> where(classModel: Class<T>, whereClause: Map<String, Any>, operator:String=SqlOperators.Equals): List<T>

    public fun <K, T: IEntity<K>> delete(classModel: Class<T>, id: K): Boolean
    public fun <K, T: IEntity<K>> deleteAll(classModel: Class<T>): Boolean
}