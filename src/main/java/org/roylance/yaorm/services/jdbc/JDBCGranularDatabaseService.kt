package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IGranularDatabaseService
import java.sql.Connection
import java.util.*

class JDBCGranularDatabaseService(
        private val connection: Connection,
        private val shouldManuallyCommitAfterUpdate: Boolean) : IGranularDatabaseService {
    override fun isAvailable(): Boolean {
        try {
            return this.connection.isValid(TenSeconds)
        }
        catch(e: UnsupportedOperationException) {
            e.printStackTrace()
            return true
        }
        catch(e: Exception) {
            e.printStackTrace()
            // todo: log better
            return false
        }
    }

    override fun commit() {
        this.connection.commit()
    }

    override fun close() {
        if (!this.connection.isClosed) {
            this.connection.close()
        }
    }

    override fun executeUpdateQuery(query: String): EntityResultModel {
        val statement = this.connection.createStatement()
        try {
            val returnObject = EntityResultModel()
            val result = statement.executeUpdate(query)

            val returnedKeys = ArrayList<String>()
            returnObject.generatedKeys = returnedKeys
            returnObject.successful = result > 0

            return returnObject
        }
        finally {
            if (this.shouldManuallyCommitAfterUpdate) {
                this.connection.commit()
            }
            statement.close()
        }
    }

    override fun <T: IEntity> executeSelectQuery(
            classModel:Class<T>,
            query: String): ICursor<T> {
        val statement = this.connection.prepareStatement(query)
        try {
            val resultSet = statement.executeQuery()
            return JDBCCursor(classModel, resultSet, statement)
        }
        finally {
            // normally close, but wait for service to do it
        }
    }

    companion object {
        const private val TenSeconds = 10
    }
}