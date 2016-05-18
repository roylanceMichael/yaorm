package org.roylance.yaorm.services.hive

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlGeneratorUtils
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

    override val javaTypeToSqlType: Map<String, String> = object : HashMap<String, String>() {
        init {
            put(CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName, HiveString)
            put(CommonSqlDataTypeUtilities.JavaAlt1BooleanName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaAltBooleanName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaAltIntegerName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaAlt1IntegerName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaAltLongName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaAlt1LongName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaAltDoubleName, HiveDouble)
            put(CommonSqlDataTypeUtilities.JavaAlt1DoubleName, HiveDouble)
            put(CommonSqlDataTypeUtilities.JavaStringName, HiveString)
            put(CommonSqlDataTypeUtilities.JavaByteName, HiveString)
            put(CommonSqlDataTypeUtilities.JavaIntegerName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaDoubleName, HiveDouble)
            put(CommonSqlDataTypeUtilities.JavaBooleanName, HiveInt)
            put(CommonSqlDataTypeUtilities.JavaLongName, HiveInt)
        }
    }

    override fun buildCountSql(definition: DefinitionModel): String {
        return "select count(1) as longVal from ${definition.name}"
    }

    override fun buildCreateColumn(
            definition: DefinitionModel,
            propertyDefinition: PropertyDefinitionModel): String? {
        if (!this.javaTypeToSqlType.containsKey(propertyDefinition.type)) {
            return null
        }
        return "alter table ${definition.name} add columns (${propertyDefinition.name}, ${this.javaTypeToSqlType[propertyDefinition.type]})"
    }

    override fun buildDropColumn(
            definition: DefinitionModel, propertyDefinition: PropertyDefinitionModel): String? {
        val columnNames = CommonSqlDataTypeUtilities.getNameTypes(
                definition,
                this.javaIdName,
                CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                this.javaTypeToSqlType)
                .map {
                    "${it.sqlColumnName} ${it.dataType}"
                }
                .joinToString(CommonSqlDataTypeUtilities.Comma)

        return "alter table ${definition.name} replace columns ($columnNames)"
    }

    override fun buildDropIndex(
            definition: DefinitionModel,
            columns: List<PropertyDefinitionModel>): String? {
        return null
    }

    override fun buildCreateIndex(
            definition: DefinitionModel,
            properties: List<PropertyDefinitionModel>,
            includes: List<PropertyDefinitionModel>): String? {
        return null
    }

    override fun buildUpdateWithCriteria(
            definition: DefinitionModel,
            newValues: Map<String, Any?>,
            whereClauseItem: WhereClauseItem): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonSqlDataTypeUtilities.getNameTypes(
                    definition,
                    this.javaIdName,
                    CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val criteriaString: String = CommonSqlDataTypeUtilities
                    .buildWhereClause(whereClauseItem)
            val updateKvp = ArrayList<String>()

            val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                    definition,
                    nameTypeMap,
                    newValues)

            mapWithCorrectValues
                .forEach {
                    updateKvp.add(it.key + CommonSqlDataTypeUtilities.Equals + it.value)
                }

            // nope, not updating entire table
            if (criteriaString.length == 0) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    definition.name,
                    updateKvp.joinToString(CommonSqlDataTypeUtilities.Comma + CommonSqlDataTypeUtilities.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildDropTable(definition: DefinitionModel): String {
        return "drop table ${definition.name}"
    }

    override fun buildDeleteAll(definition: DefinitionModel): String {
        return "delete from ${definition.name}"
    }

    override fun buildDeleteWithCriteria(
            definition: DefinitionModel,
            whereClauseItem: WhereClauseItem): String {
        val whereClause = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
        return "delete from ${definition.name} where $whereClause"
    }

    override fun buildBulkInsert(
            definition: DefinitionModel,
            items: List<Map<String, Any?>>): String {
        val tableName = definition.name
        val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
        CommonSqlDataTypeUtilities.getNameTypes(
                definition,
                this.javaIdName,
                CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                this.javaTypeToSqlType)
                .forEach { nameTypeMap.put(it.sqlColumnName, it) }

        val columnNames = ArrayList<String>()

        definition
            .properties
            .sortedBy { it.name }
            .forEach {
                if (nameTypeMap.containsKey(it.name)) {
                    columnNames.add(it.name)
                }
            }

        val initialStatement = "insert into table $tableName \nselect * from\n"
        val selectStatements = ArrayList<String>()

        items
            .forEach { instance ->
                val valueColumnPairs = ArrayList<String>()
                val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                        definition,
                        nameTypeMap,
                        instance)

                mapWithCorrectValues
                    .forEach {
                        if (valueColumnPairs.isEmpty()) {
                            valueColumnPairs.add("select ${it.value} as ${it.key}")
                        }
                        else {
                            valueColumnPairs.add("${it.value} as ${it.key}")
                        }
                    }

                selectStatements.add(valueColumnPairs.joinToString(CommonSqlDataTypeUtilities.Comma))
            }

        val carriageReturnSeparatedRows = selectStatements.joinToString("${CommonSqlDataTypeUtilities.Comma}${CommonSqlDataTypeUtilities.CarriageReturn}")

        return "$initialStatement(\nselect stack(\n ${selectStatements.size},\n $carriageReturnSeparatedRows)) s"
    }

    override fun buildSelectAll(definition: DefinitionModel, n: Int): String {
        return java.lang.String.format(SelectAllTemplate, definition.name, n)
    }

    override fun buildWhereClause(
            definition: DefinitionModel,
            whereClauseItem: WhereClauseItem): String? {
        val whereClauseItems = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                definition.name,
                whereClauseItems)

        return whereSql
    }

    override fun buildDeleteTable(definition: DefinitionModel, primaryKey: Any): String? {
        val tableName = definition.name

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun buildUpdateTable(definition: DefinitionModel, updateModel: Map<String, Any?>): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonSqlDataTypeUtilities.getNameTypes(
                    definition,
                    this.javaIdName,
                    CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val tableName = definition.name
            var stringId: String? = null

            val updateKvp = ArrayList<String>()

            val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                    definition,
                    nameTypeMap,
                    updateModel)

            mapWithCorrectValues
                .forEach {
                    if (it.key.equals(this.javaIdName)) {
                        stringId = it.value
                    }
                    else {
                        updateKvp.add(it.key + CommonSqlDataTypeUtilities.Equals + it.value)
                    }
                }

            if (stringId == null) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    tableName,
                    updateKvp.joinToString(CommonSqlDataTypeUtilities.Comma + CommonSqlDataTypeUtilities.Space),
                    stringId!!)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildInsertIntoTable(
            definition: DefinitionModel,
            newInsertModel: Map<String, Any?>): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()

            CommonSqlDataTypeUtilities.getNameTypes(
                    definition,
                    this.javaIdName,
                    CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            val values = ArrayList<String>()
            val mapWithCorrectValues = SqlGeneratorUtils.buildInsertUpdateValues(
                    definition,
                    nameTypeMap,
                    newInsertModel)

            mapWithCorrectValues
                .forEach {
                    values.add(it.value)
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    definition.name,
                    values.joinToString(CommonSqlDataTypeUtilities.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun buildCreateTable(definition: DefinitionModel): String? {
        val nameTypes = CommonSqlDataTypeUtilities.getNameTypes(
                definition,
                this.javaIdName,
                CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                this.javaTypeToSqlType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        for (nameType in nameTypes) {
            if (workspace.length == 0) {
                workspace
                    .append(CommonSqlDataTypeUtilities.Space)
                    .append(nameType.sqlColumnName)
                    .append(CommonSqlDataTypeUtilities.Space)
                    .append(nameType.dataType)
            }
            else {
                workspace
                    .append(CommonSqlDataTypeUtilities.Comma)
                    .append(CommonSqlDataTypeUtilities.Space)
                    .append(nameType.sqlColumnName)
                    .append(CommonSqlDataTypeUtilities.Space)
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
