package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class SQLiteGeneratorService(override val bulkInsertSize: Int = 500) : ISQLGeneratorService {
    private val CreateInitialTableTemplate = "create table if not exists %s (%s);"
    private val InsertIntoTableSingleTemplate = "replace into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
    private val DeleteTableTemplate = "delete from %s where \"id\"=%s;"
    private val WhereClauseTemplate = "select * from %s where %s;"
    private val SelectAllTemplate = "select * from %s limit %s offset %s;"
    private val PrimaryKey = "primary key"

    private val SqlIntegerName = "integer"
    private val SqlTextName = "text"
    private val SqlRealName = "real"
    private val SqlBlobName = "text"

    override val protoTypeToSqlType: Map<YaormModel.ProtobufType, String> = object : HashMap<YaormModel.ProtobufType, String>() {
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
        val indexName = YaormUtils.buildIndexName(columns.values.map { it.name })
        return "drop index if exists ${this.buildKeyword(indexName)} on ${this.buildKeyword(definition.name)}"
    }

    override fun buildCreateIndex(
            definition: YaormModel.TableDefinition,
            properties: Map<String, YaormModel.ColumnDefinition>,
            includes: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(properties.values.map { it.name })
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
            if (newValuesWorkspace.length > 0) {
                newValuesWorkspace.append(YaormUtils.Comma)
            }
            newValuesWorkspace.append(this.buildKeyword(it.definition.name))
            newValuesWorkspace.append(YaormUtils.Equals)
            newValuesWorkspace.append(YaormUtils.getFormattedString(it))
        }

        return "update ${this.buildKeyword(definition.name)} set ${newValuesWorkspace.toString()} where $whereClauseStr${YaormUtils.SemiColon}"
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
                        val foundColumn = instance.columnsList.firstOrNull { column -> column.definition.name.equals(columnDefinition.name) }

                        if (foundColumn != null) {
                            val formattedValue = YaormUtils.getFormattedString(foundColumn)
                            if (valueColumnPairs.isEmpty()) {
                                valueColumnPairs.add("select $formattedValue as ${this.buildKeyword(foundColumn.definition.name)}")
                            }
                            else {
                                valueColumnPairs.add("$formattedValue as ${this.buildKeyword(foundColumn.definition.name)}")
                            }
                        }
                        else {
                            val actualColumn = YaormUtils.buildColumn(YaormUtils.EmptyString, columnDefinition)
                            val formattedValue = YaormUtils.getFormattedString(actualColumn)
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
                YaormUtils.getFormattedString(primaryKey))

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
                    val formattedValue = YaormUtils.getFormattedString(it)
                    if (it.definition.name.equals(YaormUtils.IdName)) {
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
                    values.add(YaormUtils.getFormattedString(it))
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

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        for (nameType in nameTypes) {
            if (YaormUtils.IdName.equals(nameType.sqlColumnName)) {
                workspace
                        .append(this.buildKeyword(nameType.sqlColumnName))
                        .append(YaormUtils.Space)
                        .append(nameType.dataType)
                        .append(YaormUtils.Space)
                        .append(PrimaryKey)
            }
        }

        for (nameType in nameTypes) {
            if (!YaormUtils.IdName.equals(nameType.sqlColumnName)) {
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
}
