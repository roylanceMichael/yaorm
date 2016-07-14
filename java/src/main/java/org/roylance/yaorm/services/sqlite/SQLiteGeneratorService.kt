package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.CommonUtils
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
        returnList.add(createTableSql.replace(CommonUtils.SemiColon, ""))

        val nameTypes = CommonUtils.getNameTypes(
                definition,
                YaormModel.ProtobufType.STRING,
                this.protoTypeToSqlType)

        val columnsWithoutId = nameTypes
                .filter { !CommonUtils.IdName.equals(it.sqlColumnName) }
                .map { this.buildKeyword(it.sqlColumnName) }
                .joinToString(CommonUtils.Comma)

        val selectIntoStatement =
                "replace into ${this.buildKeyword(definition.name)} ($columnsWithoutId) select $columnsWithoutId from temp_${definition.name}"
        returnList.add(selectIntoStatement)
        return returnList.joinToString(CommonUtils.SemiColon) + CommonUtils.SemiColon
    }

    override fun buildDropIndex(
            definition: YaormModel.TableDefinition,
            columns: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(columns.values.map { it.name })
        return "drop index if exists ${this.buildKeyword(indexName)} on ${this.buildKeyword(definition.name)}"
    }

    override fun buildCreateIndex(
            definition: YaormModel.TableDefinition,
            properties: Map<String, YaormModel.ColumnDefinition>,
            includes: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(properties.values.map { it.name })
        val joinedColumnNames = properties.values.map { this.buildKeyword(it.name) }.joinToString(CommonUtils.Comma)
        val sqlStatement = "create index if not exists ${this.buildKeyword(indexName)} on ${this.buildKeyword(definition.name)} ($joinedColumnNames)"

        if (includes.isEmpty()) {
            return sqlStatement
        }

        val joinedIncludeColumnNames = includes.values.map { this.buildKeyword(it.name) }.joinToString(CommonUtils.Comma)
        return "$sqlStatement include ($joinedIncludeColumnNames)"
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = CommonUtils.buildWhereClause(whereClauseItem, this)
        return "delete from ${this.buildKeyword(definition.name)} where $whereClause"
    }

    override fun buildUpdateWithCriteria(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClause): String? {

        val whereClauseStr = CommonUtils.buildWhereClause(whereClauseItem, this)
        val newValuesWorkspace = StringBuilder()

        record.columnsList.forEach {
            if (newValuesWorkspace.length > 0) {
                newValuesWorkspace.append(CommonUtils.Comma)
            }
            newValuesWorkspace.append(this.buildKeyword(it.definition.name))
            newValuesWorkspace.append(CommonUtils.Equals)
            newValuesWorkspace.append(CommonUtils.getFormattedString(it))
        }

        return "update ${this.buildKeyword(definition.name)} set ${newValuesWorkspace.toString()} where $whereClauseStr${CommonUtils.SemiColon}"
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
        val columnNames = definition.columnDefinitionsList.sortedBy { it.name } .map { this.buildKeyword(it.name) }

        val commaSeparatedColumnNames = columnNames.joinToString(CommonUtils.Comma)
        val initialStatement = "replace into ${this.buildKeyword(definition.name)} ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        records
            .recordsList
            .forEach { instance ->
                val valueColumnPairs = ArrayList<String>()

                instance
                    .columnsList
                    .sortedBy { it.definition.name }
                    .forEach {
                        val formattedValue = CommonUtils.getFormattedString(it)
                        if (valueColumnPairs.isEmpty()) {
                            valueColumnPairs.add("select $formattedValue as ${this.buildKeyword(it.definition.name)}")
                        }
                        else {
                            valueColumnPairs.add("$formattedValue as ${this.buildKeyword(it.definition.name)}")
                        }
                    }

                selectStatements.add(valueColumnPairs.joinToString(CommonUtils.Comma))
            }

        val unionSeparatedStatements = selectStatements.joinToString(CommonUtils.SpacedUnion)
        return "$initialStatement $unionSeparatedStatements${CommonUtils.SemiColon}"
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
                CommonUtils.buildWhereClause(whereClauseItem, this))

        return whereSql
    }

    override fun buildDeleteTable(definition: YaormModel.TableDefinition, primaryKey: YaormModel.Column): String? {
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                this.buildKeyword(definition.name),
                CommonUtils.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun buildUpdateTable(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonUtils.getNameTypes(
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
                .sortedBy { it.definition.name }
                .forEach {
                    val formattedValue = CommonUtils.getFormattedString(it)
                    if (it.definition.name.equals(CommonUtils.IdName)) {
                        stringId = formattedValue
                    }
                    else {
                        updateKvp.add(this.buildKeyword(it.definition.name) + CommonUtils.Equals + formattedValue)
                    }
                }

            if (stringId == null) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    this.buildKeyword(definition.name),
                    updateKvp.joinToString(CommonUtils.Comma + CommonUtils.Space),
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
                .sortedBy { it.definition.name }
                .forEach {
                    columnNames.add(this.buildKeyword(it.definition.name))
                    values.add(CommonUtils.getFormattedString(it))
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    this.buildKeyword(definition.name),
                    columnNames.joinToString(CommonUtils.Comma),
                    values.joinToString(CommonUtils.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildCreateTable(definition: YaormModel.TableDefinition): String? {
        val nameTypes = CommonUtils.getNameTypes(
                definition,
                YaormModel.ProtobufType.STRING,
                this.protoTypeToSqlType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        for (nameType in nameTypes) {
            if (CommonUtils.IdName.equals(nameType.sqlColumnName)) {
                workspace
                        .append(this.buildKeyword(nameType.sqlColumnName))
                        .append(CommonUtils.Space)
                        .append(nameType.dataType)
                        .append(CommonUtils.Space)
                        .append(PrimaryKey)
            }
        }

        for (nameType in nameTypes) {
            if (!CommonUtils.IdName.equals(nameType.sqlColumnName)) {
                workspace
                        .append(CommonUtils.Comma)
                        .append(CommonUtils.Space)
                        .append(this.buildKeyword(nameType.sqlColumnName))
                        .append(CommonUtils.Space)
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
        return "${CommonUtils.DoubleQuote}$keyword${CommonUtils.DoubleQuote}"
    }
}
