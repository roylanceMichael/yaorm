package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.services.proto.IGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.IProtoCursor
import org.roylance.yaorm.services.proto.IProtoStreamer
import java.sql.Connection
import java.util.*

class JDBCGranularDatabaseProtoService(private val connection: Connection,
                                       private val shouldManuallyCommitAfterUpdate: Boolean):IGranularDatabaseProtoService {
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
        val returnObject = EntityResultModel()
        try {

            val result = statement.executeUpdate(query)

            val returnedKeys = ArrayList<String>()
            returnObject.generatedKeys = returnedKeys
            returnObject.successful = result > 0

            return returnObject
        }
        catch(e:Exception) {
            returnObject.successful = false
            throw e
        }
        finally {
            if (this.shouldManuallyCommitAfterUpdate) {
                this.connection.commit()
            }
            statement.close()
        }
    }

    override fun executeSelectQuery(definition: YaormModel.Definition, query: String): IProtoCursor {
        val statement = this.connection.prepareStatement(query)
        try {
            val resultSet = statement.executeQuery()
            return JDBCProtoCursor(definition, resultSet, statement)
        }
        catch(e:Exception) {
            throw e
        }
        finally {
            // normally close, but wait for service to do it
        }
    }

    override fun executeSelectQueryStream(definition: YaormModel.Definition, query: String, streamer: IProtoStreamer) {
        val statement = this.connection.prepareStatement(query)
        try {
            val resultSet = statement.executeQuery()
            JDBCProtoCursor(definition, resultSet, statement)
                    .getRecordsStream(streamer)
        }
        finally {
            // normally close, but wait for service to do it
        }
    }

    override fun commit() {
        this.connection.commit()
    }


    companion object {
        const private val TenSeconds = 10
    }
}
