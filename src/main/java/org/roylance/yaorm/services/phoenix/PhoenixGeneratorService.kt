package org.roylance.yaorm.services.phoenix

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonUtils
import java.util.*

class PhoenixGeneratorService (override val bulkInsertSize: Int = 500) : ISqlGeneratorService {

    private val CreateInitialTableTemplate = "create table if not exists %s (%s)"
    private val InsertIntoTableSingleTemplate = "upsert into %s (%s) values (%s)"
    private val DeleteTableTemplate = "delete from %s where id=%s"
    private val WhereClauseTemplate = "select * from %s where %s"
    private val SelectAllTemplate = "select * from %s"
    private val PrimaryKey = "primary key"
    private val NotNull = "not null"

    private val PhoenixIntegerName = "bigint"
    private val PhoenixTextName = "varchar"
    private val PhoenixRealName = "decimal"
    private val PhoenixBinaryName = "binary"

    override val javaIdName: String = "id"

    override val javaTypeToSqlType = object : HashMap<YaormModel.ProtobufType, String>() {
        init {
            put(YaormModel.ProtobufType.STRING, PhoenixTextName)
            put(YaormModel.ProtobufType.INT32, PhoenixIntegerName)
            put(YaormModel.ProtobufType.INT64, PhoenixIntegerName)
            put(YaormModel.ProtobufType.UINT32, PhoenixIntegerName)
            put(YaormModel.ProtobufType.UINT64, PhoenixIntegerName)
            put(YaormModel.ProtobufType.SINT32, PhoenixIntegerName)
            put(YaormModel.ProtobufType.SINT64, PhoenixIntegerName)
            put(YaormModel.ProtobufType.FIXED32, PhoenixIntegerName)
            put(YaormModel.ProtobufType.FIXED64, PhoenixIntegerName)
            put(YaormModel.ProtobufType.SFIXED32, PhoenixIntegerName)
            put(YaormModel.ProtobufType.SFIXED64, PhoenixIntegerName)
            put(YaormModel.ProtobufType.BOOL, PhoenixIntegerName)
            put(YaormModel.ProtobufType.BYTES, PhoenixBinaryName)
            put(YaormModel.ProtobufType.DOUBLE, PhoenixRealName)
            put(YaormModel.ProtobufType.FLOAT, PhoenixRealName)
        }
    }

    override fun buildCountSql(definition: YaormModel.TableDefinition): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(definition: YaormModel.TableDefinition, propertyDefinition: YaormModel.ColumnDefinition): String? {
        if (!javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table ${definition.name} add if not exists ${propertyDefinition.name} ${javaTypeToSqlType[propertyDefinition.type]}"
    }

    override fun buildDropColumn(definition: YaormModel.TableDefinition, propertyDefinition: YaormModel.ColumnDefinition): String {
        return "alter table ${definition.name} drop column if exists ${propertyDefinition.name}"
    }

    override fun buildDropIndex(definition: YaormModel.TableDefinition, columns: List<YaormModel.ColumnDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(columns.map { it.name })
        return "drop index if exists $indexName on ${definition.name}"
    }

    override fun buildCreateIndex(definition: YaormModel.TableDefinition, properties: List<YaormModel.ColumnDefinition>, includes: List<YaormModel.ColumnDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(properties.map { it.name })
        val joinedColumnNames = properties.joinToString(CommonUtils.Comma)
        val sqlStatement = "create index if not exists $indexName on ${definition.name} ($joinedColumnNames)"

        if (includes.isEmpty()) {
            return sqlStatement
        }
        val joinedIncludeColumnNames = includes.joinToString(CommonUtils.Comma)
        return "$sqlStatement include ($joinedIncludeColumnNames)"
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = CommonUtils.buildWhereClause(whereClauseItem)
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

    override fun buildSelectAll(definition: YaormModel.TableDefinition, n: Int): String {
        return java.lang.String.format(
                SelectAllTemplate,
                definition.name,
                n)
    }

    override fun buildWhereClause(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String? {
        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                definition.name,
                CommonUtils.buildWhereClause(whereClauseItem))

        return whereSql
    }

    override fun buildDeleteTable(definition: YaormModel.TableDefinition, primaryKey: YaormModel.Column): String? {
        val tableName = definition.name

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonUtils.getFormattedString(primaryKey))

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

            CommonUtils.getNameTypes(
                    definition,
                    this.javaIdName,
                    YaormModel.ProtobufType.STRING,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            record
                .columnsList
                .forEach {
                    val formattedValue = CommonUtils.getFormattedString(it)
                    columnNames.add(it.definition.name)
                    values.add(formattedValue)
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

    override fun buildCreateTable(definition: YaormModel.TableDefinition): String? {
        val nameTypes = CommonUtils.getNameTypes(
                definition,
                this.javaIdName,
                YaormModel.ProtobufType.STRING,
                this.javaTypeToSqlType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        val foundId = nameTypes.firstOrNull { javaIdName.equals(it.sqlColumnName) } ?: return null

        workspace.append(javaIdName)
            .append(CommonUtils.Space)
            .append(foundId.dataType)
            .append(CommonUtils.Space)
            .append(NotNull)
            .append(CommonUtils.Space)
            .append(PrimaryKey)

        for (nameType in nameTypes.filter { !javaIdName.equals(it.sqlColumnName) }) {
            workspace
                .append(CommonUtils.Comma)
                .append(CommonUtils.Space)
                .append(nameType.sqlColumnName)
                .append(CommonUtils.Space)
                .append(nameType.dataType)
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                definition.name,
                workspace.toString())

        return createTableSql
    }
}
