package org.roylance.yaorm.services

import org.roylance.yaorm.models.YaormModel

interface ISqlGeneratorService {
    val javaIdName: String
    val javaTypeToSqlType: Map<YaormModel.ProtobufType, String>
    val bulkInsertSize:Int

    fun buildCountSql(definition: YaormModel.Definition): String

    fun buildCreateColumn(definition: YaormModel.Definition, propertyDefinition: YaormModel.PropertyDefinition): String?
    fun buildDropColumn(definition: YaormModel.Definition, propertyDefinition: YaormModel.PropertyDefinition): String?

    fun buildCreateIndex(
            definition: YaormModel.Definition,
            properties: List<YaormModel.PropertyDefinition>,
            includes: List<YaormModel.PropertyDefinition>): String?
    fun buildDropIndex(definition: YaormModel.Definition, columns: List<YaormModel.PropertyDefinition>): String?

    fun buildDropTable(definition: YaormModel.Definition): String
    fun buildCreateTable(definition: YaormModel.Definition): String?

    fun buildDeleteAll(definition: YaormModel.Definition) : String
    fun buildDeleteTable(definition: YaormModel.Definition, primaryKey: YaormModel.Column): String?
    fun buildDeleteWithCriteria(definition: YaormModel.Definition, whereClauseItem: YaormModel.WhereClauseItem): String

    fun buildBulkInsert(definition: YaormModel.Definition, records: YaormModel.Records) : String
    fun buildInsertIntoTable(definition: YaormModel.Definition, record: YaormModel.Record): String?

    fun buildUpdateTable(definition: YaormModel.Definition, record: YaormModel.Record): String?
    fun buildUpdateWithCriteria(definition: YaormModel.Definition, record: YaormModel.Record, whereClauseItem: YaormModel.WhereClauseItem): String?

    fun buildSelectAll(definition: YaormModel.Definition, n: Int = 1000): String
    fun buildWhereClause(definition: YaormModel.Definition, whereClauseItem: YaormModel.WhereClauseItem): String?
}
