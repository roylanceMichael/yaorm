package org.roylance.yaorm.services.postgres

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class PostgresGeneratorService(override val bulkInsertSize: Int = 1000, private val emptyAsNull: Boolean = false) : ISQLGeneratorService {
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

        sqlTypeToProtoType.put(SqlTextName, YaormModel.ProtobufType.STRING)
        sqlTypeToProtoType.put(SqlIntegerName, YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put(SqlRealName, YaormModel.ProtobufType.DOUBLE)
        sqlTypeToProtoType.put(SqlBlobName, YaormModel.ProtobufType.BYTES)
        sqlTypeToProtoType.put(NumericType, YaormModel.ProtobufType.DOUBLE)
        sqlTypeToProtoType.put(Int8Type, YaormModel.ProtobufType.INT64)
        sqlTypeToProtoType.put(VarcharType, YaormModel.ProtobufType.STRING)
    }

    override val insertSameAsUpdate: Boolean
        get() = false

    override fun buildCountSql(definition: YaormModel.TableDefinition): String {
        return "select count(1) as ${this.buildKeyword("longVal")} from ${this.buildKeyword(definition.name)}"
    }

    override fun buildCreateColumn(definition: YaormModel.TableDefinition,
                                   propertyDefinition: YaormModel.ColumnDefinition): String? {
        if (!this.protoTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table " +
                "${this.buildKeyword(definition.name)} " +
                "add column ${this.buildKeyword(propertyDefinition.name)} ${this.protoTypeToSqlType[propertyDefinition.type]}"
    }

    override fun buildDropColumn(definition: YaormModel.TableDefinition,
                                 propertyDefinition: YaormModel.ColumnDefinition): String? {
        return "alter table " +
                "${this.buildKeyword(definition.name)} " +
                "drop column ${this.buildKeyword(propertyDefinition.name)}"
    }

    override fun buildCreateIndex(definition: YaormModel.TableDefinition,
                                  properties: Map<String, YaormModel.ColumnDefinition>,
                                  includes: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, properties.values.map { it.name })
        val joinedColumnNames = properties.values.map { this.buildKeyword(it.name) }.joinToString(YaormUtils.Comma)

        return "create index $indexName on ${this.buildKeyword(definition.name)} ($joinedColumnNames)"
    }

    override fun buildDropIndex(definition: YaormModel.TableDefinition,
                                columns: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, columns.values.map { it.name })
        return "drop index if exists $indexName"
    }

    override fun buildDropTable(definition: YaormModel.TableDefinition): String {
        return "drop table if exists ${this.buildKeyword(definition.name)}"
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
                this.buildKeyword(definition.name),
                workspace.toString())

        return createTableSql
    }

    override fun buildDeleteAll(definition: YaormModel.TableDefinition): String {
        return "delete from ${this.buildKeyword(definition.name)}"
    }

    override fun buildDeleteTable(definition: YaormModel.TableDefinition,
                                  primaryKey: YaormModel.Column): String? {
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                this.buildKeyword(definition.name),
                YaormUtils.getFormattedString(primaryKey, emptyAsNull))

        return deleteSql
    }

    override fun buildDeleteWithCriteria(definition: YaormModel.TableDefinition,
                                         whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = YaormUtils.buildWhereClause(whereClauseItem, this)
        return "delete from ${this.buildKeyword(definition.name)} where $whereClause"
    }

    override fun buildBulkInsert(definition: YaormModel.TableDefinition,
                                 records: YaormModel.Records): String {
        val sortedColumns = definition.columnDefinitionsList.sortedBy { it.order }
        val columnNames = sortedColumns.map { this.buildKeyword(it.name) }

        val commaSeparatedColumnNames = columnNames.joinToString(YaormUtils.Comma)
        val initialStatement = "insert into ${this.buildKeyword(definition.name)} ($commaSeparatedColumnNames) "
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

        return "$initialStatement $unionSeparatedStatements${YaormUtils.SemiColon}"    }

    override fun buildInsertIntoTable(definition: YaormModel.TableDefinition,
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
                    this.buildKeyword(definition.name),
                    columnNames.joinToString(YaormUtils.Comma),
                    values.joinToString(YaormUtils.Comma))

            return insertSql
        } catch (e: Exception) {
            // need better logging
            e.printStackTrace()
            return null
        }    }

    override fun buildUpdateTable(definition: YaormModel.TableDefinition,
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
                    updateKvp.joinToString(
                            YaormUtils.Comma +
                                    YaormUtils.Space),
                    stringId!!)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildUpdateWithCriteria(definition: YaormModel.TableDefinition,
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
                    this.buildKeyword(definition.name),
                    updateKvp.joinToString(YaormUtils.Comma + YaormUtils.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildSelectAll(definition: YaormModel.TableDefinition,
                                limit: Int,
                                offset: Int): String {
        return "select * from ${this.buildKeyword(definition.name)} limit $limit offset $offset"
    }

    override fun buildWhereClause(definition: YaormModel.TableDefinition,
                                  whereClauseItem: YaormModel.WhereClause): String? {
        val whereClause = YaormUtils.buildWhereClause(whereClauseItem, this)
        return "select * from " +
                "${this.buildKeyword(definition.name)} " +
                "where $whereClause"
    }

    override fun buildSelectIds(definition: YaormModel.TableDefinition): String {
        return "select id from ${this.buildKeyword(definition.name)}"
    }

    override fun buildKeyword(keyword: String): String {
        return "${YaormUtils.DoubleQuote}${keyword.toLowerCase()}${YaormUtils.DoubleQuote}"
    }

    override fun getSchemaNames(): String {
        return "select schema_name from information_schema.schemata"
    }

    override fun getTableNames(schemaName: String): String {
        return "select table_name from information_schema.tables where table_schema = '$schemaName'"
    }

    override fun buildTableDefinitionSQL(schemaName: String, tableName: String): String {
        return "select $ColumnNameName, $TypeName, $OrdinalPositionName from information_schema.columns where table_schema = '$schemaName' and table_name = '$tableName'"
    }

    override fun buildTableDefinition(tableName: String, records: YaormModel.Records): YaormModel.TableDefinition {
        val returnTableDefinition = YaormModel.TableDefinition.newBuilder().setName(tableName)

        records.recordsList.forEach { record ->
            val nameColumn = record.columnsList.firstOrNull { it.definition.name == ColumnNameName }
            val ordinalName = record.columnsList.firstOrNull { it.definition.name == OrdinalPositionName }
            val typeName = record.columnsList.firstOrNull { it.definition.name == TypeName }

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

    companion object {
        private const val ColumnNameName = "column_name"
        private const val OrdinalPositionName = "ordinal_position"
        private const val TypeName = "udt_name"

        private const val Int8Type = "int8"
        private const val VarcharType = "varchar"
        private const val NumericType = "numeric"

        private const val CreateInitialTableTemplate = "create table if not exists %s (%s)"
        private const val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"
        private const val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
        private const val UpdateTableMultipleTemplate = "update %s set %s where %s;"
        private const val DeleteTableTemplate = "delete from %s where id=%s;"
        private const val PrimaryKey = "primary key"

        private const val SqlTextIdName = "varchar(150)"
        private const val SqlIntegerName = "bigint"
        private const val SqlTextName = "text"
        private const val SqlRealName = "decimal(60, 10)"
        private const val SqlBlobName = "text"
    }
}
