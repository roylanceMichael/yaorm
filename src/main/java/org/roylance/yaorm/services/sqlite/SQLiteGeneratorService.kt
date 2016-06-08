package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonUtils
import java.util.*

class SQLiteGeneratorService(override val bulkInsertSize: Int = 500) : ISqlGeneratorService {

    private val CreateInitialTableTemplate = "create table if not exists %s (%s);"
    private val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
    private val DeleteTableTemplate = "delete from %s where id=%s;"
    private val WhereClauseTemplate = "select * from %s where %s;"
    private val SelectAllTemplate = "select * from %s limit %s;"
    private val PrimaryKey = "primary key"

    private val SqlIntegerName = "integer"
    private val SqlTextName = "text"
    private val SqlRealName = "real"
    private val SqlBlobName = "blob"

    override val javaIdName: String = "id"

    override val javaTypeToSqlType: Map<YaormModel.ProtobufType, String> = object : HashMap<YaormModel.ProtobufType, String>() {
        init {
            put(YaormModel.ProtobufType.STRING, SqlTextName)
            put(YaormModel.ProtobufType.INT32, SqlIntegerName)
            put(YaormModel.ProtobufType.INT64, SqlIntegerName)
            put(YaormModel.ProtobufType.UINT32, SqlIntegerName)
            put(YaormModel.ProtobufType.UINT64, SqlIntegerName)
            put(YaormModel.ProtobufType.SINT32, SqlIntegerName)
            put(YaormModel.ProtobufType.SINT64, SqlIntegerName)
            put(YaormModel.ProtobufType.FIXED32, SqlIntegerName)
            put(YaormModel.ProtobufType.FIXED64, SqlIntegerName)
            put(YaormModel.ProtobufType.SFIXED32, SqlIntegerName)
            put(YaormModel.ProtobufType.SFIXED64, SqlIntegerName)
            put(YaormModel.ProtobufType.BOOL, SqlIntegerName)
            put(YaormModel.ProtobufType.BYTES, SqlBlobName)
            put(YaormModel.ProtobufType.DOUBLE, SqlRealName)
            put(YaormModel.ProtobufType.FLOAT, SqlRealName)
        }
    }

    override fun buildCountSql(definition: YaormModel.Definition): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(
            definition: YaormModel.Definition,
            propertyDefinition: YaormModel.PropertyDefinition): String? {
        if (javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return "alter table ${definition.name} add column ${propertyDefinition.name} ${javaTypeToSqlType[propertyDefinition.type]}"
        }
        return null
    }

    override fun buildDropColumn(
            definition: YaormModel.Definition,
            propertyDefinition: YaormModel.PropertyDefinition): String? {
        val createTableSql = this.buildCreateTable(definition) ?: return null

        val returnList = ArrayList<String>()
        returnList.add("drop table if exists temp_${definition.name}")
        returnList.add("alter table ${definition.name} rename to temp_${definition.name}")
        returnList.add(createTableSql.replace(CommonUtils.SemiColon, ""))

        val nameTypes = CommonUtils.getNameTypes(
                definition,
                this.javaIdName,
                YaormModel.ProtobufType.STRING,
                this.javaTypeToSqlType)

        val columnsWithoutId = nameTypes
                .filter { !javaIdName.equals(it.sqlColumnName) }
                .map { "${it.sqlColumnName}" }
                .joinToString(CommonUtils.Comma)

        val selectIntoStatement =
                "insert into ${definition.name} ($columnsWithoutId) select $columnsWithoutId from temp_${definition.name}"
        returnList.add(selectIntoStatement)
        return returnList.joinToString(CommonUtils.SemiColon) + CommonUtils.SemiColon
    }

    override fun buildDropIndex(
            definition: YaormModel.Definition,
            columns: List<YaormModel.PropertyDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(columns.map { it.name })
        return "drop index if exists $indexName on ${definition.name}"
    }

    override fun buildCreateIndex(
            definition: YaormModel.Definition,
            properties: List<YaormModel.PropertyDefinition>,
            includes: List<YaormModel.PropertyDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(properties.map { it.name })
        val joinedColumnNames = properties.map { it.name }.joinToString(CommonUtils.Comma)
        val sqlStatement = "create index if not exists $indexName on ${definition.name} ($joinedColumnNames)"

        if (includes.isEmpty()) {
            return sqlStatement
        }

        val joinedIncludeColumnNames = includes.joinToString(CommonUtils.Comma)
        return "$sqlStatement include ($joinedIncludeColumnNames)"
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.Definition,
            whereClauseItem: YaormModel.WhereClauseItem): String {
        val whereClause = CommonUtils.buildWhereClause(whereClauseItem)
        return "delete from ${definition.name} where $whereClause"
    }

    override fun buildUpdateWithCriteria(
            definition: YaormModel.Definition,
            record: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClauseItem): String? {

