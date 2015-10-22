package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IGranularDatabaseService
import java.sql.Connection

public class JDBCGranularDatabaseService(
        private val connection: Connection) : IGranularDatabaseService {
    override fun executeUpdateQuery(query: String): Boolean {
        val statement = this.connection.createStatement()
        return statement.executeUpdate(query) > 0
    }

    override fun <K, T: IEntity<K>> executeSelectQuery(classModel:Class<T>, query: String): ICursor<T> {
        val statement = this.connection.createStatement()
        val resultSet = statement.executeQuery(query)
        return JDBCCursor(classModel, resultSet)
    }
}