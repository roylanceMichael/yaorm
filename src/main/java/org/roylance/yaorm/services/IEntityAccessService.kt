package org.roylance.yaorm.services

import com.google.common.base.Optional
import org.roylance.yaorm.utilities.SqlOperators
import java.io.Closeable

public interface IEntityAccessService : Closeable {
    public fun <T> instantiate(classModel: Class<T>): Boolean
    public fun <T> createOrUpdate(classModel: Class<T>, entity: T): Boolean
    public fun <T, K> get(classModel: Class<T>, id: K): Optional<T>
    public fun <T> getAll(classModel: Class<T>): List<T>
    public fun <T> where(classModel: Class<T>, whereClause: Map<String, Any>, operator:String= SqlOperators.Equals): List<T>
    public fun <T, K> delete(classModel: Class<T>, id: K): Boolean
    public fun <T> deleteAll(classModel: Class<T>): Boolean
    public fun <T> bulkInsert(classModel: Class<T>, instances: List<T>): Boolean
}