package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity

interface IGranularDatabaseService : AutoCloseable {
    fun executeUpdateQuery(query:String):Boolean
    fun <K, T: IEntity<K>> executeSelectQuery(classModel:Class<T>, query:String): ICursor<T>
    fun commit()
}
