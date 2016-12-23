package org.roylance.yaorm.services

import org.roylance.yaorm.YaormModel

interface ISQLGeneratorService: IKeywordHandler {
    val protoTypeToSqlType: Map<YaormModel.ProtobufType, String>
    val sqlTypeToProtoType: Map<String, YaormModel.ProtobufType>
    val bulkInsertSize:Int
    val insertSameAsUpdate:Boolean

    val textTypeName: String
    val integerTypeName: String
    val realTypeName: String
    val blobTypeName: String

    fun buildJoinSql(joinTable: YaormModel.JoinTable): String

    fun buildCountSql(definition: YaormModel.TableDefinition): String

    fun buildCreateColumn(definition: YaormModel.TableDefinition, propertyDefinition: YaormModel.ColumnDefinition): String?
    fun buildDropColumn(definition: YaormModel.TableDefinition, propertyDefinition: YaormModel.ColumnDefinition): String?

    fun buildCreateIndex(
            definition: YaormModel.TableDefinition,
            properties: Map<String, YaormModel.ColumnDefinition>,
            includes: Map<String, YaormModel.ColumnDefinition>): String?
    fun buildDropIndex(definition: YaormModel.TableDefinition, columns: Map<String, YaormModel.ColumnDefinition>): String?

    fun buildDropTable(definition: YaormModel.TableDefinition): String
    fun buildCreateTable(definition: YaormModel.TableDefinition): String?

    fun buildDeleteAll(definition: YaormModel.TableDefinition) : String
    fun buildDeleteTable(definition: YaormModel.TableDefinition, primaryKey: YaormModel.Column): String?
    fun buildDeleteWithCriteria(definition: YaormModel.TableDefinition, whereClauseItem: YaormModel.WhereClause): String

    fun buildBulkInsert(definition: YaormModel.TableDefinition, records: YaormModel.Records) : String
    fun buildInsertIntoTable(definition: YaormModel.TableDefinition, record: YaormModel.Record): String?

    fun buildUpdateTable(definition: YaormModel.TableDefinition, record: YaormModel.Record): String?
    fun buildUpdateWithCriteria(definition: YaormModel.TableDefinition, record: YaormModel.Record, whereClauseItem: YaormModel.WhereClause): String?

    fun buildSelectAll(definition: YaormModel.TableDefinition, limit: Int = 1000, offset: Int = 0): String
    fun buildWhereClause(definition: YaormModel.TableDefinition, whereClauseItem: YaormModel.WhereClause): String?

    fun buildSelectIds(definition: YaormModel.TableDefinition):String

    fun getSchemaNames(): String
    fun getTableNames(schemaName: String): String
    fun buildTableDefinitionSQL(schemaName: String, tableName: String): String
    fun buildTableDefinition(tableName: String, records: YaormModel.Records): YaormModel.TableDefinition

    fun buildProjectionSQL(projection: YaormModel.Projection): String
}
