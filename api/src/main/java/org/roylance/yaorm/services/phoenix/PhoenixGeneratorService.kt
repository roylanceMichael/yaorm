package org.roylance.yaorm.services.phoenix

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ISQLGeneratorService
import org.roylance.yaorm.utilities.YaormUtils
import java.util.*

class PhoenixGeneratorService (override val bulkInsertSize: Int = 500, private val emptyAsNull: Boolean = false) : ISQLGeneratorService {
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
            put(SqlBlobName, YaormModel.ProtobufType.STRING)
        }
    }

    override val insertSameAsUpdate: Boolean
        get() = true

    override fun buildSelectIds(definition: YaormModel.TableDefinition): String {
        return "select id from ${definition.name}"
    }

    override fun buildCountSql(definition: YaormModel.TableDefinition): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(definition: YaormModel.TableDefinition, propertyDefinition: YaormModel.ColumnDefinition): String? {
        if (!protoTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table ${definition.name} add if not exists ${propertyDefinition.name} ${protoTypeToSqlType[propertyDefinition.type]}"
    }

    override fun buildDropColumn(definition: YaormModel.TableDefinition, propertyDefinition: YaormModel.ColumnDefinition): String {
        return "alter table ${definition.name} drop column if exists ${propertyDefinition.name}"
    }

    override fun buildDropIndex(definition: YaormModel.TableDefinition, columns: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, columns.values.map { it.name })
        return "drop index if exists $indexName on ${definition.name}"
    }

    override fun buildCreateIndex(definition: YaormModel.TableDefinition, properties: Map<String, YaormModel.ColumnDefinition>, includes: Map<String, YaormModel.ColumnDefinition>): String? {
        val indexName = YaormUtils.buildIndexName(definition.name, properties.values.map { it.name })
        val joinedColumnNames = properties.values.joinToString(YaormUtils.Comma)
        val sqlStatement = "create index if not exists $indexName on ${definition.name} ($joinedColumnNames)"

        if (includes.isEmpty()) {
            return sqlStatement
        }
        val joinedIncludeColumnNames = includes.values.joinToString(YaormUtils.Comma)
        return "$sqlStatement include ($joinedIncludeColumnNames)"
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = YaormUtils.buildWhereClause(whereClauseItem, this)
        return "delete from ${definition.name} where $whereClause"
    }

    override fun buildUpdateWithCriteria(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClause): String? {
        return null
    }

    override fun buildDropTable(definition: YaormModel.TableDefinition): String {
        return "drop table if exists ${definition.name}"
    }

    override fun buildDeleteAll(definition: YaormModel.TableDefinition) : String {
        return "delete from ${definition.name}"
    }

    override fun buildBulkInsert(definition: YaormModel.TableDefinition, records: YaormModel.Records) : String {
        // do single inserts, then commit
        return ""
    }

    override fun buildSelectAll(definition: YaormModel.TableDefinition, limit: Int, offset: Int): String {
        return java.lang.String.format(
                SelectAllTemplate,
                definition.name,
                limit)
    }

    override fun buildWhereClause(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String? {
        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                definition.name,
                YaormUtils.buildWhereClause(whereClauseItem, this))

        return whereSql
    }

    override fun buildDeleteTable(definition: YaormModel.TableDefinition, primaryKey: YaormModel.Column): String? {
        val tableName = definition.name

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                YaormUtils.getFormattedString(primaryKey, emptyAsNull))

        return deleteSql
    }

    override fun buildUpdateTable(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record): String? {
        return this.buildInsertIntoTable(definition, record)
    }

    override fun buildInsertIntoTable(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()

            YaormUtils.getNameTypes(
                    definition,
                    YaormModel.ProtobufType.STRING,
                    this.protoTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            record
                .columnsList
                .sortedBy { it.definition.order }
                .forEach {
                    val formattedValue = YaormUtils.getFormattedString(it, emptyAsNull)
                    columnNames.add(it.definition.name)
                    values.add(formattedValue)
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    definition.name,
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

        val foundId = nameTypes.firstOrNull { YaormUtils.IdName == it.sqlColumnName } ?: return null

        workspace.append(YaormUtils.IdName)
            .append(YaormUtils.Space)
            .append(foundId.dataType)
            .append(YaormUtils.Space)
            .append(NotNull)
            .append(YaormUtils.Space)
            .append(PrimaryKey)

        for (nameType in nameTypes.filter { YaormUtils.IdName != it.sqlColumnName }) {
            workspace
                .append(YaormUtils.Comma)
                .append(YaormUtils.Space)
                .append(nameType.sqlColumnName)
                .append(YaormUtils.Space)
                .append(nameType.dataType)
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                definition.name,
                workspace.toString())

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
        private const val CreateInitialTableTemplate = "create table if not exists %s (%s)"
        private const val InsertIntoTableSingleTemplate = "upsert into %s (%s) values (%s)"
        private const val DeleteTableTemplate = "delete from %s where id=%s"
        private const val WhereClauseTemplate = "select * from %s where %s"
        private const val SelectAllTemplate = "select * from %s limit %"
        private const val PrimaryKey = "primary key"
        private const val NotNull = "not null"

        private const val SqlIntegerName = "bigint"
        private const val SqlTextName = "varchar"
        private const val SqlRealName = "decimal"
        private const val SqlBlobName = "varchar"
    }
}
