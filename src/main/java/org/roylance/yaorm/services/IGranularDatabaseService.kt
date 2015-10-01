package org.roylance.yaorm.services

public interface IGranularDatabaseService {
    public fun executeUpdateQuery<T>(query:String):Boolean
    public fun executeSelectQuery<T>(classModel:Class<T>, query:String): ICursor<T>
}
