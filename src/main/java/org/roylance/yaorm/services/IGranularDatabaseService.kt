package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity

public interface IGranularDatabaseService {
    public fun executeUpdateQuery(query:String):Boolean
    public fun <K, T: IEntity<K>> executeSelectQuery(classModel:Class<T>, query:String): ICursor<T>
}
