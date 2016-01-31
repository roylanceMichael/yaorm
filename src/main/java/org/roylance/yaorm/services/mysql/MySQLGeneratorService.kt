package org.roylance.yaorm.services.mysql

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlGeneratorUtils
import java.util.*

class MySQLGeneratorService(private val schemaName: String) : ISqlGeneratorService {
    private val CreateInitialTableTemplate = "create table if not exists %s (%s)"
    private val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
    private val UpdateTableMultipleTemplate = "update %s set %s where %s;"
    private val DeleteTableTemplate = "delete from %s where id=%s;"
    private val PrimaryKey = "primary key"

    private val SqlIntegerName = "bigint"
    // http://dev.mysql.com/doc/refman/5.0/en/char.html - thank you
    private val SqlTextName = "mediumtext"
    private val SqlTextIdName = "varchar(40)"
    private val SqlRealName = "decimal"
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

    override val bulkInsertSize: Int = 1000

    override fun buildCountSql(definition: DefinitionModel): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(
            definition: DefinitionModel,
            propertyDefinition: PropertyDefinitionModel): String? {
        if (!this.javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table " +
                "${this.schemaName}.${definition.name} " +
                "add column ${propertyDefinition.name} ${this.javaTypeToSqlType[propertyDefinition.type]}"
    }

    override fun buildDropColumn(
            definition: DefinitionModel,
            propertyDefinition: PropertyDefinitionModel): String? {
        return "alter table " +
                "${this.schemaName}.${definition.name} " +
                "drop column ${propertyDefinition.name}"
    }

    override fun buildCreateIndex(
            definition: DefinitionModel,
            properties: List<PropertyDefinitionModel>,
            includes: List<PropertyDefinitionModel>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(properties.map { it.name })
        val joinedColumnNames = properties.map { it.name }.joinToString(CommonSqlDataTypeUtilities.Comma)
        val sqlStatement = "create index $indexName on " +
                "${this.schemaName}.${definition.name} " +
                "($joinedColumnNames) using BTREE"
        return sqlStatement
    }

    override fun buildDropIndex(
            definition: DefinitionModel,
            columns: List<PropertyDefinitionModel>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns.map { it.name })
        return "drop index $indexName on ${this.schemaName}.${definition.name}"
    }

    override fun buildDropTable(definition: DefinitionModel): String {
        return "drop table if exists ${this.schemaName}.${definition.name}"
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
                // if type is string, let's limit it to 40 chars
                var dataType = nameType.dataType
                if (SqlTextName.equals(dataType)) {
                    dataType = SqlTextIdName
                }

                workspace
                        .append(nameType.sqlColumnName)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(dataType)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(PrimaryKey)
            }
        }

        for (nameType in nameTypes) {
            if (!javaIdName.equals(nameType.sqlColumnName)) {
                var dataType = nameType.dataType
                if (nameType.isForeignKey &&
                        SqlTextName.equals(dataType)) {
                    dataType = SqlTextIdName

                }
                workspace
                        .append(CommonSqlDataTypeUtilities.Comma)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.sqlColumnName)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(dataType)
            }
        }

        // set primary key for javaId, always
        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                definition.name,
                workspace.toString())

        return createTableSql
    }

    override fun buildDeleteAll(definition: DefinitionModel): String {
        return "delete from ${definition.name}"
    }

    override fun buildDeleteTable(
            definition: DefinitionModel,
            primaryKey: Any): String? {
        val tableName = definition.name
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun buildDeleteWithCriteria(
            definition: DefinitionModel,
            whereClauseItem: WhereClauseItem): String {
        val whereClause = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
        return "delete from ${definition.name} where $whereClause"
    }

    override fun buildBulkInsert(
            definition: DefinitionModel,
            items: List<Map<String, Any>>): String {
        val tableName = definition.name
        val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
        CommonSqlDataTypeUtilities.getNameTypes(
                definition,
                this.javaIdName,
                CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                this.javaTypeToSqlType)
                .forEach { nameTypeMap.put(it.sqlColumnName, it) }

        val columnNames = definition.properties.sortedBy { it.name }.map { it.name }

        val commaSeparatedColumnNames = columnNames.joinToString(CommonSqlDataTypeUtilities.Comma)
        val initialStatement = "insert into ${this.schemaName}.$tableName ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        items
                .forEach { instance ->
                    val valueColumnPairs = ArrayList<String>()

                    val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                            definition,
                            nameTypeMap,
                            instance)

                    mapWithCorrectValues
                        .forEach {
                            if (valueColumnPairs.isEmpty()) {
                                valueColumnPairs.add("select ${it.value} as ${it.key}")
                            }
                            else {
                                valueColumnPairs.add("${it.value} as ${it.key}")
                            }
                        }

                    selectStatements.add(valueColumnPairs.joinToString(CommonSqlDataTypeUtilities.Comma))
                }

        val unionSeparatedStatements = selectStatements.joinToString(CommonSqlDataTypeUtilities.SpacedUnion)

        return "$initialStatement $unionSeparatedStatements${CommonSqlDataTypeUtilities.SemiColon}"
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
            // need better logging
            e.printStackTrace()
            return null
        }
    }

    override fun buildUpdateTable(definition: DefinitionModel, updateModel: Map<String, Any>): String? {
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
            val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                    definition,
                    nameTypeMap,
                    updateModel)

            mapWithCorrectValues
                .forEach {
                    if (it.key.equals(this.javaIdName)) {
                        stringId = it.value
                    }
                    else {
                        updateKvp.add(it.key + CommonSqlDataTypeUtilities.Equals + it.value)
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

            val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                    definition,
                    nameTypeMap,
                    newValues)

            mapWithCorrectValues
                .forEach {
                    updateKvp.add(it.key + CommonSqlDataTypeUtilities.Equals + it.value)
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

    override fun buildSelectAll(
            definition: DefinitionModel,
            n: Int): String {
        return "select * from ${this.schemaName}.${definition.name} limit $n;"
    }

    override fun buildWhereClause(
            definition: DefinitionModel,
            whereClauseItem: WhereClauseItem): String? {
        val whereClause = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
        return "select * from " +
                "${this.schemaName}.${definition.name} " +
                "where $whereClause"
    }
}
