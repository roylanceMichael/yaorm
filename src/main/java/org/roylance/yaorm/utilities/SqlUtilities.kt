package org.roylance.yaorm.utilities

import com.google.common.base.Optional
import org.roylance.yaorm.models.Tuple
import java.util.*

public object SqlUtilities {
    private val CreateInitialTableTemplate = "create table if not exists %s (%s);"

    private val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"

    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"

    private val DeleteTableTemplate = "delete from %s where id=%s;"

    private val WhereClauseTemplate = "select * from %s where %s;"

    private val SelectAllTemplate = "select * from %s;"

    private val PrimaryKey = "primary key"

    private val AutoIncrement = "autoincrement"

    private val Space = " "

    private val Comma = ","

    private val Equals = "="

    private val SemiColon = ";"

    private val CarriageReturn = '\n'

    private val SpacedUnion =  CarriageReturn + "union "

    private val SpacedAnd = " and "

    private val SqlIntegerName = "integer"

    private val SqlTextName = "text"

    private val SqlRealName = "real"

    private val SqlBlobName = "blob"

    public  val Get:String = "get"

    public  val Set:String = "set"

    public val JavaGetIdName:String = "getId"

    public val JavaIdName: String = "id"

    public val JavaFullyQualifiedStringName: String = "String"

    public val JavaStringName: String = "java.lang.String"

    public val JavaDoubleName: String = "double"

    public val JavaIntegerName: String = "int"

    public val JavaLongName: String = "long"

    public val JavaByteName: String = "byte"

    public val JavaBooleanName: String = "boolean"

    private val GetSetLength = Get.length()

    public val JavaTypeToSqliteType: Map<String, String> = object : HashMap<String, String>() {
        init {
            put(JavaFullyQualifiedStringName, SqlTextName)
            put(JavaStringName, SqlTextName)
            put(JavaByteName, SqlBlobName)
            put(JavaIntegerName, SqlIntegerName)
            put(JavaDoubleName, SqlRealName)
            put(JavaBooleanName, SqlIntegerName)
            put(JavaLongName, SqlIntegerName)
        }
    }

    public fun <T> buildDeleteAll(classModel: Class<T>) : String {
        return "delete from ${classModel.simpleName}"
    }

    public fun <T> buildBulkInsert(classModel: Class<T>, items: List<T>) : String {

        val tableName = classModel.simpleName
        val nameTypeMap = HashMap<String, Tuple<String>>()
        getNameTypes(classModel)
                .forEach { nameTypeMap.put(it.first, it) }

        val columnNames = ArrayList<String>()

        classModel
                .methods
                .filter { it.name.startsWith(Set) }
                .forEach {
                    val actualName = SqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(GetSetLength))

                    if (nameTypeMap.containsKey(actualName) &&
                            !JavaIdName.equals(actualName)) {
                        columnNames.add(actualName)
                    }
                }

        val commaSeparatedColumnNames = columnNames.join(Comma)
        val initialStatement = "insert into $tableName ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        items
                .forEach { instance ->
                    val valueColumnPairs = ArrayList<String>()

                    classModel
                            .methods
                            .filter { it.name.startsWith(Get) }
                            .forEach {
                                val actualName = SqlDataTypeUtilities.lowercaseFirstChar(
                                        it.name.substring(GetSetLength))

                                if (nameTypeMap.containsKey(actualName) &&
                                        !JavaIdName.equals(actualName)) {

                                    val instanceValue = it.invoke(instance)
                                    val cleansedValue = SqlDataTypeUtilities.getFormattedString(instanceValue)

                                    if (valueColumnPairs.isEmpty()) {
                                        valueColumnPairs.add("select $cleansedValue as $actualName")
                                    }
                                    else {
                                        valueColumnPairs.add("$cleansedValue as $actualName")
                                    }
                                }
                            }

                    selectStatements.add(valueColumnPairs.join(Comma))
                }

        val unionSeparatedStatements = selectStatements.join(SpacedUnion)

