package org.roylance.yaorm.services

import org.roylance.yaorm.models.YaormModel

interface ISqlGeneratorService {
    val javaIdName: String
    val javaTypeToSqlType: Map<YaormModel.ProtobufType, String>
    val bulkInsertSize:Int

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

    fun buildSelectAll(definition: YaormModel.TableDefinition, n: Int = 1000): String
    fun buildWhereClause(definition: YaormModel.TableDefinition, whereClauseItem: YaormModel.WhereClause): String?

    fun buildSelectIds(definition: YaormModel.TableDefinition):String
}
