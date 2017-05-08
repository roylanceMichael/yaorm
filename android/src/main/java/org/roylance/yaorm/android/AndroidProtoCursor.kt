package org.roylance.yaorm.android

import android.database.Cursor
import android.util.Log
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IStreamer
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*
import kotlin.collections.HashMap

class AndroidProtoCursor(
        private val definitionModel: YaormModel.TableDefinition,
        private val cursor: Cursor) : ICursor {
    private val namesToAvoid = HashSet<String>()
    private val columnNamesNormalized = HashMap<String, String>()

    fun moveNext() : Boolean {
        return cursor.moveToNext()
    }

    fun getRecord(): YaormModel.Record {
        val newInstance = YaormModel.Record.newBuilder()

        definitionModel
                .columnDefinitionsList
                .distinctBy { it.name }
                .forEach {
                    if (this.namesToAvoid.contains(it.name)) {
                        val propertyHolder = YaormUtils.buildColumn(YaormUtils.EmptyString, it)
                        newInstance.addColumns(propertyHolder)
                    }
                    else if (columnNamesNormalized.containsKey(it.name)) {
                        try {
                            val index = cursor.getColumnIndex(columnNamesNormalized[it.name]!!)
                            val newValue = cursor.getString(index)
                            val propertyHolder = YaormUtils.buildColumn(newValue, it)
                            newInstance.addColumns(propertyHolder)
                        }
                        catch (e: Exception) {
                            // if we can't see this name for w/e reason, we'll print to the console, but continue on
                            // e.printStackTrace()
                            Log.e("sqlite", e.message)
                            this.namesToAvoid.add(it.name)
                            val propertyHolder = YaormUtils.buildColumn(YaormUtils.EmptyString, it)
                            newInstance.addColumns(propertyHolder)
                        }
                    }
                    else {
                        val propertyHolder = YaormUtils.buildColumn(YaormUtils.EmptyString, it)
                        newInstance.addColumns(propertyHolder)
                    }
                }
        return newInstance.build()
    }

    override fun getRecords(): YaormModel.Records {
        var i = 0
        while (i < cursor.columnCount) {
            val sqliteColumnName = cursor.getColumnName(i)
            val sqliteColumnNameNormalized = sqliteColumnName.toLowerCase()

            definitionModel.columnDefinitionsList
                    .filter { it.name.toLowerCase() == sqliteColumnNameNormalized }
                    .forEach {
                        columnNamesNormalized[it.name] = sqliteColumnName
                    }
            i++
        }

        val returnItems = YaormModel.Records.newBuilder()
        while (this.moveNext()) {
            returnItems.addRecords(this.getRecord())
        }
        this.cursor.close()
        return returnItems.build()
    }

    override fun getRecordsStream(streamer: IStreamer) {
        while (this.moveNext()) {
            streamer.stream(this.getRecord())
        }
        this.cursor.close()
    }
}