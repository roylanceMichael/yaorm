package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.entity.EntityResultModel

interface IGranularDatabaseService : AutoCloseable {
    fun isAvailable(): Boolean
    fun executeUpdateQuery(query:String): EntityResultModel
    fun <T: IEntity> executeSelectQuery(classModel:Class<T>, query:String): ICursor<T>
    fun commit()
}
