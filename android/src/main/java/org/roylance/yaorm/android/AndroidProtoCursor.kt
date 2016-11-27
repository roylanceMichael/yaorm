package org.roylance.yaorm.android

import android.database.Cursor
import android.database.SQLException
import android.util.Log
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.proto.IProtoCursor
import org.roylance.yaorm.services.proto.IProtoStreamer
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class AndroidProtoCursor(
        private val definitionModel: YaormModel.TableDefinition,
        private val cursor: Cursor) : IProtoCursor {
    private val namesToAvoid = HashSet<String>()

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
                    else {
                        try {
                            val index = cursor.getColumnIndex(it.name)
                            val newValue = cursor.getString(index)
                            val propertyHolder = YaormUtils.buildColumn(newValue, it)
                            newInstance.addColumns(propertyHolder)
                        }
                        catch (e: SQLException) {
                            // if we can't see this name for w/e reason, we'll print to the console, but continue on
                            // e.printStackTrace()
                            Log.e("sqlite", e.message)
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
        return returnItems.build()
    }

    override fun getRecordsStream(streamer: IProtoStreamer) {
        while (this.moveNext()) {
            streamer.stream(this.getRecord())
        }
    }
}