package org.roylance.yaorm.services

import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel

interface ISqlGeneratorService {
    val javaIdName: String
    val javaTypeToSqlType: Map<String, String>
    val bulkInsertSize:Int

    fun buildCountSql(definition: DefinitionModel): String

    fun buildCreateColumn(definition: DefinitionModel, propertyDefinition: PropertyDefinitionModel): String?
    fun buildDropColumn(definition: DefinitionModel, propertyDefinition: PropertyDefinitionModel): String?

    fun buildCreateIndex(
            definition: DefinitionModel,
            properties: List<PropertyDefinitionModel>,
            includes: List<PropertyDefinitionModel>): String?
    fun buildDropIndex(definition: DefinitionModel, columns: List<PropertyDefinitionModel>): String?

    fun buildDropTable(definition: DefinitionModel): String
    fun buildCreateTable(definition: DefinitionModel): String?

    fun buildDeleteAll(definition: DefinitionModel) : String
    fun buildDeleteTable(definition: DefinitionModel, primaryKey: Any): String?
    fun buildDeleteWithCriteria(definition: DefinitionModel, whereClauseItem: WhereClauseItem): String

    fun buildBulkInsert(definition: DefinitionModel, items: List<Map<String, Any>>) : String
    fun buildInsertIntoTable(definition: DefinitionModel, newInsertModel: Map<String, Any>): String?

    fun buildUpdateTable(definition: DefinitionModel, updateModel: Map<String, Any>): String?
    fun buildUpdateWithCriteria(definition: DefinitionModel, newValues: Map<String, Any>, whereClauseItem: WhereClauseItem): String?

    fun buildSelectAll(definition: DefinitionModel, n: Int = 1000): String
    fun buildWhereClause(definition: DefinitionModel, whereClauseItem: WhereClauseItem): String?
}
