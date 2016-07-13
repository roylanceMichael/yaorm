package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.proto.IProtoCursor
import org.roylance.yaorm.services.proto.IProtoStreamer
import org.roylance.yaorm.utilities.CommonUtils
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class JDBCProtoCursor(private val definitionModel: YaormModel.TableDefinition,
                      private val resultSet: ResultSet,
                      private val preparedStatement: Statement):IProtoCursor {
    private val namesToAvoid = HashSet<String>()

    fun moveNext(): Boolean {
        return this.resultSet.next()
    }

    fun getRecord(): YaormModel.Record {
        val newInstance = YaormModel.Record.newBuilder()

        // set all the properties that we can
        this.definitionModel
                .columnDefinitionsList
                .distinctBy { it.name }
                .forEach {
                    if (this.namesToAvoid.contains(it.name)) {
                        val propertyHolder = CommonUtils.buildColumn(null, it)
                        newInstance.addColumns(propertyHolder)
                    }
                    else {
                        try {
                            val newValue = resultSet.getString(it.name)
                            val propertyHolder = CommonUtils.buildColumn(newValue, it)
                            newInstance.addColumns(propertyHolder)
                        }
                        catch (e:SQLException) {
                            // if we can't see this name for w/e reason, we'll print to the console, but continue on
                            e.printStackTrace()
                            this.namesToAvoid.add(it.name)
                            val propertyHolder = CommonUtils.buildColumn(null, it)
                            newInstance.addColumns(propertyHolder)
                        }
                    }
                }

        return newInstance.build()
    }

    override fun getRecords(): YaormModel.Records {
        val returnItems = YaormModel.Records.newBuilder()
        try {
            while (this.moveNext()) {
                returnItems.addRecords(this.getRecord())
            }
            return returnItems.build()
        }
        finally {
            // mysql is having problems closing...
            this.preparedStatement.close()
        }
    }

    override fun getRecordsStream(streamer: IProtoStreamer) {
        try {
            while (this.moveNext()) {
                streamer.stream(this.getRecord())
            }
        }
        finally {
            // mysql is having problems closing...
            this.preparedStatement.close()
        }
    }
}
