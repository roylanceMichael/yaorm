package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IStreamer
import org.roylance.yaorm.utilities.YaormUtils
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

class JDBCCursor(private val definitionModel: YaormModel.TableDefinition,
                 private val resultSet: ResultSet): ICursor {
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
                        val propertyHolder = YaormUtils.buildColumn(YaormUtils.EmptyString, it)
                        newInstance.addColumns(propertyHolder)
                    }
                    else {
                        try {
                            val newValue = resultSet.getString(it.name)
                            val propertyHolder = YaormUtils.buildColumn(newValue, it)
                            newInstance.addColumns(propertyHolder)
                        }
                        catch (e:SQLException) {
                            // if we can't see this name for w/e reason, we'll print to the console, but continue on
                            // e.printStackTrace()
                            this.namesToAvoid.add(it.name)
                            val propertyHolder = YaormUtils.buildColumn(YaormUtils.EmptyString, it)
                            newInstance.addColumns(propertyHolder)
                        }
                    }
                }

        return newInstance.build()
    }

    override fun getRecords(): YaormModel.Records {
        val returnItems = YaormModel.Records.newBuilder()

        while (this.moveNext()) {
            returnItems.addRecords(this.getRecord())
        }
        this.resultSet.close()
        return returnItems.build()
    }

    override fun getRecordsStream(streamer: IStreamer) {
        while (this.moveNext()) {
            streamer.stream(this.getRecord())
        }
        this.resultSet.close()
    }
}
