package org.roylance.yaorm.services.hive

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class HiveGeneratorService(override val bulkInsertSize: Int = 2000,
                           private val emptyAsNull: Boolean = false) : ISQLGeneratorService {
    override val textTypeName: String
        get() = SqlTextName
    override val integerTypeName: String
        get() = SqlIntegerName
    override val realTypeName: String
        get() = SqlRealName
    override val blobTypeName: String
        get() = SqlBlobName

    override val protoTypeToSqlType = HashMap<YaormModel.ProtobufType, String>()

    override val sqlTypeToProtoType = HashMap<String, YaormModel.ProtobufType>()

    override val insertSameAsUpdate: Boolean
        get() = true

    init {
        protoTypeToSqlType.put(YaormModel.ProtobufType.STRING, SqlTextName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.INT32, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.INT64, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.UINT32, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.UINT64, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.SINT32, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.SINT64, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.FIXED32, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.FIXED64, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.SFIXED32, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.SFIXED64, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.BOOL, SqlIntegerName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.BYTES, SqlTextName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.DOUBLE, SqlRealName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.FLOAT, SqlRealName)

        sqlTypeToProtoType.put(SqlTextName, YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put(SqlIntegerName, YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put(SqlRealName, YaormModel.ProtobufType.DOUBLE)
        sqlTypeToProtoType.put(SqlTextName, YaormModel.ProtobufType.STRING)
    }

    override fun buildSelectIds(definition: YaormModel.TableDefinition): String {
        return "select id from ${this.buildKeyword(definition.name)}"
    }

    override fun buildCountSql(definition: YaormModel.TableDefinition): String {
        return "select count(1) as ${this.buildKeyword("longVal")} from ${this.buildKeyword(definition.name)}"
    }

    override fun buildCreateColumn(
            definition: YaormModel.TableDefinition,
            propertyDefinition: YaormModel.ColumnDefinition): String? {
        if (!this.protoTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table ${this.buildKeyword(definition.name)} add columns (${this.buildKeyword(propertyDefinition.name)}, ${this.protoTypeToSqlType[propertyDefinition.type]})"
    }

    override fun buildDropColumn(
            definition: YaormModel.TableDefinition, propertyDefinition: YaormModel.ColumnDefinition): String? {
        val columnNames = YaormUtils.getNameTypes(
                definition,
                YaormModel.ProtobufType.STRING,
                this.protoTypeToSqlType)
                .map {
                    "${this.buildKeyword(it.sqlColumnName)} ${it.dataType}"
                }
                .joinToString(YaormUtils.Comma)

        return "alter table ${this.buildKeyword(definition.name)} replace columns ($columnNames)"
    }

    override fun buildDropIndex(
            definition: YaormModel.TableDefinition,
            columns: Map<String, YaormModel.ColumnDefinition>): String? {
        return null
    }

    override fun buildCreateIndex(
            definition: YaormModel.TableDefinition,
            properties: Map<String, YaormModel.ColumnDefinition>,
            includes: Map<String, YaormModel.ColumnDefinition>): String? {
        return null
    }

    override fun buildUpdateWithCriteria(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClause): String? {
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

            val criteriaString: String = YaormUtils
                    .buildWhereClause(whereClauseItem, this)
            val updateKvp = ArrayList<String>()

            record
                .columnsList
                .forEach {
                    updateKvp.add(this.buildKeyword(it.definition.name) + YaormUtils.Equals + YaormUtils.getFormattedString(it, emptyAsNull))
                }

            // nope, not updating entire table
            if (criteriaString.isEmpty()) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    this.buildKeyword(definition.name),
                    updateKvp.joinToString(YaormUtils.Comma + YaormUtils.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildDropTable(definition: YaormModel.TableDefinition): String {
        return "drop table ${this.buildKeyword(definition.name)}"
    }

    override fun buildDeleteAll(definition: YaormModel.TableDefinition): String {
        return "delete from ${this.buildKeyword(definition.name)}"
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = YaormUtils.buildWhereClause(whereClauseItem, this)
        return "delete from ${this.buildKeyword(definition.name)} where $whereClause"
    }

    override fun buildBulkInsert(
            definition: YaormModel.TableDefinition,
            records: YaormModel.Records): String {
        val tableName = definition.name
        val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
        YaormUtils.getNameTypes(
                definition,
                YaormModel.ProtobufType.STRING,
                this.protoTypeToSqlType)
                .forEach { nameTypeMap.put(it.sqlColumnName, it) }

        val columnNames = ArrayList<String>()

        val sortedColumns = definition.columnDefinitionsList.sortedBy { it.order }

        sortedColumns
            .forEach {
                if (nameTypeMap.containsKey(it.name)) {
                    columnNames.add(this.buildKeyword(it.name))
                }
            }

        val initialStatement = "insert into table ${this.buildKeyword(tableName)} \nselect * from\n"
        val selectStatements = ArrayList<String>()

        records
            .recordsList
            .forEach { instance ->
                val valueColumnPairs = ArrayList<String>()

                sortedColumns.forEach { columnDefinition ->
                    val foundColumn = instance.columnsList.firstOrNull { column -> column.definition.name == columnDefinition.name }

                    if (foundColumn != null) {
                        val formattedString = YaormUtils.getFormattedString(foundColumn, emptyAsNull)
                        if (valueColumnPairs.isEmpty()) {
                            valueColumnPairs.add("select $formattedString as ${this.buildKeyword(foundColumn.definition.name)}")
                        }
                        else {
                            valueColumnPairs.add("$formattedString as ${this.buildKeyword(foundColumn.definition.name)}")
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

        val carriageReturnSeparatedRows = selectStatements.joinToString("${YaormUtils.Comma}${YaormUtils.CarriageReturn}")

        return "$initialStatement(\nselect stack(\n ${selectStatements.size},\n $carriageReturnSeparatedRows)) s"
    }

    override fun buildSelectAll(definition: YaormModel.TableDefinition, limit: Int, offset: Int): String {
        return java.lang.String.format(SelectAllTemplate, this.buildKeyword(definition.name), limit)
    }

    override fun buildWhereClause(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String? {
        val whereClauseItems = YaormUtils.buildWhereClause(whereClauseItem, this)

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                this.buildKeyword(definition.name),
                whereClauseItems)

        return whereSql
    }

    override fun buildDeleteTable(definition: YaormModel.TableDefinition, primaryKey: YaormModel.Column): String? {
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                this.buildKeyword(definition.name),
                YaormUtils.getFormattedString(primaryKey, emptyAsNull))

        return deleteSql
    }

    override fun buildUpdateTable(definition: YaormModel.TableDefinition, record: YaormModel.Record): String? {
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
                    val formattedString = YaormUtils.getFormattedString(it, emptyAsNull)
                    if (it.definition.name == YaormUtils.IdName) {
                        stringId = formattedString
                    }
                    else {
                        updateKvp.add(this.buildKeyword(it.definition.name) + YaormUtils.Equals + formattedString)
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
            val values = ArrayList<String>()

            record
                .columnsList
                .sortedBy { it.definition.order }
                .forEach {
                    values.add(YaormUtils.getFormattedString(it, emptyAsNull))
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    this.buildKeyword(definition.name),
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
            if (workspace.isEmpty()) {
                workspace
                    .append(YaormUtils.Space)
                    .append(this.buildKeyword(nameType.sqlColumnName))
                    .append(YaormUtils.Space)
                    .append(nameType.dataType)
            }
            else {
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
            workspace.toString(),
            10)

        return createTableSql
    }

    override fun buildKeyword(keyword: String): String {
        return keyword
    }

    override fun getSchemaNames(): String {
        return ""
    }

    override fun getTableNames(schemaName: String): String {
        return ".tables"
    }

    override fun buildTableDefinitionSQL(schemaName: String, tableName: String): String {
        return ""
    }

    override fun buildTableDefinition(tableName: String, records: YaormModel.Records): YaormModel.TableDefinition {
        return YaormModel.TableDefinition.getDefaultInstance()
    }

    companion object {
        private const val CreateInitialTableTemplate = "create table if not exists %s (%s)\nclustered by (${YaormUtils.IdName})\ninto %s buckets\nstored as orc TBLPROPERTIES ('transactional'='true')"
        private const val InsertIntoTableSingleTemplate = "insert into %s values (%s)"
        private const val UpdateTableSingleTemplate = "update %s set %s where id=%s"
        private const val UpdateTableMultipleTemplate = "update %s set %s where %s"
        private const val DeleteTableTemplate = "delete from %s where id=%s"
        private const val WhereClauseTemplate = "select * from %s where %s"
        private const val SelectAllTemplate = "select * from %s limit %s"

        private const val SqlTextName = "string"
        private const val SqlRealName = "double"
        private const val SqlIntegerName = "bigint"
        private const val SqlBlobName = "string"
    }
}
