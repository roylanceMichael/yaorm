package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.services.map.IGranularDatabaseMapService
import org.roylance.yaorm.services.map.IMapCursor
import java.sql.Connection
import java.util.*

class JDBCGranularDatabaseMapService(
        private val connection: Connection,
        private val shouldManuallyCommitAfterUpdate: Boolean
): IGranularDatabaseMapService {

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

    override fun executeSelectQuery(definitionModel: DefinitionModel, query: String): IMapCursor {
        val statement = this.connection.prepareStatement(query)
        try {
            val resultSet = statement.executeQuery()
            return JDBCMapCursor(definitionModel, resultSet, statement)
        }
        finally {
            // normally close, but wait for service to do it
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

    companion object {
        const private val TenSeconds = 10
    }
}
