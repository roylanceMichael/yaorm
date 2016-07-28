package org.roylance.yaorm.services.proto

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.entity.EntityResultModel

interface IGranularDatabaseProtoService {
    fun isAvailable(): Boolean
    fun executeUpdateQuery(query:String): EntityResultModel
    fun executeSelectQuery(definition: YaormModel.TableDefinition, query:String): IProtoCursor
    fun executeSelectQueryStream(definition: YaormModel.TableDefinition, query:String, streamer: IProtoStreamer)
    fun commit()
}
