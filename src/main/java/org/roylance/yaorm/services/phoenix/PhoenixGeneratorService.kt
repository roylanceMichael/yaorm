package org.roylance.yaorm.services.phoenix

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.Tuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

public class PhoenixGeneratorService (
        public override val bulkInsertSize: Int = 500
) : ISqlGeneratorService {

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

    override public val javaIdName: String = "id"

    override public val javaTypeToSqlType: Map<String, String> = object : HashMap<String, String>() {
        init {
            put(CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName, PhoenixTextName)
            put(CommonSqlDataTypeUtilities.JavaAlt1IntegerName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1BooleanName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1LongName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1DoubleName, PhoenixRealName)
            put(CommonSqlDataTypeUtilities.JavaAltIntegerName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltLongName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltDoubleName, PhoenixRealName)
            put(CommonSqlDataTypeUtilities.JavaStringName, PhoenixTextName)
            put(CommonSqlDataTypeUtilities.JavaByteName, PhoenixBinaryName)
            put(CommonSqlDataTypeUtilities.JavaIntegerName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaDoubleName, PhoenixRealName)
            put(CommonSqlDataTypeUtilities.JavaBooleanName, PhoenixIntegerName)
            put(CommonSqlDataTypeUtilities.JavaLongName, PhoenixIntegerName)
        }
    }

    override fun <K, T : IEntity<K>> buildCountSql(classType: Class<T>): String {
        return "select count(1) as longVal from ${classType.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildCreateColumn(classType: Class<T>, columnName: String, javaType: String): String? {
        if (!javaTypeToSqlType.containsKey(javaType)) {
            return null
        }
        return "alter table ${classType.simpleName} add if not exists $columnName ${javaTypeToSqlType[javaType]}"
    }

    override fun <K, T : IEntity<K>> buildDropColumn(classType: Class<T>, columnName: String): String {
        return "alter table ${classType.simpleName} drop column if exists $columnName"
    }

    override fun <K, T : IEntity<K>> buildDropIndex(classType: Class<T>, columns: List<String>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns)
        return "drop index if exists $indexName on ${classType.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildCreateIndex(classType: Class<T>, columns: List<String>, includes: List<String>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns)
        val joinedColumnNames = columns.joinToString(CommonSqlDataTypeUtilities.Comma)
        val sqlStatement = "create index if not exists $indexName on ${classType.simpleName} ($joinedColumnNames)"

        if (includes.isEmpty()) {
            return sqlStatement
        }
        val joinedIncludeColumnNames = includes.joinToString(CommonSqlDataTypeUtilities.Comma)
        return "$sqlStatement include ($joinedIncludeColumnNames)"
    }

    override fun <K, T : IEntity<K>> buildDeleteWithCriteria(
            classModel: Class<T>,
            whereClauseItem: WhereClauseItem): String {
        val whereClause = this.buildWhereClause(whereClauseItem)
        return "delete from ${classModel.simpleName} where $whereClause"
    }

    override fun <K, T : IEntity<K>> buildUpdateWithCriteria(
            classModel: Class<T>,
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): String? {
        return null
    }

    override fun <K, T : IEntity<K>> buildDropTable(classType: Class<T>): String {
        return "drop table if exists ${classType.simpleName}"
    }

    override public fun <K, T: IEntity<K>> buildDeleteAll(classModel: Class<T>) : String {
        return "delete from ${classModel.simpleName}"
    }

    override public fun <K, T: IEntity<K>> buildBulkInsert(classModel: Class<T>, items: List<T>) : String {
        // do single inserts, then commit
        return ""
    }

    override public fun <K, T: IEntity<K>> buildSelectAll(classModel: Class<T>): String {
        return java.lang.String.format(SelectAllTemplate, classModel.simpleName)
    }

    override public fun <K, T: IEntity<K>> buildWhereClause(classModel: Class<T>, whereClauseItem: WhereClauseItem): String? {
        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                classModel.simpleName,
                this.buildWhereClause(whereClauseItem))

        return whereSql
    }

    override public fun <K, T: IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): String? {
        val tableName = classModel.simpleName

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return deleteSql
    }

    override public fun <K, T: IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): String? {
        return this.buildInsertIntoTable(classModel, updateModel)
    }

    override public fun <K, T: IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): String? {
        try {
            val nameTypeMap = HashMap<String, Tuple<String>>()

            this.getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            classModel
                    .methods
                    .sortedBy { it.name }
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                    .forEach {
                        val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                        if (nameTypeMap.containsKey(actualName)) {
                            columnNames.add(actualName)

                            val instanceValue = it.invoke(newInsertModel)
                            values.add(CommonSqlDataTypeUtilities.getFormattedString(instanceValue))
                        }
                    }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    classModel.simpleName,
                    columnNames.joinToString(CommonSqlDataTypeUtilities.Comma),
                    values.joinToString(CommonSqlDataTypeUtilities.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override public fun <K, T: IEntity<K>> buildCreateTable(classType: Class<T>): String? {
        val nameTypes = this.getNameTypes(classType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        val foundId = nameTypes.firstOrNull { javaIdName.equals(it.first) } ?: return null

        workspace.append(javaIdName)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(foundId.third)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(NotNull)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(PrimaryKey)

        for (nameType in nameTypes.filter { !javaIdName.equals(it.first) }) {
            workspace
                    .append(CommonSqlDataTypeUtilities.Comma)
                    .append(CommonSqlDataTypeUtilities.Space)
                    .append(nameType.first)
                    .append(CommonSqlDataTypeUtilities.Space)
                    .append(nameType.third)
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                classType.simpleName,
                workspace.toString())

        return createTableSql
    }

    private fun buildWhereClause(whereClauseItem: WhereClauseItem):String {
        val filterItems = StringBuilder()
        var currentWhereClauseItem: WhereClauseItem? = whereClauseItem

        while (currentWhereClauseItem != null) {
            val stringValue = CommonSqlDataTypeUtilities.getFormattedString(currentWhereClauseItem.rightSide)
            filterItems.append(currentWhereClauseItem.leftSide + currentWhereClauseItem.operator + stringValue + CommonSqlDataTypeUtilities.Space)

            if (currentWhereClauseItem.connectingAndOr != null) {
                filterItems.append(currentWhereClauseItem.connectingAndOr)
            }

            currentWhereClauseItem = currentWhereClauseItem.connectingWhereClause
        }

        return filterItems.toString().trim()
    }

    private fun <K, T: IEntity<K>> getNameTypes(classModel: Class<T>): List<Tuple<String>> {
        val nameTypes = ArrayList<Tuple<String>>()
        var foundIdColumnName = false

        val propertyNames = classModel
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                .map { it.name.substring(CommonSqlDataTypeUtilities.GetSetLength) }
                .toHashSet()

        // let's handle the types now
        classModel
                .methods
                .filter {
                    it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            propertyNames.contains(it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)) &&
                            !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName)
                }
                .forEach {
                    val columnName = it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)
                    val javaType = it.returnType.name

                    if (this.javaTypeToSqlType.containsKey(javaType)) {
                        val sqlColumnName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)
                        )

                        val javaColumnName = columnName
                        val dataType = this.javaTypeToSqlType[javaType]

                        if (javaIdName.equals(sqlColumnName)) {
                            foundIdColumnName = true
                        }

                        nameTypes.add(Tuple(sqlColumnName, javaColumnName, dataType!!))
                    }
                }

        if (!foundIdColumnName) {
            return ArrayList()
        }

        return nameTypes
    }
}
