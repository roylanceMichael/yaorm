package org.roylance.yaorm.services.proto

import org.roylance.yaorm.models.YaormModel

interface IEntityProtoService {
    val indexDefinition: YaormModel.Index?

    fun createTable(definition: YaormModel.Definition): Boolean
    fun dropTable(definition: YaormModel.Definition): Boolean

    fun createIndex(indexModel: YaormModel.Index, definition: YaormModel.Definition): Boolean
    fun dropIndex(indexModel: YaormModel.Index, definition: YaormModel.Definition): Boolean

    fun createColumn(propertyDefinition: YaormModel.PropertyDefinition, definition: YaormModel.Definition): Boolean
    fun dropColumn(propertyDefinition: YaormModel.PropertyDefinition, definition: YaormModel.Definition): Boolean

    fun getCount(definition: YaormModel.Definition): Long

    fun getCustom(customSql: String, definition: YaormModel.Definition): YaormModel.Records
    fun get(id: String, definition: YaormModel.Definition): YaormModel.Record?
    fun getMany(n: Int=1000, definition: YaormModel.Definition): YaormModel.Records
    fun getManyStream(n: Int=100000, definition: YaormModel.Definition, streamer: IProtoStreamer)
    fun where(whereClauseItem: YaormModel.WhereClauseItem, definition: YaormModel.Definition): YaormModel.Records

    fun bulkInsert(instances: YaormModel.Records, definition: YaormModel.Definition): Boolean
    fun createOrUpdate(entity: YaormModel.Record, definition: YaormModel.Definition): Boolean
    fun create(entity: YaormModel.Record, definition: YaormModel.Definition): Boolean
    fun update(entity: YaormModel.Record, definition: YaormModel.Definition): Boolean
    fun updateWithCriteria(
            newValues: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClauseItem,
            definition: YaormModel.Definition): Boolean

    fun updateCustom(customSql: String): Boolean

    fun delete(id: String, definition: YaormModel.Definition): Boolean
    fun deleteAll(definition: YaormModel.Definition): Boolean
}