        val whereClauseStr = CommonUtils.buildWhereClause(whereClauseItem)
        val newValuesWorkspace = StringBuilder()

        record.columnsList.forEach {
            if (newValuesWorkspace.length > 0) {
                newValuesWorkspace.append(CommonUtils.Comma)
            }
            newValuesWorkspace.append(it.propertyDefinition.name)
            newValuesWorkspace.append(CommonUtils.Equals)
            newValuesWorkspace.append(CommonUtils.getFormattedString(it))
        }

        return "update ${definition.name} set ${newValuesWorkspace.toString()} where $whereClauseStr${CommonUtils.SemiColon}"
    }

    override fun buildDropTable(definition: YaormModel.Definition): String {
        return "drop table if exists ${definition.name}"
    }

    override fun buildDeleteAll(definition: YaormModel.Definition) : String {
        return "delete from ${definition.name}"
    }

    override fun buildBulkInsert(
            definition: YaormModel.Definition,
            records: YaormModel.Records) : String {
        val tableName = definition.name
        val columnNames = definition.propertyDefinitionsList.sortedBy { it.name } .map { it.name }

        val commaSeparatedColumnNames = columnNames.joinToString(CommonUtils.Comma)
        val initialStatement = "insert into $tableName ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        records
            .recordsList
            .forEach { instance ->
                val valueColumnPairs = ArrayList<String>()

                instance
                    .columnsList
                    .forEach {
                        val formattedValue = CommonUtils.getFormattedString(it)
                        if (valueColumnPairs.isEmpty()) {
                            valueColumnPairs.add("select $formattedValue as ${it.propertyDefinition.name}")
                        }
                        else {
                            valueColumnPairs.add("$formattedValue as ${it.propertyDefinition.name}")
                        }
                    }

                selectStatements.add(valueColumnPairs.joinToString(CommonUtils.Comma))
            }

        val unionSeparatedStatements = selectStatements.joinToString(CommonUtils.SpacedUnion)

        return "$initialStatement $unionSeparatedStatements${CommonUtils.SemiColon}"
    }

    override fun buildSelectAll(definition: YaormModel.Definition, n: Int): String {
        return java.lang.String.format(
                SelectAllTemplate,
                definition.name,
                n)
    }

    override fun buildWhereClause(
            definition: YaormModel.Definition,
            whereClauseItem: YaormModel.WhereClauseItem): String? {
        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                definition.name,
                CommonUtils.buildWhereClause(whereClauseItem))

        return whereSql
    }

    override fun buildDeleteTable(definition: YaormModel.Definition, primaryKey: YaormModel.PropertyHolder): String? {
        val tableName = definition.name
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonUtils.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun buildUpdateTable(
            definition: YaormModel.Definition,
            record: YaormModel.Record): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonUtils.getNameTypes(
                    definition,
                    this.javaIdName,
                    YaormModel.ProtobufType.STRING,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val tableName = definition.name
            var stringId: String? = null

            val updateKvp = ArrayList<String>()
            record
                .columnsList
                .forEach {
                    val formattedValue = CommonUtils.getFormattedString(it)
                    if (it.propertyDefinition.name.equals(this.javaIdName)) {
                        stringId = formattedValue
                    }
                    else {
                        updateKvp.add(it.propertyDefinition.name + CommonUtils.Equals + formattedValue)
                    }
                }

            if (stringId == null) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    tableName,
                    updateKvp.joinToString(
                            CommonUtils.Comma +
                                    CommonUtils.Space),
                    stringId!!)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildInsertIntoTable(
            definition: YaormModel.Definition,
            record: YaormModel.Record): String? {
        try {
            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            record
                .columnsList
                .forEach {
                    columnNames.add(it.propertyDefinition.name)
                    values.add(CommonUtils.getFormattedString(it))
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    definition.name,
                    columnNames.joinToString(CommonUtils.Comma),
                    values.joinToString(CommonUtils.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildCreateTable(definition: YaormModel.Definition): String? {

        val nameTypes = CommonUtils.getNameTypes(
                definition,
                this.javaIdName,
                YaormModel.ProtobufType.STRING,
                this.javaTypeToSqlType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        for (nameType in nameTypes) {
            if (javaIdName.equals(nameType.sqlColumnName)) {
                workspace
                        .append(nameType.sqlColumnName)
                        .append(CommonUtils.Space)
                        .append(nameType.dataType)
                        .append(CommonUtils.Space)
                        .append(PrimaryKey)
            }
        }

        for (nameType in nameTypes) {
            if (!javaIdName.equals(nameType.sqlColumnName)) {
                workspace
                        .append(CommonUtils.Comma)
                        .append(CommonUtils.Space)
                        .append(nameType.sqlColumnName)
                        .append(CommonUtils.Space)
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
