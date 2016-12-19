package org.roylance.yaorm.android

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.TypeModel
import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.services.IConnectionSourceFactory
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.services.IGranularDatabaseService
import org.roylance.yaorm.services.IStreamer
import java.util.*

class AndroidGranularDatabaseService(databaseName:String,
                                     context: Context) : SQLiteOpenHelper(context, databaseName, null, 1), IGranularDatabaseService {
    private val report = YaormModel.DatabaseExecutionReport.newBuilder().setCallsToDatabase(0)
    override val connectionSourceFactory: IConnectionSourceFactory = AndroidConnectionSourceFactory()

    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    override fun buildTableDefinitionFromQuery(query: String, rowCount: Int): YaormModel.TableDefinition {
        val cursor = readableDatabase.rawQuery(query, null)
        val types = HashMap<String, TypeModel>()
        try {
            var rowNumber = 0
            while(cursor.moveToNext() && rowNumber < rowCount) {
                if (types.size == 0) {
                    var i = 0
                    cursor.columnNames.forEach { columnName ->
                        types[columnName] = TypeModel(columnName, i)
                        i++
                    }
                }

                types.keys.forEach {
                    val item = cursor.getString(types[it]!!.index)
                    if (item == null) {
                        types[it]!!.addTest("")
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
        catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        finally {
            report.callsToDatabase = report.callsToDatabase + 1
        }
    }

    override fun commit() {
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

    override fun executeSelectQuery(definition: YaormModel.TableDefinition, query: String): ICursor {
        val cursor = readableDatabase.rawQuery(query, null)
        try {
            return AndroidProtoCursor(definition, cursor)
        }
        finally {
            report.callsToDatabase = report.callsToDatabase + 1
        }
    }

    override fun executeSelectQueryStream(query: String, stream: IStreamer) {
        val cursor = readableDatabase.rawQuery(query, null)
        buildRecords(cursor, stream)
    }

    override fun executeSelectQueryStream(definition: YaormModel.TableDefinition, query: String, streamer: IStreamer) {
        val cursor = readableDatabase.rawQuery(query, null)
        AndroidProtoCursor(definition, cursor).getRecordsStream(streamer)
    }

    override fun executeUpdateQuery(query: String): EntityResultModel {
        writableDatabase.execSQL(query)
        report.callsToDatabase = report.callsToDatabase + 1
        return EntityResultModel()
    }

    override fun getReport(): YaormModel.DatabaseExecutionReport {
        return report.build()
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun close() {
        readableDatabase.close()
        writableDatabase.close()
    }

    private fun buildRecords(cursor: Cursor, protoStreamer: IStreamer) {
        val foundColumns = HashMap<String, YaormModel.ColumnDefinition>()
        while (cursor.moveToNext()) {
            val newRecord = YaormModel.Record.newBuilder()
            if (foundColumns.size == 0) {
                var i = 0
                cursor.columnNames.forEach { columnName ->
                    foundColumns[columnName] = YaormModel.ColumnDefinition.newBuilder()
                            .setName(columnName)
                            .setType(YaormModel.ProtobufType.STRING)
                            .setOrder(i)
                            .build()

                    i++
                }
            }

            foundColumns.keys.forEach {
                val item = cursor.getString(foundColumns[it]!!.order)
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
}