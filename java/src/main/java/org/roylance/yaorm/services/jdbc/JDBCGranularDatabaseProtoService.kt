package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.TypeModel
import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.services.IConnectionSourceFactory
import org.roylance.yaorm.services.proto.IGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.IProtoCursor
import org.roylance.yaorm.services.proto.IProtoStreamer
import java.util.*

class JDBCGranularDatabaseProtoService(private val connectionSourceFactory: IConnectionSourceFactory,
                                       private val shouldManuallyCommitAfterUpdate: Boolean): IGranularDatabaseProtoService {

    override fun buildTableDefinitionFromQuery(query: String): YaormModel.TableDefinition {
        val statement = this.connectionSourceFactory.connectionSource.prepareStatement(query)
        try {
            val resultSet = statement.executeQuery()
            val types = HashMap<String, TypeModel>()

            while (resultSet.next()) {

                if (types.size == 0) {
                    // this starts at index 1 for some reason... tests for sqlite, mysql, and postgres
                    var i = 1
                    while (i <= statement.metaData.columnCount) {
                        val columnName = statement.metaData.getColumnLabel(i)
                        types[columnName] = TypeModel(columnName, i)
                        i++
                    }
                }

                types.keys.forEach {
                    val item = resultSet.getString(it)
                    types[it]!!.addTest(item)
                }
            }

            val returnTable = YaormModel.TableDefinition.newBuilder()
            types.keys.forEach {
                returnTable.addColumnDefinitions(types[it]!!.buildColumnDefinition())
            }

            return returnTable.build()
        }
        catch(e: Exception) {
            e.printStackTrace()
            throw e
        }
        finally {
            statement.close()
        }
    }

    override fun isAvailable(): Boolean {
        try {
            return this.connectionSourceFactory.connectionSource.isValid(TenSeconds)
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
        val statement = this.connectionSourceFactory.connectionSource.createStatement()
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
                this.connectionSourceFactory.connectionSource.commit()
            }
            statement.close()
        }
    }

    override fun executeSelectQuery(definition: YaormModel.TableDefinition, query: String): IProtoCursor {
        val statement = this.connectionSourceFactory.connectionSource.prepareStatement(query)
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

    override fun executeSelectQueryStream(definition: YaormModel.TableDefinition, query: String, streamer: IProtoStreamer) {
        val statement = this.connectionSourceFactory.connectionSource.prepareStatement(query)
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
        this.connectionSourceFactory.connectionSource.commit()
    }

    override fun close() {
        this.connectionSourceFactory.close()
    }

    companion object {
        const private val TenSeconds = 10
    }
}
