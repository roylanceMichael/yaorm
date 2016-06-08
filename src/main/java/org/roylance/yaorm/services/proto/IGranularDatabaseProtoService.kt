package org.roylance.yaorm.services.proto

import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.entity.EntityResultModel

interface IGranularDatabaseProtoService {
    fun isAvailable(): Boolean
    fun executeUpdateQuery(query:String): EntityResultModel
    fun executeSelectQuery(definition: YaormModel.Definition, query:String): IProtoCursor
    fun executeSelectQueryStream(definition: YaormModel.Definition, query:String, streamer: IProtoStreamer)
    fun commit()
}
