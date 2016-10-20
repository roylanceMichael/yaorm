package org.roylance.yaorm.services.proto

import org.roylance.yaorm.YaormModel

interface IEntityProtoService: AutoCloseable {
    val insertSameAsUpdate: Boolean

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

    fun get(id: String, definition: YaormModel.TableDefinition): YaormModel.Record?
    fun getIds(definition: YaormModel.TableDefinition):List<String>
    fun getIdsStream(definition:YaormModel.TableDefinition, streamer: IProtoStreamer)

    fun getCustom(customSql: String, definition: YaormModel.TableDefinition): YaormModel.Records
    fun getCustomStream(customSql: String, definition: YaormModel.TableDefinition, streamer: IProtoStreamer)

    fun getMany(definition: YaormModel.TableDefinition, limit: Int=1000, offset: Int=0): YaormModel.Records
    fun getManyStream(definition: YaormModel.TableDefinition, streamer: IProtoStreamer, limit: Int=100000, offset: Int=0)

    fun where(whereClauseItem: YaormModel.WhereClause, definition: YaormModel.TableDefinition): YaormModel.Records

    fun buildDefinitionFromSql(customSql: String): YaormModel.TableDefinition

    fun getSchemaNames(): List<String>
    fun getTableNames(schemaName: String): List<String>
    fun getTableDefinition(schemaName: String, tableName: String): YaormModel.TableDefinition
}
