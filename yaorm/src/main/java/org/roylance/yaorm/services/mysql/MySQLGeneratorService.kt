package org.roylance.yaorm.services.mysql

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.ProjectionUtilities
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class MySQLGeneratorService(private val schemaName: String,
                            override val bulkInsertSize: Int = 1000,
                            private val emptyAsNull: Boolean = false,
                            private val useMyISAM: Boolean = false) : ISQLGeneratorService {
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

    init {
        sqlTypeToProtoType.put(SqlTextName, YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put(SqlIntegerName, YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put(SqlRealName, YaormModel.ProtobufType.DOUBLE)
        sqlTypeToProtoType.put(SqlBlobName, YaormModel.ProtobufType.STRING)

        // not using anywhere else, hard coding this in
        sqlTypeToProtoType.put("tinyint", YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put("smallint", YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put("mediumint", YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put("int", YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put("bigint", YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put("float", YaormModel.ProtobufType.DOUBLE)
        sqlTypeToProtoType.put("double", YaormModel.ProtobufType.DOUBLE)
        sqlTypeToProtoType.put("decimal", YaormModel.ProtobufType.DOUBLE)
        sqlTypeToProtoType.put("bit", YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put("char", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("varchar", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("tinytext", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("text", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("longtext", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("binary", YaormModel.ProtobufType.BYTES)
        sqlTypeToProtoType.put("varbinary", YaormModel.ProtobufType.BYTES)
        sqlTypeToProtoType.put("tinyblob", YaormModel.ProtobufType.BYTES)
        sqlTypeToProtoType.put("blob", YaormModel.ProtobufType.BYTES)
        sqlTypeToProtoType.put("mediumblob", YaormModel.ProtobufType.BYTES)
        sqlTypeToProtoType.put("longblob", YaormModel.ProtobufType.BYTES)
        sqlTypeToProtoType.put("enum", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("set", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("date", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("datetime", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("time", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("timestamp", YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put("year", YaormModel.ProtobufType.STRING)

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
        protoTypeToSqlType.put(YaormModel.ProtobufType.BYTES, SqlBlobName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.DOUBLE, SqlRealName)
        protoTypeToSqlType.put(YaormModel.ProtobufType.FLOAT, SqlRealName)
    }

    override val insertSameAsUpdate: Boolean
        get() = true

    override fun buildJoinSql(joinTable: YaormModel.JoinTable): String {
        return """select *
from ${this.buildKeyword(joinTable.firstTable.name)} a
join ${this.buildKeyword(joinTable.secondTable.name)} b
    on a.${buildKeyword(joinTable.firstColumn.name)} = b.${buildKeyword(joinTable.secondColumn.name)}
"""
    }

    override fun buildSelectIds(definition: YaormModel.TableDefinition): String {
        return "select id from ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}"
    }

    override fun buildCountSql(definition: YaormModel.TableDefinition): String {
        return "select count(1) as ${this.buildKeyword("longVal")} from ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}"
    }

    override fun buildCreateColumn(
            definition: YaormModel.TableDefinition,
            propertyDefinition: YaormModel.ColumnDefinition): String? {
        if (!this.protoTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table " +
                "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)} " +
                "add column ${this.buildKeyword(propertyDefinition.name)} ${this.protoTypeToSqlType[propertyDefinition.type]}"
    }

    override fun buildDropColumn(
            definition: YaormModel.TableDefinition,
            propertyDefinition: YaormModel.ColumnDefinition): String? {
        return "alter table " +
                "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)} " +
                "drop column ${this.buildKeyword(propertyDefinition.name)}"
    }

    override fun buildCreateIndex(
            definition: YaormModel.TableDefinition,
            properties: Map<String, YaormModel.ColumnDefinition>,
            includes: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, properties.values.map { it.name })
        val joinedColumnNames = properties.values
                .map { this.buildIndexColumnName(it) }
                .joinToString(YaormUtils.Comma)
        val sqlStatement = "create index $indexName on " +
                "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)} " +
                "($joinedColumnNames) using BTREE"
        return sqlStatement
    }

    override fun buildDropIndex(
            definition: YaormModel.TableDefinition,
            columns: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, columns.values.map { it.name })
        return "drop index if exists ${this.buildKeyword(indexName)} on ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}"
    }

    override fun buildDropTable(definition: YaormModel.TableDefinition): String {
        return "drop table if exists ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}"
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
                var dataType = nameType.dataType
                if (SqlTextName == dataType) {
                    dataType = SqlTextIdName
                }

                workspace
                        .append(this.buildKeyword(nameType.sqlColumnName))
                        .append(YaormUtils.Space)
                        .append(dataType)
                        .append(YaormUtils.Space)
                        .append(PrimaryKey)
            }
        }

        for (nameType in nameTypes) {
            if (YaormUtils.IdName != nameType.sqlColumnName) {
                var dataType = nameType.dataType
                if (nameType.isForeignKey && SqlTextName == dataType) {
                    dataType = SqlTextIdName

                }
                workspace
                        .append(YaormUtils.Comma)
                        .append(YaormUtils.Space)
                        .append(this.buildKeyword(nameType.sqlColumnName))
                        .append(YaormUtils.Space)
                        .append(dataType)
            }
        }

        // set primary key for javaId, always
        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}",
                workspace.toString())

        if (useMyISAM) {
            return "$createTableSql engine = $MyISAMName;"
        }

        return createTableSql + ";"
    }

    override fun buildDeleteAll(definition: YaormModel.TableDefinition): String {
        return "delete from ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}"
    }

    override fun buildDeleteTable(
            definition: YaormModel.TableDefinition,
            primaryKey: YaormModel.Column): String? {
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}",
                YaormUtils.getFormattedString(primaryKey, emptyAsNull))

        return deleteSql
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = YaormUtils.buildWhereClause(whereClauseItem, this)
        return "delete from ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)} where $whereClause"
    }

    override fun buildBulkInsert(
            definition: YaormModel.TableDefinition,
            records: YaormModel.Records): String {
        val sortedColumns = definition.columnDefinitionsList.sortedBy { it.order }
        val columnNames = sortedColumns.map { this.buildKeyword(it.name) }

        val commaSeparatedColumnNames = columnNames.joinToString(YaormUtils.Comma)
        val initialStatement = "replace into ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)} ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        records
                .recordsList
                .forEach { instance ->
                    val valueColumnPairs = ArrayList<String>()
                    sortedColumns
                            .forEach { columnDefinition ->
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

        val unionSeparatedStatements = selectStatements.joinToString(YaormUtils.SpacedUnion)
        return "$initialStatement $unionSeparatedStatements${YaormUtils.SemiColon}"
    }

    override fun buildInsertIntoTable(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record): String? {
        try {
            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            record
                .columnsList
                .forEach {
                    val formattedString = YaormUtils.getFormattedString(it, emptyAsNull)
                    columnNames.add(this.buildKeyword(it.definition.name))
                    values.add(formattedString)
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}",
                    columnNames.joinToString(YaormUtils.Comma),
                    values.joinToString(YaormUtils.Comma))

            return insertSql
        } catch (e: Exception) {
            // need better logging
            e.printStackTrace()
            return null
        }
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
                    "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}",
                    updateKvp.joinToString(YaormUtils.Comma + YaormUtils.Space),
                    stringId!!)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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

            val criteriaString: String = YaormUtils.buildWhereClause(whereClauseItem, this)
            val updateKvp = ArrayList<String>()

            record.columnsList
                .sortedBy { it.definition.order }
                .forEach {
                    updateKvp.add(this.buildKeyword(it.definition.name) + YaormUtils.Equals + YaormUtils.getFormattedString(it, emptyAsNull))
                }

            // nope, not updating entire table
            if (criteriaString.isEmpty()) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)}",
                    updateKvp.joinToString(YaormUtils.Comma + YaormUtils.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildSelectAll(
            definition: YaormModel.TableDefinition,
            limit: Int,
            offset: Int): String {
        return "select * from ${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)} limit $offset,$limit;"
    }

    override fun buildWhereClause(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String? {
        val whereClause = YaormUtils.buildWhereClause(whereClauseItem, this)
        return "select * from " +
                "${this.buildKeyword(this.schemaName)}.${this.buildKeyword(definition.name)} " +
                "where $whereClause"
    }

    override fun buildKeyword(keyword: String): String {
        return "${YaormUtils.AccentQuote}$keyword${YaormUtils.AccentQuote}"
    }

    override fun getSchemaNames(): String {
        return "select distinct table_schema from information_schema.columns"
    }

    override fun getTableNames(schemaName: String): String {
        return "select distinct table_name from information_schema.columns where table_schema = '$schemaName'"
    }

    override fun buildTableDefinitionSQL(schemaName: String, tableName: String): String {
        return "select $ColumnNameName, $DataTypeName, $OrdinalPositionName from information_schema.columns where table_schema = '$schemaName' and table_name = '$tableName'"
    }

    override fun buildTableDefinition(tableName: String, records: YaormModel.Records): YaormModel.TableDefinition {
        val returnTableDefinition = YaormModel.TableDefinition.newBuilder().setName(tableName)

        records.recordsList.forEach { record ->
            val nameColumn = record.columnsList.firstOrNull { it.definition.name == ColumnNameName }
            val ordinalName = record.columnsList.firstOrNull { it.definition.name == OrdinalPositionName }
            val typeName = record.columnsList.firstOrNull { it.definition.name == DataTypeName }

            if (nameColumn == null) {
                return@forEach
            }

            val newDefinition = YaormModel.ColumnDefinition.newBuilder()
                    .setName(nameColumn.stringHolder)

            if (ordinalName != null) {
                newDefinition.order = ordinalName.stringHolder.toInt()
            }

            if (typeName != null && sqlTypeToProtoType.containsKey(typeName.stringHolder)) {
                newDefinition.type = sqlTypeToProtoType[typeName.stringHolder]
            }
            else {
                newDefinition.type = YaormModel.ProtobufType.STRING
            }

            returnTableDefinition.addColumnDefinitions(newDefinition)
        }

        return returnTableDefinition.build()
    }

    override fun buildProjectionSQL(projection: YaormModel.Projection): String {
        return ProjectionUtilities.buildProjectionSQL(projection, this)
    }

    private fun buildIndexColumnName(columnName:YaormModel.ColumnDefinition): String {
        if ((columnName.type == YaormModel.ProtobufType.STRING ||
                columnName.type == YaormModel.ProtobufType.BYTES) &&
                columnName.name != YaormUtils.IdName) {
            return "${columnName.name}(100)"
        }
        return this.buildKeyword(columnName.name)
    }

    companion object {
        private const val MyISAMName = "MYISAM"
        private const val ColumnNameName = "column_name"
        private const val DataTypeName = "data_type"
        private const val OrdinalPositionName = "ordinal_position"

        private const val CreateInitialTableTemplate = "create table if not exists %s (%s)"
        private const val InsertIntoTableSingleTemplate = "replace into %s (%s) values (%s);"
        private const val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
        private const val UpdateTableMultipleTemplate = "update %s set %s where %s;"
        private const val DeleteTableTemplate = "delete from %s where id=%s;"
        private const val PrimaryKey = "primary key"

        private const val SqlIntegerName = "bigint"
        // http://dev.mysql.com/doc/refman/5.0/en/char.html - thank you
        private const val SqlTextName = "mediumtext"
        private const val SqlTextIdName = "varchar(150)"
        private const val SqlRealName = "decimal(60,10)"
        private const val SqlBlobName = "longtext"
    }
}
