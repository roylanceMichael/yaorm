package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity

public interface IGranularDatabaseService : AutoCloseable {
    public fun executeUpdateQuery(query:String):Boolean
    public fun <K, T: IEntity<K>> executeSelectQuery(classModel:Class<T>, query:String): ICursor<T>
    public fun commit()
}
