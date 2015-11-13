package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IGranularDatabaseService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.sql.Connection
import java.util.*
import java.util.logging.Logger

public class JDBCGranularDatabaseService(
        private val connection: Connection,
        private val shouldManuallyCommitAfterUpdate: Boolean) : IGranularDatabaseService {

    override fun commit() {
        this.connection.commit()
    }

    override fun close() {
        if (!this.connection.isClosed) {
            this.connection.close()
        }
    }

    override fun executeUpdateQuery(query: String): Boolean {
        val statement = this.connection.createStatement()
        try {
            return statement.executeUpdate(query) > 0
        }
        finally {
            if (this.shouldManuallyCommitAfterUpdate) {
                this.connection.commit()
            }
            statement.close()
        }
    }

    override fun <K, T: IEntity<K>> executeSelectQuery(classModel:Class<T>, query: String): ICursor<T> {
        val statement = this.connection.prepareStatement(query)
        val resultSet = statement.executeQuery()
        return JDBCCursor(classModel, resultSet, statement)
    }

    companion object {
        val logger = Logger.getLogger(JDBCGranularDatabaseService::class.java.simpleName)
    }
}