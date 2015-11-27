package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IGranularDatabaseService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.sql.Connection
import java.util.*
import java.util.logging.Logger

class JDBCGranularDatabaseService(
        private val connection: Connection,
        private val shouldManuallyCommitAfterUpdate: Boolean,
        private val generatedKeysColumnName: String) : IGranularDatabaseService {

    override fun commit() {
        this.connection.commit()
    }

    override fun close() {
        if (!this.connection.isClosed) {
            this.connection.close()
        }
    }

    override fun <K> executeUpdateQuery(query: String): EntityResultModel<K> {
        val statement = this.connection.createStatement()
        val returnObject = EntityResultModel<K>()
        try {
            val result = statement.executeUpdate(query)

            val returnedKeys = ArrayList<K>()
            val generatedKeys = statement.generatedKeys
            if (generatedKeys.metaData.columnCount > 0) {
                while (generatedKeys.next()) {
                    val foundKey = generatedKeys
                            .getObject(this.generatedKeysColumnName)

                    returnedKeys.add(foundKey as K)
                }
            }

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

    override fun <K, T: IEntity<K>> executeSelectQuery(
            classModel:Class<T>,
            query: String): ICursor<T> {
        val statement = this.connection.prepareStatement(query)
        val resultSet = statement.executeQuery()
        return JDBCCursor(classModel, resultSet, statement)
    }

    companion object {
        val logger = Logger.getLogger(JDBCGranularDatabaseService::class.java.simpleName)
    }
}