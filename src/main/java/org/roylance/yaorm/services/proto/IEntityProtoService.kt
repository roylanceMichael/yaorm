package org.roylance.yaorm.services.proto

import org.roylance.yaorm.models.YaormModel

interface IEntityProtoService {
    val indexDefinition: YaormModel.Index?

    fun createTable(definition: YaormModel.TableDefinition): Boolean
    fun dropTable(definition: YaormModel.TableDefinition): Boolean

    fun createIndex(indexModel: YaormModel.Index, definition: YaormModel.TableDefinition): Boolean
    fun dropIndex(indexModel: YaormModel.Index, definition: YaormModel.TableDefinition): Boolean

    fun createColumn(propertyDefinition: YaormModel.ColumnDefinition, definition: YaormModel.TableDefinition): Boolean
    fun dropColumn(propertyDefinition: YaormModel.ColumnDefinition, definition: YaormModel.TableDefinition): Boolean

    fun bulkInsert(instances: YaormModel.Records, definition: YaormModel.TableDefinition): Boolean
    fun createOrUpdate(entity: YaormModel.Record, definition: YaormModel.TableDefinition): Boolean
    fun create(entity: YaormModel.Record, definition: YaormModel.TableDefinition): Boolean
    fun update(entity: YaormModel.Record, definition: YaormModel.TableDefinition): Boolean

    fun updateWithCriteria(
            newValues: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClause,
            definition: YaormModel.TableDefinition): Boolean
    fun updateCustom(customSql: String): Boolean
    fun delete(id: String, definition: YaormModel.TableDefinition): Boolean
    fun deleteAll(definition: YaormModel.TableDefinition): Boolean

    fun getCount(definition: YaormModel.TableDefinition): Long

    fun getManyStream(n: Int=100000, definition: YaormModel.TableDefinition, streamer: IProtoStreamer)

    fun get(id: String, definition: YaormModel.TableDefinition): YaormModel.Record?

    fun getCustom(customSql: String, definition: YaormModel.TableDefinition): YaormModel.Records
    fun getMany(n: Int=1000, definition: YaormModel.TableDefinition): YaormModel.Records
    fun where(whereClauseItem: YaormModel.WhereClause, definition: YaormModel.TableDefinition): YaormModel.Records
}
