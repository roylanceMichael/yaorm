package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.TypeModel
import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.services.IConnectionSourceFactory
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IGranularDatabaseService
import org.roylance.yaorm.services.IStreamer
import java.sql.ResultSet
import java.util.*

class JDBCGranularDatabaseService(override val connectionSourceFactory: IConnectionSourceFactory,
                                  private val shouldManuallyCommitAfterUpdate: Boolean,
                                  private val keepLogOfSQLExecutions: Boolean = false): IGranularDatabaseService {
    private val report = YaormModel.DatabaseExecutionReport.newBuilder().setCallsToDatabase(0)

    override fun getReport(): YaormModel.DatabaseExecutionReport {
        return this.report.build()
    }

    override fun buildTableDefinitionFromQuery(query: String, rowCount: Int): YaormModel.TableDefinition {
        val statement = this.connectionSourceFactory.generateReadStatement()
        val resultSet = statement.executeQuery(query)
        var successful = true
        try {
            val types = HashMap<String, TypeModel>()

            var rowNumber = 0
            while (resultSet.next() && rowNumber < rowCount) {
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
                rowNumber += 1
            }

            val returnTable = YaormModel.TableDefinition.newBuilder()
            types.keys.forEach {
                returnTable.addColumnDefinitions(types[it]!!.buildColumnDefinition())
            }

            return returnTable.build()
        }
        catch(e: Exception) {
            successful = false
            e.printStackTrace()
            throw e
        }
        finally {
            resultSet.close()
            this.report.callsToDatabase = this.report.callsToDatabase + 1

            if (keepLogOfSQLExecutions) {
                val newExecution = YaormModel.DatabaseExecution.newBuilder()
                        .setRawSql(query)
                        .setTimeCalled(Date().time)
                        .setOrderCalled(report.callsToDatabase)
                        .setResult(successful)

                report.addExecutions(newExecution)
            }
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
            if (keepLogOfSQLExecutions) {
                val newExecution = YaormModel.DatabaseExecution.newBuilder()
                        .setRawSql(query)
                        .setTimeCalled(Date().time)
                        .setOrderCalled(report.callsToDatabase)
                        .setResult(returnObject.successful)

                report.addExecutions(newExecution)
            }
        }
    }

    override fun executeSelectQuery(definition: YaormModel.TableDefinition, query: String): ICursor {
        val statement = this.connectionSourceFactory.generateReadStatement()
        var successful = true
        try {
            val resultSet = statement.executeQuery(query)
            return JDBCCursor(definition, resultSet)
        }
        catch(e:Exception) {
            successful = false
            throw e
        }
        finally {
            // normally close, but wait for service to do it
            this.report.callsToDatabase = this.report.callsToDatabase + 1
            if (keepLogOfSQLExecutions) {
                val newExecution = YaormModel.DatabaseExecution.newBuilder()
                        .setRawSql(query)
                        .setTimeCalled(Date().time)
                        .setOrderCalled(report.callsToDatabase)
                        .setResult(successful)

                report.addExecutions(newExecution)
            }
        }
    }

    override fun executeSelectQueryStream(definition: YaormModel.TableDefinition, query: String, streamer: IStreamer) {
        val statement = this.connectionSourceFactory.generateReadStatement()
        var successful = true
        try {
            val resultSet = statement.executeQuery(query)
            JDBCCursor(definition, resultSet).getRecordsStream(streamer)
        }
        catch (e: Exception) {
            successful = false
            throw e
        }
        finally {
            // normally close, but wait for service to do it
            this.report.callsToDatabase = this.report.callsToDatabase + 1
            if (keepLogOfSQLExecutions) {
                val newExecution = YaormModel.DatabaseExecution.newBuilder()
                        .setRawSql(query)
                        .setTimeCalled(Date().time)
                        .setOrderCalled(report.callsToDatabase)
                        .setResult(successful)

                report.addExecutions(newExecution)
            }
        }
    }

    override fun executeSelectQuery(query: String): YaormModel.Records {
        val returnRecord = YaormModel.Records.newBuilder()
        val streamer = object: IStreamer {
            override fun stream(record: YaormModel.Record) {
                returnRecord.addRecords(record)
            }
        }

        executeSelectQueryStream(query, streamer)
        return returnRecord.build()
    }

    override fun executeSelectQueryStream(query: String, stream: IStreamer) {
        val statement = connectionSourceFactory.generateReadStatement()
        var successful = true
        try {
            val resultSet = statement.executeQuery(query)
            buildRecords(resultSet, stream)
        }
        catch (e: Exception) {
            successful = false
            throw e
        }
        finally {
            report.callsToDatabase = report.callsToDatabase + 1
            if (keepLogOfSQLExecutions) {
                val newExecution = YaormModel.DatabaseExecution.newBuilder()
                        .setRawSql(query)
                        .setTimeCalled(Date().time)
                        .setOrderCalled(report.callsToDatabase)
                        .setResult(successful)

                report.addExecutions(newExecution)
            }
        }
    }

    override fun commit() {
        this.connectionSourceFactory.writeConnection.commit()
    }

    override fun close() {
        this.connectionSourceFactory.close()
    }

    private fun buildRecords(resultSet: ResultSet, protoStreamer: IStreamer) {
        val foundColumns = HashMap<String, YaormModel.ColumnDefinition>()
        while (resultSet.next()) {
            val newRecord = YaormModel.Record.newBuilder()
            if (foundColumns.size == 0) {
                // this starts at index 1 for some reason... tests for sqlite, mysql, and postgres
                var i = 1
                while (i <= resultSet.metaData.columnCount) {
                    val columnName = resultSet.metaData.getColumnLabel(i)
                    foundColumns[columnName] = YaormModel.ColumnDefinition.newBuilder()
                            .setName(columnName)
                            .setType(YaormModel.ProtobufType.STRING)
                            .build()

                    i++
                }
            }

            foundColumns.keys.forEach {
                val item = resultSet.getString(it)
                if (item != null) {
                    val newColumn = YaormModel.Column.newBuilder()
                            .setDefinition(foundColumns[it]!!)
                            .setStringHolder(item)
                    newRecord.addColumns(newColumn)
                }
            }

            protoStreamer.stream(newRecord.build())
        }
    }

    companion object {
        const private val TenSeconds = 10
        const private val EmptyString = ""
    }
}
