package org.roylance.yaorm.services.hive

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonUtils
import java.util.*

class HiveGeneratorService(override val bulkInsertSize: Int = 2000) : ISqlGeneratorService {

    private val constJavaIdName = "id"

    private val CreateInitialTableTemplate = "create table if not exists %s (%s)\nclustered by ($constJavaIdName)\ninto %s buckets\nstored as orc TBLPROPERTIES ('transactional'='true')"
    private val InsertIntoTableSingleTemplate = "insert into %s values (%s)"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s"
    private val UpdateTableMultipleTemplate = "update %s set %s where %s"
    private val DeleteTableTemplate = "delete from %s where id=%s"
    private val WhereClauseTemplate = "select * from %s where %s"
    private val SelectAllTemplate = "select * from %s limit %s"

    private val HiveString: String = "string"
    private val HiveDouble: String = "double"
    private val HiveInt: String = "bigint"

    override val javaIdName: String = constJavaIdName

    override val javaTypeToSqlType: Map<YaormModel.ProtobufType, String> = object : HashMap<YaormModel.ProtobufType, String>() {
        init {
            put(YaormModel.ProtobufType.STRING, HiveString)
            put(YaormModel.ProtobufType.INT32, HiveInt)
            put(YaormModel.ProtobufType.INT64, HiveInt)
            put(YaormModel.ProtobufType.UINT32, HiveInt)
            put(YaormModel.ProtobufType.UINT64, HiveInt)
            put(YaormModel.ProtobufType.SINT32, HiveInt)
            put(YaormModel.ProtobufType.SINT64, HiveInt)
            put(YaormModel.ProtobufType.FIXED32, HiveInt)
            put(YaormModel.ProtobufType.FIXED64, HiveInt)
            put(YaormModel.ProtobufType.SFIXED32, HiveInt)
            put(YaormModel.ProtobufType.SFIXED64, HiveInt)
            put(YaormModel.ProtobufType.BOOL, HiveInt)
            put(YaormModel.ProtobufType.BYTES, HiveString)
            put(YaormModel.ProtobufType.DOUBLE, HiveDouble)
            put(YaormModel.ProtobufType.FLOAT, HiveDouble)
        }
    }

    override fun buildCountSql(definition: YaormModel.Definition): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(
            definition: YaormModel.Definition,
            propertyDefinition: YaormModel.PropertyDefinition): String? {
        if (!this.javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table ${definition.name} add columns (${propertyDefinition.name}, ${this.javaTypeToSqlType[propertyDefinition.type]})"
    }

    override fun buildDropColumn(
            definition: YaormModel.Definition, propertyDefinition: YaormModel.PropertyDefinition): String? {
        val columnNames = CommonUtils.getNameTypes(
                definition,
                this.javaIdName,
                YaormModel.ProtobufType.STRING,
                this.javaTypeToSqlType)
                .map {
                    "${it.sqlColumnName} ${it.dataType}"
                }
                .joinToString(CommonUtils.Comma)

        return "alter table ${definition.name} replace columns ($columnNames)"
    }

    override fun buildDropIndex(
            definition: YaormModel.Definition,
            columns: List<YaormModel.PropertyDefinition>): String? {
        return null
    }

    override fun buildCreateIndex(
            definition: YaormModel.Definition,
            properties: List<YaormModel.PropertyDefinition>,
            includes: List<YaormModel.PropertyDefinition>): String? {
        return null
    }

    override fun buildUpdateWithCriteria(
            definition: YaormModel.Definition,
            record: YaormModel.Record,
            whereClauseItem: YaormModel.WhereClauseItem): String? {
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

            val criteriaString: String = CommonUtils
                    .buildWhereClause(whereClauseItem)
            val updateKvp = ArrayList<String>()

            record
                .columnsList
                .forEach {
                    updateKvp.add(it.propertyDefinition.name + CommonUtils.Equals + CommonUtils.getFormattedString(it))
                }

            // nope, not updating entire table
            if (criteriaString.length == 0) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    definition.name,
                    updateKvp.joinToString(CommonUtils.Comma + CommonUtils.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildDropTable(definition: YaormModel.Definition): String {
        return "drop table ${definition.name}"
    }

    override fun buildDeleteAll(definition: YaormModel.Definition): String {
        return "delete from ${definition.name}"
    }

    override fun buildDeleteWithCriteria(
            definition: YaormModel.Definition,
            whereClauseItem: YaormModel.WhereClauseItem): String {
        val whereClause = CommonUtils.buildWhereClause(whereClauseItem)
        return "delete from ${definition.name} where $whereClause"
    }

    override fun buildBulkInsert(
            definition: YaormModel.Definition,
            records: YaormModel.Records): String {
        val tableName = definition.name
        val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
        CommonUtils.getNameTypes(
                definition,
                this.javaIdName,
                YaormModel.ProtobufType.STRING,
                this.javaTypeToSqlType)
                .forEach { nameTypeMap.put(it.sqlColumnName, it) }

        val columnNames = ArrayList<String>()

        definition
            .propertyDefinitionsList
            .sortedBy { it.name }
            .forEach {
                if (nameTypeMap.containsKey(it.name)) {
                    columnNames.add(it.name)
                }
            }

        val initialStatement = "insert into table $tableName \nselect * from\n"
        val selectStatements = ArrayList<String>()

        records
            .recordsList
            .forEach { instance ->
                val valueColumnPairs = ArrayList<String>()

                instance
                    .columnsList
                    .forEach {
                        val formattedString = CommonUtils.getFormattedString(it)
                        if (valueColumnPairs.isEmpty()) {
                            valueColumnPairs.add("select $formattedString as ${it.propertyDefinition.name}")
                        }
                        else {
                            valueColumnPairs.add("$formattedString as ${it.propertyDefinition.name}")
                        }
                    }

                selectStatements.add(valueColumnPairs.joinToString(CommonUtils.Comma))
            }

        val carriageReturnSeparatedRows = selectStatements.joinToString("${CommonUtils.Comma}${CommonUtils.CarriageReturn}")

        return "$initialStatement(\nselect stack(\n ${selectStatements.size},\n $carriageReturnSeparatedRows)) s"
    }

    override fun buildSelectAll(definition: YaormModel.Definition, n: Int): String {
        return java.lang.String.format(SelectAllTemplate, definition.name, n)
    }

    override fun buildWhereClause(
            definition: YaormModel.Definition,
            whereClauseItem: YaormModel.WhereClauseItem): String? {
        val whereClauseItems = CommonUtils.buildWhereClause(whereClauseItem)

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                definition.name,
                whereClauseItems)

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

    override fun buildUpdateTable(definition: YaormModel.Definition, record: YaormModel.Record): String? {
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
                    if (it.propertyDefinition.name.equals(this.javaIdName)) {
                        stringId = formattedString
                    }
                    else {
                        updateKvp.add(it.propertyDefinition.name + CommonUtils.Equals + formattedString)
                    }
                }

            if (stringId == null) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    tableName,
                    updateKvp.joinToString(CommonUtils.Comma + CommonUtils.Space),
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
            val values = ArrayList<String>()

            record
                .columnsList
                .sortedBy { it.propertyDefinition.name }
                .forEach {
                    values.add(CommonUtils.getFormattedString(it))
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    definition.name,
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
            if (workspace.length == 0) {
                workspace
                    .append(CommonUtils.Space)
                    .append(nameType.sqlColumnName)
                    .append(CommonUtils.Space)
                    .append(nameType.dataType)
            }
            else {
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
            workspace.toString(),
            10)

        return createTableSql
    }
}
