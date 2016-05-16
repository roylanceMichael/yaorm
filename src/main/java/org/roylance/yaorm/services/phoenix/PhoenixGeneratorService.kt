package org.roylance.yaorm.services.phoenix

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlGeneratorUtils
import java.util.*

class PhoenixGeneratorService (override val bulkInsertSize: Int = 500) : ISqlGeneratorService {

    private val CreateInitialTableTemplate = "create table if not exists %s (%s)"
    private val InsertIntoTableSingleTemplate = "upsert into %s (%s) values (%s)"
    private val DeleteTableTemplate = "delete from %s where id=%s"
    private val WhereClauseTemplate = "select * from %s where %s"
    private val SelectAllTemplate = "select * from %s"
    private val PrimaryKey = "primary key"
    private val NotNull = "not null"

    private val PhoenixIntegerName = "bigint"
    private val PhoenixTextName = "varchar"
    private val PhoenixRealName = "decimal"
    private val PhoenixBinaryName = "binary"

    override val javaIdName: String = "id"

    override val javaTypeToSqlType = object : HashMap<String, String>() {
        init {
            put(CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName, PhoenixTextName)
            put(CommonSqlDataTypeUtilities.JavaAlt1IntegerName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1BooleanName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1LongName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1DoubleName, PhoenixRealName)
            put(CommonSqlDataTypeUtilities.JavaAltIntegerName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltLongName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltDoubleName, PhoenixRealName)
            put(CommonSqlDataTypeUtilities.JavaStringName, PhoenixTextName)
            put(CommonSqlDataTypeUtilities.JavaByteName, PhoenixBinaryName)
            put(CommonSqlDataTypeUtilities.JavaIntegerName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaDoubleName, PhoenixRealName)
            put(CommonSqlDataTypeUtilities.JavaBooleanName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaLongName, PhoenixIntegerName)
        }
    }

    override fun buildCountSql(definition: DefinitionModel): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(definition: DefinitionModel, propertyDefinition: PropertyDefinitionModel): String? {
        if (!javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table ${definition.name} add if not exists ${propertyDefinition.name} ${javaTypeToSqlType[propertyDefinition.type]}"
    }

    override fun buildDropColumn(definition: DefinitionModel, propertyDefinition: PropertyDefinitionModel): String {
        return "alter table ${definition.name} drop column if exists ${propertyDefinition.name}"
    }

    override fun buildDropIndex(definition: DefinitionModel, columns: List<PropertyDefinitionModel>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns.map { it.name })
        return "drop index if exists $indexName on ${definition.name}"
    }

    override fun buildCreateIndex(definition: DefinitionModel, properties: List<PropertyDefinitionModel>, includes: List<PropertyDefinitionModel>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(properties.map { it.name })
        val joinedColumnNames = properties.joinToString(CommonSqlDataTypeUtilities.Comma)
        val sqlStatement = "create index if not exists $indexName on ${definition.name} ($joinedColumnNames)"

        if (includes.isEmpty()) {
            return sqlStatement
        }
        val joinedIncludeColumnNames = includes.joinToString(CommonSqlDataTypeUtilities.Comma)
        return "$sqlStatement include ($joinedIncludeColumnNames)"
    }

    override fun buildDeleteWithCriteria(
            definition: DefinitionModel,
            whereClauseItem: WhereClauseItem): String {
        val whereClause = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
        return "delete from ${definition.name} where $whereClause"
    }

    override fun buildUpdateWithCriteria(
            definition: DefinitionModel,
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): String? {
        return null
    }

    override fun buildDropTable(definition: DefinitionModel): String {
        return "drop table if exists ${definition.name}"
    }

    override fun buildDeleteAll(definition: DefinitionModel) : String {
        return "delete from ${definition.name}"
    }

    override fun buildBulkInsert(definition: DefinitionModel, items: List<Map<String, Any>>) : String {
        // do single inserts, then commit
        return ""
    }

    override fun buildSelectAll(definition: DefinitionModel, n: Int): String {
        return java.lang.String.format(
                SelectAllTemplate,
                definition.name,
                n)
    }

    override fun buildWhereClause(
            definition: DefinitionModel,
            whereClauseItem: WhereClauseItem): String? {
        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                definition.name,
                CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem))

        return whereSql
    }

    override fun buildDeleteTable(definition: DefinitionModel, primaryKey: Any): String? {
        val tableName = definition.name

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun buildUpdateTable(
            definition: DefinitionModel,
            updateModel: Map<String, Any>): String? {
        return this.buildInsertIntoTable(definition, updateModel)
    }

    override fun buildInsertIntoTable(
            definition: DefinitionModel,
            newInsertModel: Map<String, Any>): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()

            CommonSqlDataTypeUtilities.getNameTypes(
                    definition,
                    this.javaIdName,
                    CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                    definition,
                    nameTypeMap,
                    newInsertModel)

            mapWithCorrectValues
                .forEach {
                    columnNames.add(it.key)
                    values.add(it.value)
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    definition.name,
                    columnNames.joinToString(CommonSqlDataTypeUtilities.Comma),
                    values.joinToString(CommonSqlDataTypeUtilities.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildCreateTable(definition: DefinitionModel): String? {
        val nameTypes = CommonSqlDataTypeUtilities.getNameTypes(
                definition,
                this.javaIdName,
                CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                this.javaTypeToSqlType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        val foundId = nameTypes.firstOrNull { javaIdName.equals(it.sqlColumnName) } ?: return null

        workspace.append(javaIdName)
            .append(CommonSqlDataTypeUtilities.Space)
            .append(foundId.dataType)
            .append(CommonSqlDataTypeUtilities.Space)
            .append(NotNull)
            .append(CommonSqlDataTypeUtilities.Space)
            .append(PrimaryKey)

        for (nameType in nameTypes.filter { !javaIdName.equals(it.sqlColumnName) }) {
            workspace
                .append(CommonSqlDataTypeUtilities.Comma)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(nameType.sqlColumnName)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(nameType.dataType)
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                definition.name,
                workspace.toString())

        return createTableSql
    }
}
