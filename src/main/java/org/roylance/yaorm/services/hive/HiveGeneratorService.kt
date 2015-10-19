package org.roylance.yaorm.services.hive

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.Tuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

public class HiveGeneratorService : ISqlGeneratorService {

    private val constJavaIdName = "id"

    private val CreateInitialTableTemplate = "create table if not exists %s (%s)\nclustered by ($constJavaIdName)\ninto %s buckets\nstored as orc TBLPROPERTIES ('transactional'='true')"
    private val InsertIntoTableSingleTemplate = "insert into %s values (%s)"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s"
    private val UpdateTableMultipleTemplate = "update %s set %s where %s"
    private val DeleteTableTemplate = "delete from %s where id=%s"
    private val WhereClauseTemplate = "select * from %s where %s"
    private val SelectAllTemplate = "select * from %s"

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

    override val bulkInsertSize: Int = 10000

    override fun <K, T : IEntity<K>> buildUpdateWithCriteria(
            classModel: Class<T>,
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): Optional<String> {
        try {
            val nameTypeMap = HashMap<String, Tuple<String>>()
            getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

            if (nameTypeMap.size() == 0) {
                return Optional.absent()
            }

            var criteriaString: String = this.buildWhereClause(whereClauseItem)
            val updateKvp = ArrayList<String>()

            newValues
                    .forEach {
                        val actualName = it.getKey()
                        val actualValue = it.getValue()
                        val stringValue = CommonSqlDataTypeUtilities.getFormattedString(actualValue)
                        updateKvp.add(actualName + CommonSqlDataTypeUtilities.Equals + stringValue)
                    }

            // nope, not updating entire table
            if (criteriaString.length() == 0) {
                return Optional.absent()
            }

            val updateSql = java.lang.String.format(
                    UpdateTableMultipleTemplate,
                    classModel.simpleName,
                    updateKvp.join(CommonSqlDataTypeUtilities.Comma + CommonSqlDataTypeUtilities.Space),
                    criteriaString)

            return Optional.of(updateSql)
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.absent<String>()
        }
    }

    override fun <K, T : IEntity<K>> buildDropTable(classType: Class<T>): String {
        return "drop table ${classType.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildDeleteAll(classModel: Class<T>): String {
        return "delete from ${classModel.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildDeleteWithCriteria(
            classModel: Class<T>,
            whereClauseItem: WhereClauseItem): String {
        val whereClause = this.buildWhereClause(whereClauseItem)
        return "delete from ${classModel.simpleName} where $whereClause"
    }

    override fun <K, T : IEntity<K>> buildBulkInsert(classModel: Class<T>, items: List<T>): String {
        val tableName = classModel.simpleName
        val nameTypeMap = HashMap<String, Tuple<String>>()
        getNameTypes(classModel)
                .forEach { nameTypeMap.put(it.first, it) }

        val columnNames = ArrayList<String>()

        classModel
                .methods
                .sortedBy { it.name }
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                .forEach {
                    val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                    if (nameTypeMap.containsKey(actualName) &&
                            !javaIdName.equals(actualName)) {
                        columnNames.add(actualName)
                    }
                }

        val initialStatement = "insert into table $tableName \nselect * from\n"
        val selectStatements = ArrayList<String>()

        items
                .forEach { instance ->
                    val valueColumnPairs = ArrayList<String>()

                    classModel
                            .methods
                            .sortedBy { it.name }
                            .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                                    !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                            .forEach {
                                val actualName = CommonSqlDataTypeUtilities
                                        .lowercaseFirstChar(it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                                if (nameTypeMap.containsKey(actualName)) {
                                    val instanceValue = it.invoke(instance)
                                    val cleansedValue = CommonSqlDataTypeUtilities.getFormattedString(instanceValue)

                                    valueColumnPairs.add(cleansedValue)
                                }
                            }

                    selectStatements.add(valueColumnPairs.join(CommonSqlDataTypeUtilities.Comma))
                }

        val carriageReturnSeparatedRows = selectStatements.join("${CommonSqlDataTypeUtilities.Comma}${CommonSqlDataTypeUtilities.CarriageReturn}")

        return "$initialStatement(\nselect stack(\n ${selectStatements.size()},\n $carriageReturnSeparatedRows)) s"
    }

    override fun <K, T : IEntity<K>> buildSelectAll(classModel: Class<T>): String {
        return java.lang.String.format(SelectAllTemplate, classModel.simpleName)
    }

    override fun <K, T : IEntity<K>> buildWhereClause(classModel: Class<T>, whereClauseItem: WhereClauseItem): Optional<String> {
        val whereClauseItems = this.buildWhereClause(whereClauseItem)

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                classModel.simpleName,
                whereClauseItems)

        return Optional.of<String>(whereSql)
    }

    override fun <K, T : IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): Optional<String> {
        val tableName = classModel.simpleName

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return Optional.of<String>(deleteSql)
    }

    override fun <K, T : IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): Optional<String> {
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
                    .sortedBy { it.name }
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

    override fun <K, T : IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): Optional<String> {
        try {
            val nameTypeMap = HashMap<String, Tuple<String>>()

            this.getNameTypes(classModel)
                    .forEach { nameTypeMap.put(it.first, it) }

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
                            val instanceValue = it.invoke(newInsertModel)
                            values.add(CommonSqlDataTypeUtilities.getFormattedString(instanceValue))
                        }
                    }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    classModel.simpleName,
                    values.join(CommonSqlDataTypeUtilities.Comma))

            return Optional.of(insertSql)
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.absent()
        }
    }

    override fun <K, T : IEntity<K>> buildInitialTableCreate(classType: Class<T>): Optional<String> {
        val nameTypes = this.getNameTypes(classType)

        if (nameTypes.size() == 0) {
            return Optional.absent()
        }

        val workspace = StringBuilder()

        for (nameType in getNameTypes(classType)) {
            if (workspace.length() == 0) {
                workspace
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.first)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.third)
            }
            else {
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
                workspace.toString(),
                10)

        return Optional.of<String>(createTableSql)
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

        return filterItems.toString()
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
                .sortedBy { it.name }
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
