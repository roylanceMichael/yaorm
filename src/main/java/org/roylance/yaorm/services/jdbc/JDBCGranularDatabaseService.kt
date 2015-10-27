package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IGranularDatabaseService
import java.sql.Connection

public class JDBCGranularDatabaseService(
        private val connection: Connection,
        private val shouldCommit: Boolean) : IGranularDatabaseService {
    override fun commit() {
        this.connection.commit()
    }

    override fun close() {
        if (!this.connection.isClosed) {
            this.connection.close()
        }
    }

    override fun executeUpdateQuery(query: String): Boolean {
        try {
            val statement = this.connection.createStatement()
            return statement.executeUpdate(query) > 0
        }
        finally {
            if (this.shouldCommit) {
                this.connection.commit()
            }
        }
    }

    override fun <K, T: IEntity<K>> executeSelectQuery(classModel:Class<T>, query: String): ICursor<T> {
        val statement = this.connection.prepareStatement(query)
        val resultSet = statement.executeQuery()
        return JDBCCursor(classModel, resultSet)
    }
}