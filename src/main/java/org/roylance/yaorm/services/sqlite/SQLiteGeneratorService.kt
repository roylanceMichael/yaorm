package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.Tuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

public class SQLiteGeneratorService(
        public override val bulkInsertSize: Int = 500
) : ISqlGeneratorService {

    private val CreateInitialTableTemplate = "create table if not exists %s (%s);"
    private val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
    private val UpdateTableMultipleTemplate = "update %s set %s where %s;"
    private val DeleteTableTemplate = "delete from %s where id=%s;"
    private val WhereClauseTemplate = "select * from %s where %s;"
    private val SelectAllTemplate = "select * from %s;"
    private val PrimaryKey = "primary key"

    private val AutoIncrement = "autoincrement"

    private val SqlIntegerName = "integer"
    private val SqlTextName = "text"
    private val SqlRealName = "real"
    private val SqlBlobName = "blob"

    override public val javaIdName: String = "id"

    override public val javaTypeToSqlType: Map<String, String> = object : HashMap<String, String>() {
        init {
            put(CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName, SqlTextName)
            put(CommonSqlDataTypeUtilities.JavaAlt1IntegerName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1BooleanName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1LongName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAlt1DoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaAltIntegerName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltLongName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaAltDoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaStringName, SqlTextName)
            put(CommonSqlDataTypeUtilities.JavaByteName, SqlBlobName)
            put(CommonSqlDataTypeUtilities.JavaIntegerName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaDoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaBooleanName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaLongName, SqlIntegerName)
        }
    }

    override fun <K, T : IEntity<K>> buildDropIndex(classType: Class<T>, columns: List<String>): String? {
        return null
    }

    override fun <K, T : IEntity<K>> buildIndex(classType: Class<T>, columns: List<String>): String? {
        return null
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
        try {
            val nameTypeMap = HashMap<String, Tuple<String>>()
            getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val tableName = classModel.simpleName
            var criteriaString: String = this.buildWhereClause(whereClauseItem)
            val updateKvp = ArrayList<String>()

            newValues
                    .forEach {
                        val actualName = it.key

                        val actualValue = it.value
                        val stringValue = CommonSqlDataTypeUtilities.getFormattedString(actualValue)
                        updateKvp.add(actualName + CommonSqlDataTypeUtilities.Equals + stringValue)
                    }

            // nope, not updating entire table
            if (criteriaString.length == 0) {
                return null
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    tableName,
                    updateKvp.joinToString(CommonSqlDataTypeUtilities.Comma + CommonSqlDataTypeUtilities.Space),
                    criteriaString)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun <K, T : IEntity<K>> buildDropTable(classType: Class<T>): String {
        return "drop table ${classType.simpleName}"
    }

    override public fun <K, T: IEntity<K>> buildDeleteAll(classModel: Class<T>) : String {
        return "delete from ${classModel.simpleName}"
    }

    override public fun <K, T: IEntity<K>> buildBulkInsert(classModel: Class<T>, items: List<T>) : String {

        val tableName = classModel.simpleName
        val nameTypeMap = HashMap<String, Tuple<String>>()
        getNameTypes(classModel)
                .forEach { nameTypeMap.put(it.first, it) }

        val columnNames = ArrayList<String>()

        classModel
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                .forEach {
                    val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                    if (nameTypeMap.containsKey(actualName) &&
                            !javaIdName.equals(actualName)) {
                        columnNames.add(actualName)
                    }
                }

        val commaSeparatedColumnNames = columnNames.joinToString(CommonSqlDataTypeUtilities.Comma)
        val initialStatement = "insert into $tableName ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        items
                .forEach { instance ->
                    val valueColumnPairs = ArrayList<String>()

                    classModel
                            .methods
                            .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                                    !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                            .forEach {
                                val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                        it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                                if (nameTypeMap.containsKey(actualName) &&
                                        !javaIdName.equals(actualName)) {

                                    val instanceValue = it.invoke(instance)
                                    val cleansedValue = CommonSqlDataTypeUtilities.getFormattedString(instanceValue)

                                    if (valueColumnPairs.isEmpty()) {
                                        valueColumnPairs.add("select $cleansedValue as $actualName")
                                    }
                                    else {
                                        valueColumnPairs.add("$cleansedValue as $actualName")
                                    }
                                }
                            }

                    selectStatements.add(valueColumnPairs.joinToString(CommonSqlDataTypeUtilities.Comma))
                }

        val unionSeparatedStatements = selectStatements.joinToString(CommonSqlDataTypeUtilities.SpacedUnion)

        return "$initialStatement $unionSeparatedStatements${CommonSqlDataTypeUtilities.SemiColon}"
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
        try {

            val nameTypeMap = HashMap<String, Tuple<String>>()
            getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val tableName = classModel.simpleName
            var stringId: String? = null

            val updateKvp = ArrayList<String>()

            classModel
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                    .forEach {
                        val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                        val actualValue = it.invoke(updateModel)
                        val stringValue = CommonSqlDataTypeUtilities.getFormattedString(actualValue)

                        if (javaIdName.equals(actualName)) {
                            stringId = stringValue
                        }
                        else if (nameTypeMap.containsKey(actualName)) {
                            updateKvp.add(actualName + CommonSqlDataTypeUtilities.Equals + stringValue)
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

    override public fun <K, T: IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): String? {
        try {
            val nameTypeMap = HashMap<String, Tuple<String>>()

            this.getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            classModel
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                    .forEach {
                        val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                        if (nameTypeMap.containsKey(actualName) &&
                                !javaIdName.equals(actualName)) {
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

    override public fun <K, T: IEntity<K>> buildInitialTableCreate(classType: Class<T>): String? {

        val nameTypes = this.getNameTypes(classType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        workspace.append(javaIdName)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(SqlIntegerName)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(PrimaryKey)
                .append(CommonSqlDataTypeUtilities.Space)
                .append(AutoIncrement)

        for (nameType in getNameTypes(classType)) {
            if (!javaIdName.equals(nameType.first)) {
                workspace
                        .append(CommonSqlDataTypeUtilities.Comma)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.first)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.third)
            }
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                classType.simpleName,
                workspace.toString())

        return createTableSql
    }

    private fun buildWhereClause(whereClauseItem: WhereClauseItem):String {
        val filterItems = StringBuilder()
        var currentWhereClauseItem:WhereClauseItem? = whereClauseItem

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
