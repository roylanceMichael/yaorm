package org.roylance.yaorm.services.sqlite

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.Tuple
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

public class SqliteGeneratorService : ISqlGeneratorService {

    private val CreateInitialTableTemplate = "create table if not exists %s (%s);"
    private val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
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
            put(CommonSqlDataTypeUtilities.JavaAltLongName, SqlBlobName)
            put(CommonSqlDataTypeUtilities.JavaAltDoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaStringName, SqlTextName)
            put(CommonSqlDataTypeUtilities.JavaByteName, SqlBlobName)
            put(CommonSqlDataTypeUtilities.JavaIntegerName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaDoubleName, SqlRealName)
            put(CommonSqlDataTypeUtilities.JavaBooleanName, SqlIntegerName)
            put(CommonSqlDataTypeUtilities.JavaLongName, SqlIntegerName)
        }
    }

    override val bulkInsertSize: Int = 500

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

        val commaSeparatedColumnNames = columnNames.join(CommonSqlDataTypeUtilities.Comma)
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

                    selectStatements.add(valueColumnPairs.join(CommonSqlDataTypeUtilities.Comma))
                }

        val unionSeparatedStatements = selectStatements.join(CommonSqlDataTypeUtilities.SpacedUnion)

        return "$initialStatement $unionSeparatedStatements${CommonSqlDataTypeUtilities.SemiColon}"
    }

    override public fun <K, T: IEntity<K>> buildSelectAll(classModel: Class<T>): String {
        return java.lang.String.format(SelectAllTemplate, classModel.simpleName)
    }

    override public fun <K, T: IEntity<K>> buildWhereClauseAnd(classModel: Class<T>, values: Map<String, Any>, operator:String): Optional<String> {

        val tableName = classModel.simpleName
        val andItems = ArrayList<String>()

        for (columnName in values.keySet()) {
            val stringValue = CommonSqlDataTypeUtilities.getFormattedString(values.get(columnName))

            andItems.add(columnName + operator + stringValue)
        }

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                tableName,
                andItems.join(CommonSqlDataTypeUtilities.SpacedAnd))

        return Optional.of<String>(whereSql)
    }

    override public fun <K, T: IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): Optional<String> {

        val tableName = classModel.simpleName

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return Optional.of<String>(deleteSql)
    }

    override public fun <K, T: IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): Optional<String> {
        try {

            val nameTypeMap = HashMap<String, Tuple<String>>()
            getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

            if (nameTypeMap.size() == 0) {
                return Optional.absent()
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
                return Optional.absent()
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    tableName,
                    updateKvp.join(CommonSqlDataTypeUtilities.Comma + CommonSqlDataTypeUtilities.Space),
                    stringId!!)

            return Optional.of(updateSql)
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.absent<String>()
        }

    }

    override public fun <K, T: IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): Optional<String> {
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
                    columnNames.join(CommonSqlDataTypeUtilities.Comma),
                    values.join(CommonSqlDataTypeUtilities.Comma))

            return Optional.of(insertSql)
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.absent()
        }
    }

    override public fun <K, T: IEntity<K>> buildInitialTableCreate(classType: Class<T>): Optional<String> {

        val nameTypes = this.getNameTypes(classType)

        if (nameTypes.size() == 0) {
            return Optional.absent()
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

        return Optional.of<String>(createTableSql)
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
                        val dataType = this.javaTypeToSqlType.get(javaType)

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
