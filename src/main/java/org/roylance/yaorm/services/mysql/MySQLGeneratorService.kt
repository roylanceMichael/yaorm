package org.roylance.yaorm.services.mysql

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonUtils
import java.util.*

class MySQLGeneratorService(private val schemaName: String) : ISqlGeneratorService {
    private val CreateInitialTableTemplate = "create table if not exists %s (%s)"
    private val InsertIntoTableSingleTemplate = "replace into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
    private val UpdateTableMultipleTemplate = "update %s set %s where %s;"
    private val DeleteTableTemplate = "delete from %s where id=%s;"
    private val PrimaryKey = "primary key"

    private val SqlIntegerName = "bigint"
    // http://dev.mysql.com/doc/refman/5.0/en/char.html - thank you
    private val SqlTextName = "mediumtext"
    private val SqlTextIdName = "varchar(100)"
    private val SqlRealName = "decimal"
    private val SqlBlobName = "longtext"

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

    override val bulkInsertSize: Int = 1000

    override fun buildCountSql(definition: YaormModel.TableDefinition): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(
            definition: YaormModel.TableDefinition,
            propertyDefinition: YaormModel.ColumnDefinition): String? {
        if (!this.javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table " +
                "${this.schemaName}.${definition.name} " +
                "add column ${propertyDefinition.name} ${this.javaTypeToSqlType[propertyDefinition.type]}"
    }

    override fun buildDropColumn(
            definition: YaormModel.TableDefinition,
            propertyDefinition: YaormModel.ColumnDefinition): String? {
        return "alter table " +
                "${this.schemaName}.${definition.name} " +
                "drop column ${propertyDefinition.name}"
    }

    override fun buildCreateIndex(
            definition: YaormModel.TableDefinition,
            properties: List<YaormModel.ColumnDefinition>,
            includes: List<YaormModel.ColumnDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(properties.map { it.name })
        val joinedColumnNames = properties.map { it.name }.joinToString(CommonUtils.Comma)
        val sqlStatement = "create index $indexName on " +
                "${this.schemaName}.${definition.name} " +
                "($joinedColumnNames) using BTREE"
        return sqlStatement
    }

    override fun buildDropIndex(
            definition: YaormModel.TableDefinition,
            columns: List<YaormModel.ColumnDefinition>): String? {
        val indexName = CommonUtils.buildIndexName(columns.map { it.name })
        return "drop index $indexName on ${this.schemaName}.${definition.name}"
    }

    override fun buildDropTable(definition: YaormModel.TableDefinition): String {
        return "drop table if exists ${this.schemaName}.${definition.name}"
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

        for (nameType in nameTypes) {
            if (javaIdName.equals(nameType.sqlColumnName)) {
                var dataType = nameType.dataType
                if (SqlTextName.equals(dataType)) {
                    dataType = SqlTextIdName
                }

                workspace
                        .append(nameType.sqlColumnName)
                        .append(CommonUtils.Space)
                        .append(dataType)
                        .append(CommonUtils.Space)
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
                        .append(CommonUtils.Comma)
                        .append(CommonUtils.Space)
                        .append(nameType.sqlColumnName)
                        .append(CommonUtils.Space)
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

    override fun buildDeleteAll(definition: YaormModel.TableDefinition): String {
        return "delete from ${definition.name}"
    }

    override fun buildDeleteTable(
            definition: YaormModel.TableDefinition,
            primaryKey: YaormModel.Column): String? {
        val tableName = definition.name
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonUtils.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String {
        val whereClause = CommonUtils.buildWhereClause(whereClauseItem)
        return "delete from ${definition.name} where $whereClause"
    }

    override fun buildBulkInsert(
            definition: YaormModel.TableDefinition,
            records: YaormModel.Records): String {
        val tableName = definition.name
        val columnNames = definition.columnDefinitionsList.sortedBy { it.name }.map { it.name }

        val commaSeparatedColumnNames = columnNames.joinToString(CommonUtils.Comma)
        val initialStatement = "replace into ${this.schemaName}.$tableName ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        records
                .recordsList
                .forEach { instance ->
                    val valueColumnPairs = ArrayList<String>()
                    instance
                        .columnsList
                        .sortedBy { it.definition.name }
                        .forEach {
                            val formattedString = CommonUtils.getFormattedString(it)
                            if (valueColumnPairs.isEmpty()) {
                                valueColumnPairs.add("select $formattedString as ${it.definition.name}")
                            }
                            else {
                                valueColumnPairs.add("$formattedString as ${it.definition.name}")
                            }
                        }

                    selectStatements.add(valueColumnPairs.joinToString(CommonUtils.Comma))
                }

        val unionSeparatedStatements = selectStatements.joinToString(CommonUtils.SpacedUnion)

        return "$initialStatement $unionSeparatedStatements${CommonUtils.SemiColon}"
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
                    val formattedString = CommonUtils.getFormattedString(it)
                    columnNames.add(it.definition.name)
                    values.add(formattedString)
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    definition.name,
                    columnNames.joinToString(CommonUtils.Comma),
                    values.joinToString(CommonUtils.Comma))

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
                    val formattedString = CommonUtils.getFormattedString(it)
                    if (it.definition.name.equals(this.javaIdName)) {
                        stringId = formattedString
                    }
                    else {
                        updateKvp.add(it.definition.name + CommonUtils.Equals + formattedString)
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

    override fun buildUpdateWithCriteria(
            definition: YaormModel.TableDefinition,
            record: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClause): String? {
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
            val criteriaString: String = CommonUtils.buildWhereClause(whereClauseItem)
            val updateKvp = ArrayList<String>()

            record.columnsList
                .forEach {
                    updateKvp.add(it.definition.name + CommonUtils.Equals + CommonUtils.getFormattedString(it))
                }

            // nope, not updating entire table
            if (criteriaString.length == 0) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    tableName,
                    updateKvp.joinToString(CommonUtils.Comma + CommonUtils.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildSelectAll(
            definition: YaormModel.TableDefinition,
            n: Int): String {
        return "select * from ${this.schemaName}.${definition.name} limit $n;"
    }

    override fun buildWhereClause(
            definition: YaormModel.TableDefinition,
            whereClauseItem: YaormModel.WhereClause): String? {
        val whereClause = CommonUtils.buildWhereClause(whereClauseItem)
        return "select * from " +
                "${this.schemaName}.${definition.name} " +
                "where $whereClause"
    }
}
