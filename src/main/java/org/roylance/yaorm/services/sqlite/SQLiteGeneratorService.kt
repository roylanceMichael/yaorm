package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

public class SQLiteGeneratorService(
        public override val bulkInsertSize: Int = 500
) : ISqlGeneratorService {

    private val CreateInitialTableTemplate = "create table if not exists %s (%s);"
    private val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
    private val UpdateTableMultipleTemplate = "update %s set %s where %s;"
    private val DeleteTableTemplate = "delete from %s where id=%s;"
    private val WhereClauseTemplate = "select * from %s where %s;"
    private val SelectAllTemplate = "select * from %s limit %s;"
    private val PrimaryKey = "primary key"

    private val SqlIntegerName = "integer"
    private val SqlTextName = "text"
    private val SqlRealName = "real"
    private val SqlBlobName = "blob"

    override val javaIdName: String = "id"

    override val javaTypeToSqlType: Map<String, String> = object : HashMap<String, String>() {
        init {
            put(CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName, SqlTextName)
            put(CommonSqlDataTypeUtilities.JavaAlt1IntegerName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1BooleanName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1LongName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1DoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaAltIntegerName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltLongName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltDoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaStringName, SqlTextName)
            put(CommonSqlDataTypeUtilities.JavaByteName, SqlBlobName)
            put(CommonSqlDataTypeUtilities.JavaIntegerName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaDoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaBooleanName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaLongName, SqlIntegerName)
        }
    }

    override fun buildCountSql(definition: DefinitionModel): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(
            definition: DefinitionModel,
            propertyDefinition: PropertyDefinitionModel): String? {
        if (javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return "alter table ${definition.name} add column ${propertyDefinition.name} ${javaTypeToSqlType[propertyDefinition.type]}"
        }
        return null
    }

    override fun buildDropColumn(
            definition: DefinitionModel,
            propertyDefinition: PropertyDefinitionModel): String? {
        val createTableSql = this.buildCreateTable(definition) ?: return null

        val returnList = ArrayList<String>()
        returnList.add("drop table if exists temp_${definition.name}")
        returnList.add("alter table ${definition.name} rename to temp_${definition.name}")
        returnList.add(createTableSql.replace(CommonSqlDataTypeUtilities.SemiColon, ""))

        val nameTypes = CommonSqlDataTypeUtilities.getNameTypes(
                definition,
                this.javaIdName,
                CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                this.javaTypeToSqlType)

        val columnsWithoutId = nameTypes
                .filter { !javaIdName.equals(it.sqlColumnName) }
                .map { "${it.sqlColumnName}" }
                .joinToString(CommonSqlDataTypeUtilities.Comma)

        val selectIntoStatement =
                "insert into ${definition.name} ($columnsWithoutId) select $columnsWithoutId from temp_${definition.name}"
        returnList.add(selectIntoStatement)
        return returnList.joinToString(CommonSqlDataTypeUtilities.SemiColon) + CommonSqlDataTypeUtilities.SemiColon
    }

    override fun buildDropIndex(
            definition: DefinitionModel,
            columns: List<PropertyDefinitionModel>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns.map { it.name })
        return "drop index if exists $indexName on ${definition.name}"
    }

    override fun buildCreateIndex(
            definition: DefinitionModel,
            properties: List<PropertyDefinitionModel>,
            includes: List<PropertyDefinitionModel>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(properties.map { it.name })
        val joinedColumnNames = properties.map { it.name }.joinToString(CommonSqlDataTypeUtilities.Comma)
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
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonSqlDataTypeUtilities.getNameTypes(
                    definition,
                    this.javaIdName,
                    CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val tableName = definition.name
            var criteriaString: String = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
            val updateKvp = ArrayList<String>()

            newValues
                    .forEach {
                        val actualName = it.key
                        val actualValue = it.value
                        val stringValue = CommonSqlDataTypeUtilities.getFormattedString(actualValue)
                        updateKvp.add(actualName + CommonSqlDataTypeUtilities.Equals + stringValue)
                    }

            // nope, not updating entire table
            if (criteriaString.length == 0) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    tableName,
                    updateKvp.joinToString(CommonSqlDataTypeUtilities.Comma + CommonSqlDataTypeUtilities.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildDropTable(definition: DefinitionModel): String {
        return "drop table if exists ${definition.name}"
    }

    override fun buildDeleteAll(definition: DefinitionModel) : String {
        return "delete from ${definition.name}"
    }

    override fun buildBulkInsert(
            definition: DefinitionModel,
            items: List<Map<String, Any>>) : String {
        val tableName = definition.name
        val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
        CommonSqlDataTypeUtilities.getNameTypes(
                definition,
                this.javaIdName,
                CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                this.javaTypeToSqlType)
                .forEach { nameTypeMap.put(it.sqlColumnName, it) }

        val columnNames = definition.properties.sortedBy { it.name } .map { it.name }

        val commaSeparatedColumnNames = columnNames.joinToString(CommonSqlDataTypeUtilities.Comma)
        val initialStatement = "insert into $tableName ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        items
            .forEach { instance ->
                val valueColumnPairs = ArrayList<String>()

                definition
                    .properties
                    .sortedBy { it.name }
                    .forEach {
                        val actualName = it.name
                        val javaType = it.type
                        if (nameTypeMap.containsKey(actualName) &&
                                instance.containsKey(actualName) &&
                                this.javaTypeToSqlType.containsKey(javaType)) {

                            val instanceValue = instance[actualName]
                            val cleansedValue = CommonSqlDataTypeUtilities
                                    .getFormattedString(instanceValue)

                            if (valueColumnPairs.isEmpty()) {
                                valueColumnPairs.add("select $cleansedValue as $actualName")
                            }
                            else {
                                valueColumnPairs.add("$cleansedValue as $actualName")
                            }
                        }
                    }

                selectStatements.add(valueColumnPairs.joinToString(CommonSqlDataTypeUtilities.Comma))
            }

        val unionSeparatedStatements = selectStatements.joinToString(CommonSqlDataTypeUtilities.SpacedUnion)

        return "$initialStatement $unionSeparatedStatements${CommonSqlDataTypeUtilities.SemiColon}"
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
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonSqlDataTypeUtilities.getNameTypes(
                    definition,
                    this.javaIdName,
                    CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val tableName = definition.name
            var stringId: String? = null

            val updateKvp = ArrayList<String>()

            definition.properties
                .sortedBy { it.name }
                .forEach {
                    val actualName = it.name
                    val javaType = it.type
                    if (this.javaTypeToSqlType.containsKey(javaType) && updateModel.containsKey(actualName)) {
                        val actualValue = updateModel[actualName]
                        val stringValue = CommonSqlDataTypeUtilities.getFormattedString(actualValue)

                        if (javaIdName.equals(actualName)) {
                            stringId = stringValue
                        }
                        else if (nameTypeMap.containsKey(actualName)) {
                            updateKvp.add(actualName + CommonSqlDataTypeUtilities.Equals + stringValue)
                        }
                    }
                }

            if (stringId == null) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    tableName,
                    updateKvp.joinToString(
                            CommonSqlDataTypeUtilities.Comma +
                                    CommonSqlDataTypeUtilities.Space),
                    stringId!!)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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

            definition.properties
                .sortedBy { it.name }
                .forEach {
                    val actualName = it.name
                    val javaType = it.type

                    if (nameTypeMap.containsKey(actualName) &&
                            this.javaTypeToSqlType.containsKey(javaType) &&
                            newInsertModel.containsKey(actualName)) {
                        columnNames.add(actualName)
                        val instanceValue = newInsertModel[actualName]
                        values.add(CommonSqlDataTypeUtilities.getFormattedString(instanceValue))
                    }
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

        for (nameType in nameTypes) {
            if (javaIdName.equals(nameType.sqlColumnName)) {
                workspace
                        .append(nameType.sqlColumnName)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.dataType)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(PrimaryKey)
            }
        }

        for (nameType in nameTypes) {
            if (!javaIdName.equals(nameType.sqlColumnName)) {
                workspace
                        .append(CommonSqlDataTypeUtilities.Comma)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.sqlColumnName)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.dataType)
            }
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                definition.name,
                workspace.toString())

        return createTableSql
    }
}
