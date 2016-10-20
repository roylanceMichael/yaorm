package org.roylance.yaorm.services.proto

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.entity.EntityResultModel

interface IGranularDatabaseProtoService: AutoCloseable {
    fun isAvailable(): Boolean
    fun executeUpdateQuery(query:String): EntityResultModel
    fun buildTableDefinitionFromQuery(query: String): YaormModel.TableDefinition
    fun executeSelectQuery(definition: YaormModel.TableDefinition, query:String): IProtoCursor
    fun executeSelectQueryStream(definition: YaormModel.TableDefinition, query:String, streamer: IProtoStreamer)
    fun executeSelectQuery(query: String): YaormModel.Records
    fun executeSelectQueryStream(query: String, stream: IProtoStreamer)
    fun commit()
    fun getReport(): YaormModel.DatabaseExecutionReport
}
