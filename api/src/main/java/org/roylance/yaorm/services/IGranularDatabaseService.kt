package org.roylance.yaorm.services

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.services.IConnectionSourceFactory

interface IGranularDatabaseService : AutoCloseable {
    val connectionSourceFactory: IConnectionSourceFactory
    fun isAvailable(): Boolean
    fun executeUpdateQuery(query:String): EntityResultModel
    fun buildTableDefinitionFromQuery(query: String, rowCount: Int = 100000): YaormModel.TableDefinition
    fun executeSelectQuery(definition: YaormModel.TableDefinition, query:String): ICursor
    fun executeSelectQueryStream(definition: YaormModel.TableDefinition, query:String, streamer: IStreamer)
    fun executeSelectQuery(query: String): YaormModel.Records
    fun executeSelectQueryStream(query: String, stream: IStreamer)
    fun commit()
    fun getReport(): YaormModel.DatabaseExecutionReport
}
