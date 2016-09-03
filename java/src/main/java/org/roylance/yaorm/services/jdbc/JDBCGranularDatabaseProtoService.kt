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
    private val report = YaormModel.DatabaseExecutionReport.newBuilder().setCallsToDatabase(0)

    override fun getReport(): YaormModel.DatabaseExecutionReport {
        return this.report.build()
    }

    override fun buildTableDefinitionFromQuery(query: String): YaormModel.TableDefinition {
        val statement = this.connectionSourceFactory.generateReadStatement()
        try {
            val resultSet = statement.executeQuery(query)
            val types = HashMap<String, TypeModel>()

            while (resultSet.next()) {

                if (types.size == 0) {
                    // this starts at index 1 for some reason... tests for sqlite, mysql, and postgres
                    var i = 1
                    while (i <= resultSet.metaData.columnCount) {
                        val columnName = resultSet.metaData.getColumnLabel(i)
                        types[columnName] = TypeModel(columnName, i)
                        i++
                    }
                }

                types.keys.forEach {
                    val item = resultSet.getString(it)
                    if (item == null) {
                        types[it]!!.addTest(EmptyString)
                    }
                    else {
                        types[it]!!.addTest(item)
                    }
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
            this.report.callsToDatabase = this.report.callsToDatabase + 1
        }
    }

    override fun isAvailable(): Boolean {
        try {
            return this.connectionSourceFactory.writeConnection.isValid(TenSeconds)
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
        val statement = this.connectionSourceFactory.generateUpdateStatement()
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
                this.connectionSourceFactory.writeConnection.commit()
            }
            this.report.callsToDatabase = this.report.callsToDatabase + 1
        }
    }

    override fun executeSelectQuery(definition: YaormModel.TableDefinition, query: String): IProtoCursor {
        val statement = this.connectionSourceFactory.generateReadStatement()
        try {
            val resultSet = statement.executeQuery(query)
            return JDBCProtoCursor(definition, resultSet)
        }
        catch(e:Exception) {
            throw e
        }
        finally {
            // normally close, but wait for service to do it
            this.report.callsToDatabase = this.report.callsToDatabase + 1
        }
    }

    override fun executeSelectQueryStream(definition: YaormModel.TableDefinition, query: String, streamer: IProtoStreamer) {
        val statement = this.connectionSourceFactory.generateReadStatement()
        try {
            val resultSet = statement.executeQuery(query)
            JDBCProtoCursor(definition, resultSet).getRecordsStream(streamer)
        }
        finally {
            // normally close, but wait for service to do it
            this.report.callsToDatabase = this.report.callsToDatabase + 1
        }
    }

    override fun commit() {
        this.connectionSourceFactory.writeConnection.commit()
    }

    override fun close() {
        this.connectionSourceFactory.close()
    }

    companion object {
        const private val TenSeconds = 10
        const private val EmptyString = ""
    }
}
