package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.entity.EntityResultModel

interface IGranularDatabaseService : AutoCloseable {
    fun isAvailable(): Boolean
    fun <K> executeUpdateQuery(query:String): EntityResultModel<K>
    fun <K, T: IEntity<K>> executeSelectQuery(classModel:Class<T>, query:String): ICursor<T>
    fun commit()
}
