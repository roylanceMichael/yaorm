package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class SQLiteGeneratorService(override val bulkInsertSize: Int = 500,
                             private val emptyAsNull: Boolean = false) : ISQLGeneratorService {
    override val textTypeName: String
        get() = SqlTextName
    override val integerTypeName: String
        get() = SqlIntegerName
    override val realTypeName: String
        get() = SqlRealName
    override val blobTypeName: String
        get() = SqlBlobName

    override val protoTypeToSqlType = object : HashMap<YaormModel.ProtobufType, String>() {
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

    override val sqlTypeToProtoType = object : HashMap<String, YaormModel.ProtobufType>() {
        init {
            put(SqlTextName, YaormModel.ProtobufType.STRING)
            put(SqlIntegerName, YaormModel.ProtobufType.INT64)
            put(SqlRealName, YaormModel.ProtobufType.DOUBLE)
            put(SqlBlobName, YaormModel.ProtobufType.BYTES)
        }
    }

    override val insertSameAsUpdate: Boolean
        get() = true

    override fun buildSelectIds(definition: YaormModel.TableDefinition): String {
        return "select \"id\" from ${definition.name}"
    }

    override fun buildCountSql(definition: YaormModel.TableDefinition): String {
        return "select count(1) as ${this.buildKeyword("longVal")} from ${definition.name}"
    }

    override fun buildCreateColumn(
            definition: YaormModel.TableDefinition,
            propertyDefinition: YaormModel.ColumnDefinition): String? {
        if (protoTypeToSqlType.containsKey(propertyDefinition.type)) {
            return "alter table ${this.buildKeyword(definition.name)} add column ${this.buildKeyword(propertyDefinition.name)} ${protoTypeToSqlType[propertyDefinition.type]}"
        }
        return null
    }

    override fun buildDropColumn(
            definition: YaormModel.TableDefinition,
            propertyDefinition: YaormModel.ColumnDefinition): String? {
        val createTableSql = this.buildCreateTable(definition) ?: return null

        val returnList = ArrayList<String>()
        returnList.add("drop table if exists temp_${definition.name}")
        returnList.add("alter table ${this.buildKeyword(definition.name)} rename to temp_${definition.name}")
        returnList.add(createTableSql.replace(YaormUtils.SemiColon, YaormUtils.EmptyString))

        val nameTypes = YaormUtils.getNameTypes(
                definition,
                YaormModel.ProtobufType.STRING,
                this.protoTypeToSqlType)

        val columnsWithId = nameTypes
                .map { this.buildKeyword(it.sqlColumnName) }
                .joinToString(YaormUtils.Comma)

        val selectIntoStatement =
                "replace into ${this.buildKeyword(definition.name)} ($columnsWithId) select $columnsWithId from temp_${definition.name}"
        returnList.add(selectIntoStatement)
        return returnList.joinToString(YaormUtils.SemiColon) + YaormUtils.SemiColon
    }

    override fun buildDropIndex(
            definition: YaormModel.TableDefinition,
            columns: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, columns.values.map { it.name })
        return "drop index if exists ${this.buildKeyword(indexName)} on ${this.buildKeyword(definition.name)}"
    }

    override fun buildCreateIndex(
            definition: YaormModel.TableDefinition,
            properties: Map<String, YaormModel.ColumnDefinition>,
            includes: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, properties.values.map { it.name })
        val joinedColumnNames = properties.values.map { this.buildKeyword(it.name) }.joinToString(YaormUtils.Comma)
        val sqlStatement = "create index if not exists ${this.buildKeyword(indexName)} on ${this.buildKeyword(definition.name)} ($joinedColumnNames)"

        if (includes.isEmpty()) {
            return sqlStatement
        }

        val joinedIncludeColumnNames = includes.values.map { this.buildKeyword(it.name) }.joinToString(YaormUtils.Comma)
        return "$sqlStatement include ($joinedIncludeColumnNames)"
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = YaormUtils.buildWhereClause(whereClauseItem, this)
        return "delete from ${this.buildKeyword(definition.name)} where $whereClause"
    }

    override fun buildUpdateWithCriteria(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClause): String? {

        val whereClauseStr = YaormUtils.buildWhereClause(whereClauseItem, this)
        val newValuesWorkspace = StringBuilder()

        record.columnsList.forEach {
            if (newValuesWorkspace.isNotEmpty()) {
                newValuesWorkspace.append(YaormUtils.Comma)
            }
            newValuesWorkspace.append(this.buildKeyword(it.definition.name))
            newValuesWorkspace.append(YaormUtils.Equals)
            newValuesWorkspace.append(YaormUtils.getFormattedString(it, emptyAsNull))
        }

        return "update ${this.buildKeyword(definition.name)} set $newValuesWorkspace where $whereClauseStr${YaormUtils.SemiColon}"
    }

    override fun buildDropTable(definition: YaormModel.TableDefinition): String {
        return "drop table if exists ${this.buildKeyword(definition.name)}"
    }

    override fun buildDeleteAll(definition: YaormModel.TableDefinition) : String {
        return "delete from ${this.buildKeyword(definition.name)}"
    }

    override fun buildBulkInsert(
            definition: YaormModel.TableDefinition,
            records: YaormModel.Records) : String {
        val sortedColumns = definition.columnDefinitionsList.sortedBy { it.order }
        val columnNames = sortedColumns.map { this.buildKeyword(it.name) }

        val commaSeparatedColumnNames = columnNames.joinToString(YaormUtils.Comma)
        val initialStatement = "replace into ${this.buildKeyword(definition.name)} ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        records
            .recordsList
            .forEach { instance ->
                val valueColumnPairs = ArrayList<String>()

                sortedColumns
                    .forEach { columnDefinition ->
                        val foundColumn = instance.columnsList.firstOrNull { column -> column.definition.name == columnDefinition.name }

                        if (foundColumn != null) {
                            val formattedValue = YaormUtils.getFormattedString(foundColumn, emptyAsNull)
                            if (valueColumnPairs.isEmpty()) {
                                valueColumnPairs.add("select $formattedValue as ${this.buildKeyword(foundColumn.definition.name)}")
                            }
                            else {
                                valueColumnPairs.add("$formattedValue as ${this.buildKeyword(foundColumn.definition.name)}")
                            }
                        }
                        else {
                            val actualColumn = YaormUtils.buildColumn(YaormUtils.EmptyString, columnDefinition)
                            val formattedValue = YaormUtils.getFormattedString(actualColumn, emptyAsNull)
                            if (valueColumnPairs.isEmpty()) {
                                valueColumnPairs.add("select $formattedValue as ${this.buildKeyword(columnDefinition.name)}")
                            }
                            else {
                                valueColumnPairs.add("$formattedValue as ${this.buildKeyword(columnDefinition.name)}")
                            }
                        }
                    }

                selectStatements.add(valueColumnPairs.joinToString(YaormUtils.Comma))
            }

        val unionSeparatedStatements = selectStatements.joinToString(YaormUtils.SpacedUnion)
        return "$initialStatement $unionSeparatedStatements${YaormUtils.SemiColon}"
    }

    override fun buildSelectAll(definition: YaormModel.TableDefinition, limit: Int, offset: Int): String {
        return java.lang.String.format(
                SelectAllTemplate,
                this.buildKeyword(definition.name),
                limit,
                offset)
    }

    override fun buildWhereClause(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String? {

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                this.buildKeyword(definition.name),
                YaormUtils.buildWhereClause(whereClauseItem, this))

        return whereSql
    }

    override fun buildDeleteTable(definition: YaormModel.TableDefinition, primaryKey: YaormModel.Column): String? {
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                this.buildKeyword(definition.name),
                YaormUtils.getFormattedString(primaryKey, emptyAsNull))

        return deleteSql
    }

    override fun buildUpdateTable(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            YaormUtils.getNameTypes(
                    definition,
                    YaormModel.ProtobufType.STRING,
                    this.protoTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            var stringId: String? = null

            val updateKvp = ArrayList<String>()
            record
                .columnsList
                .sortedBy { it.definition.order }
                .forEach {
                    val formattedValue = YaormUtils.getFormattedString(it, emptyAsNull)
                    if (it.definition.name == YaormUtils.IdName) {
                        stringId = formattedValue
                    }
                    else {
                        updateKvp.add(this.buildKeyword(it.definition.name) + YaormUtils.Equals + formattedValue)
                    }
                }

            if (stringId == null) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    this.buildKeyword(definition.name),
                    updateKvp.joinToString(YaormUtils.Comma + YaormUtils.Space),
                    stringId!!)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildInsertIntoTable(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record): String? {
        try {
            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            record
                .columnsList
                .sortedBy { it.definition.order }
                .forEach {
                    columnNames.add(this.buildKeyword(it.definition.name))
                    values.add(YaormUtils.getFormattedString(it, emptyAsNull))
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    this.buildKeyword(definition.name),
                    columnNames.joinToString(YaormUtils.Comma),
                    values.joinToString(YaormUtils.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildCreateTable(definition: YaormModel.TableDefinition): String? {
        val nameTypes = YaormUtils.getNameTypes(
                definition,
                YaormModel.ProtobufType.STRING,
                this.protoTypeToSqlType)

        if (nameTypes.isEmpty()) {
            return null
        }

        val workspace = StringBuilder()

        for (nameType in nameTypes) {
            if (YaormUtils.IdName == nameType.sqlColumnName) {
                workspace
                        .append(this.buildKeyword(nameType.sqlColumnName))
                        .append(YaormUtils.Space)
                        .append(nameType.dataType)
                        .append(YaormUtils.Space)
                        .append(PrimaryKey)
            }
        }

        for (nameType in nameTypes) {
            if (YaormUtils.IdName != nameType.sqlColumnName) {
                workspace
                        .append(YaormUtils.Comma)
                        .append(YaormUtils.Space)
                        .append(this.buildKeyword(nameType.sqlColumnName))
                        .append(YaormUtils.Space)
                        .append(nameType.dataType)
            }
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                this.buildKeyword(definition.name),
                workspace.toString())

        return createTableSql
    }

    override fun buildKeyword(keyword: String): String {
        return "${YaormUtils.DoubleQuote}$keyword${YaormUtils.DoubleQuote}"
    }

    override fun getSchemaNames(): String {
        return ""
    }

    override fun getTableNames(schemaName: String): String {
        return "select * from sqlite_master where type='table';"
    }

    override fun buildTableDefinitionSQL(schemaName: String, tableName: String): String {
        return "select sql from sqlite_master where type='table' and name='$tableName';"
    }

    override fun buildTableDefinition(tableName: String, records: YaormModel.Records): YaormModel.TableDefinition {
        val firstRecord = records.recordsList.firstOrNull { it.columnsCount > 0 } ?:
                return YaormModel.TableDefinition.getDefaultInstance()

        val actualRecord = firstRecord.columnsList.firstOrNull { it.hasDefinition() && it.definition.name != YaormUtils.IdName } ?:
                return YaormModel.TableDefinition.getDefaultInstance()

        val foundMatch = SchemaTableRegex.matchEntire(actualRecord.stringHolder)

        if (foundMatch == null || foundMatch.groupValues.size != 3) {
            return YaormModel.TableDefinition.getDefaultInstance()
        }

        val returnTable = YaormModel.TableDefinition.newBuilder()
            .setName(tableName)

        var position = 0
        foundMatch.groupValues[2].split(Comma)
            .forEach { columnInfo ->
                val columnSplitInfo = columnInfo.trim().replace(DoubleQuote, Empty).split(Space)
                if (columnInfo.length < 2) {
                    return@forEach
                }

                val columnDefinition = YaormModel.ColumnDefinition.newBuilder()
                columnDefinition.name = columnSplitInfo[0].replace(DoubleQuote, Empty)
                columnDefinition.order = position
                val type  = columnSplitInfo[1].toLowerCase()
                if (sqlTypeToProtoType.containsKey(type)) {
                    columnDefinition.type = sqlTypeToProtoType[type]!!
                }
                else {
                    columnDefinition.type = YaormModel.ProtobufType.STRING
                }

                returnTable.addColumnDefinitions(columnDefinition)
                position += 1
            }

        return returnTable.build()
    }

    companion object {
        private const val Empty = ""
        private const val DoubleQuote = "\""
        private const val Comma = ","
        private const val Space = " "

        private const val CreateInitialTableTemplate = "create table if not exists %s (%s);"
        private const val InsertIntoTableSingleTemplate = "replace into %s (%s) values (%s);"
        private const val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
        private const val DeleteTableTemplate = "delete from %s where \"id\"=%s;"
        private const val WhereClauseTemplate = "select * from %s where %s;"
        private const val SelectAllTemplate = "select * from %s limit %s offset %s;"
        private const val PrimaryKey = "primary key"

        private const val SqlIntegerName = "integer"
        private const val SqlTextName = "text"
        private const val SqlRealName = "real"
        private const val SqlBlobName = "text"

        private const val SchemaTableRegexStr = """CREATE TABLE "(.+)" \((.+)\)"""
        private val SchemaTableRegex = Regex(SchemaTableRegexStr)
    }
}
