package org.roylance.yaorm.services

import org.roylance.yaorm.YaormModel

interface IEntityService : AutoCloseable {
    val insertSameAsUpdate: Boolean
    val connectionSourceFactory: IConnectionSourceFactory

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
    fun getIdsStream(definition: YaormModel.TableDefinition, streamer: IStreamer)

    fun getCustom(customSql: String, definition: YaormModel.TableDefinition): YaormModel.Records
    fun getCustomStream(customSql: String, definition: YaormModel.TableDefinition, streamer: IStreamer)

    fun getMany(definition: YaormModel.TableDefinition, limit: Int=1000, offset: Int=0): YaormModel.Records
    fun getManyStream(definition: YaormModel.TableDefinition, streamer: IStreamer, limit: Int=100000, offset: Int=0)

    fun where(whereClauseItem: YaormModel.WhereClause, definition: YaormModel.TableDefinition): YaormModel.Records

    fun buildDefinitionFromSql(customSql: String, rowCount: Int = 100000): YaormModel.TableDefinition

    fun getSchemaNames(): List<String>
    fun getTableNames(schemaName: String): List<String>
    fun getTableDefinition(schemaName: String, tableName: String): YaormModel.TableDefinition

    fun getTableDefinitionFromProject(projection: YaormModel.Projection): YaormModel.TableDefinition
    fun getRecordsFromProject(project: YaormModel.Projection): YaormModel.Records
    fun getRecordsFromProjectStream(project: YaormModel.Projection, streamer: IStreamer)
}
