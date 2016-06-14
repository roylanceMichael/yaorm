package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.proto.IProtoCursor
import org.roylance.yaorm.services.proto.IProtoStreamer
import org.roylance.yaorm.utilities.CommonUtils
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class JDBCProtoCursor(private val definitionModel: YaormModel.TableDefinition,
                      private val resultSet: ResultSet,
                      private val preparedStatement: Statement):IProtoCursor {
    private val columnNamesFromResultSet: HashSet<String> = HashSet()

    fun moveNext(): Boolean {
        return this.resultSet.next()
    }

    fun getRecord(): YaormModel.Record {
        val newInstance = YaormModel.Record.newBuilder()

        if (this.columnNamesFromResultSet.isEmpty()) {
            val totalColumns = this.resultSet.metaData.columnCount

            var iter = 1
            while (iter <= totalColumns) {
                // let's make sure we get the last one
                val lowercaseName = CommonUtils.getLastWord(this.resultSet
                        .metaData
                        .getColumnName(iter))
                        .toLowerCase()

                this.columnNamesFromResultSet.add(lowercaseName)
                iter++
            }
        }

        // set all the properties that we can
        this.definitionModel
                .columnDefinitionsList
                .forEach {
                    val newValue = resultSet.getString(it.name) //this.typeToAction[it.type]!!(it.name, this.resultSet)
                    val propertyHolder = CommonUtils.buildColumn(newValue, it)
                    newInstance.addColumns(propertyHolder)
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