        return "$initialStatement $unionSeparatedStatements$SemiColon"
    }

    public fun <T> buildSelectAll(classModel: Class<T>): String {
        return java.lang.String.format(SelectAllTemplate, classModel.simpleName)
    }

    public fun <T,K> buildWhereClauseAnd(classModel: Class<T>, values: Map<String, K>, operator:String=Equals): Optional<String> {

        val tableName = classModel.simpleName
        val andItems = ArrayList<String>()

        for (columnName in values.keySet()) {
            val stringValue = SqlDataTypeUtilities.getFormattedString(values.get(columnName))

            andItems.add(columnName + operator + stringValue)
        }

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                tableName,
                andItems.join(SpacedAnd))

        return Optional.of<String>(whereSql)
    }

    public fun <T,K> buildDeleteTable(classModel: Class<T>, primaryKey: K): Optional<String> {

        val tableName = classModel.simpleName

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                SqlDataTypeUtilities.getFormattedString(primaryKey))

        return Optional.of<String>(deleteSql)
    }

    public fun <T> buildUpdateTable(classModel: Class<T>, updateModel: T): Optional<String> {
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
                    .filter { it.name.startsWith(Get) }
                    .forEach {
                        val actualName = SqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(GetSetLength))

                        val actualValue = it.invoke(updateModel)
                        val stringValue = SqlDataTypeUtilities.getFormattedString(actualValue)

                        if (JavaIdName.equals(actualName)) {
                            stringId = stringValue
                        }
                        else if (nameTypeMap.containsKey(actualName)) {
                            updateKvp.add(actualName + Equals + stringValue)
                        }
                    }

            if (stringId == null) {
                return Optional.absent()
            }

            val updateSql = java.lang.String.format(
                    UpdateTableSingleTemplate,
                    tableName,
                    updateKvp.join(Comma + Space),
                    stringId!!)

            return Optional.of(updateSql)
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.absent<String>()
        }

    }

    public fun <T> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): Optional<String> {
        try {
            val nameTypeMap = HashMap<String, Tuple<String>>()

            this.getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            classModel
                    .methods
                    .filter { it.name.startsWith(Get) }
                    .forEach {
                        val actualName = SqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(GetSetLength))

                        if (nameTypeMap.containsKey(actualName) &&
                                !JavaIdName.equals(actualName)) {
                            columnNames.add(actualName)

                            val instanceValue = it.invoke(newInsertModel)
                            values.add(SqlDataTypeUtilities.getFormattedString(instanceValue))
                        }
                    }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    classModel.simpleName,
                    columnNames.join(Comma),
                    values.join(Comma))

            return Optional.of(insertSql)
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.absent()
        }
    }

    public fun <T> buildInitialTableCreate(classType: Class<T>): Optional<String> {

        val nameTypes = this.getNameTypes(classType)

        if (nameTypes.size() == 0) {
            return Optional.absent()
        }

        val workspace = StringBuilder()

        workspace.append(JavaIdName)
                .append(Space)
                .append(SqlIntegerName)
                .append(Space)
                .append(PrimaryKey)
                .append(Space)
                .append(AutoIncrement)

        for (nameType in getNameTypes(classType)) {
            if (!JavaIdName.equals(nameType.first)) {
                workspace
                        .append(Comma)
                        .append(Space)
                        .append(nameType.first)
                        .append(Space)
                        .append(nameType.third)
            }
        }

        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                classType.simpleName,
                workspace.toString())

        return Optional.of<String>(createTableSql)
    }

    private fun <T> getNameTypes(classModel: Class<T>): List<Tuple<String>> {
        val nameTypes = ArrayList<Tuple<String>>()
        var foundIdColumnName = false

        val propertyNames = classModel
                .methods
                .filter { it.name.startsWith(Set) }
                .map { it.name.substring(GetSetLength) }
                .toHashSet()

        // let's handle the types now
        classModel
                .methods
                .filter {
                    it.name.startsWith(Get) &&
                            propertyNames.contains(it.name.substring(GetSetLength))
                }
                .forEach {
                    val columnName = it.name.substring(GetSetLength)
                    val javaType = it.returnType.name

                    if (JavaTypeToSqliteType.containsKey(javaType)) {
                        val sqlColumnName = SqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(GetSetLength)
                        )

                        val javaColumnName = columnName
                        val dataType = JavaTypeToSqliteType.get(javaType)

                        if (JavaIdName.equals(sqlColumnName)) {
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
