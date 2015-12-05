package org.roylance.yaorm.services.hive

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

public class HiveGeneratorService(
        public override val bulkInsertSize: Int = 2000
) : ISqlGeneratorService {

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

    override fun <K, T : IEntity<K>> buildCountSql(classType: Class<T>): String {
        return "select count(1) as longVal from ${classType.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildCreateColumn(
            classType: Class<T>,
            columnName: String,
            javaType: String): String? {
        if (!this.javaTypeToSqlType.containsKey(javaType)) {
            return null
        }
        return "alter table ${classType.simpleName} add columns ($columnName, ${this.javaTypeToSqlType[javaType]})"
    }

    override fun <K, T : IEntity<K>> buildDropColumn(
            classType: Class<T>,
            columnName: String): String? {
        val columnNames = CommonSqlDataTypeUtilities.getNameTypes(
                classType,
                this.javaIdName,
                this.javaTypeToSqlType)
                .map {
                    "${it.sqlColumnName} ${it.dataType}"
                }
                .joinToString(CommonSqlDataTypeUtilities.Comma)
        return "alter table ${classType.simpleName} replace columns ($columnNames)"
    }

    override fun <K, T : IEntity<K>> buildDropIndex(
            classType: Class<T>,
            columns: List<String>): String? {
        return null
    }

    override fun <K, T : IEntity<K>> buildCreateIndex(
            classType: Class<T>,
            columns: List<String>,
            includes: List<String>): String? {
        return null
    }

    override fun <K, T : IEntity<K>> buildUpdateWithCriteria(
            classModel: Class<T>,
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonSqlDataTypeUtilities.getNameTypes(
                    classModel,
                    this.javaIdName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            var criteriaString: String = CommonSqlDataTypeUtilities
                    .buildWhereClause(whereClauseItem)
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
                    classModel.simpleName,
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

    override fun <K, T : IEntity<K>> buildDeleteAll(classModel: Class<T>): String {
        return "delete from ${classModel.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildDeleteWithCriteria(
            classModel: Class<T>,
            whereClauseItem: WhereClauseItem): String {
        val whereClause = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
        return "delete from ${classModel.simpleName} where $whereClause"
    }

    override fun <K, T : IEntity<K>> buildBulkInsert(
            classModel: Class<T>,
            items: List<T>): String {
        val tableName = classModel.simpleName
        val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
        CommonSqlDataTypeUtilities.getNameTypes(
                classModel,
                this.javaIdName,
                this.javaTypeToSqlType)
                .forEach { nameTypeMap.put(it.sqlColumnName, it) }

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
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                    .sortedBy { it.name }
                    .forEach {
                        val actualName = CommonSqlDataTypeUtilities
                                .lowercaseFirstChar(
                                        it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                        if (nameTypeMap.containsKey(actualName) &&
                                !nameTypeMap[actualName]!!.isForeignKey) {
                            val instanceValue = it.invoke(instance)
                            val cleansedValue = CommonSqlDataTypeUtilities.getFormattedString(instanceValue)

                            valueColumnPairs.add(cleansedValue)
                        }
                        else if (nameTypeMap.containsKey(actualName) &&
                                nameTypeMap[actualName]!!.isForeignKey) {

                            val foreignObject = it.invoke(instance)
                            var strValue:String?

                            if (foreignObject != null) {
                                val instanceValue = (foreignObject as IEntity<*>).id
                                strValue = CommonSqlDataTypeUtilities
                                        .getFormattedString(instanceValue)
                            }
                            else {
                                strValue = CommonSqlDataTypeUtilities.Null
                            }

                            if (valueColumnPairs.isEmpty()) {
                                valueColumnPairs.add("select $strValue as $actualName")
                            }
                            else {
                                valueColumnPairs.add("$strValue as $actualName")
                            }
                        }
                    }

                selectStatements.add(valueColumnPairs.joinToString(CommonSqlDataTypeUtilities.Comma))
            }

        val carriageReturnSeparatedRows = selectStatements.joinToString("${CommonSqlDataTypeUtilities.Comma}${CommonSqlDataTypeUtilities.CarriageReturn}")

        return "$initialStatement(\nselect stack(\n ${selectStatements.size},\n $carriageReturnSeparatedRows)) s"
    }

    override fun <K, T : IEntity<K>> buildSelectAll(classModel: Class<T>): String {
        return java.lang.String.format(SelectAllTemplate, classModel.simpleName)
    }

    override fun <K, T : IEntity<K>> buildWhereClause(
            classModel: Class<T>,
            whereClauseItem: WhereClauseItem): String? {
        val whereClauseItems = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)

        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                classModel.simpleName,
                whereClauseItems)

        return whereSql
    }

    override fun <K, T : IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): String? {
        val tableName = classModel.simpleName

        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun <K, T : IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()
            CommonSqlDataTypeUtilities.getNameTypes(
                    classModel,
                    this.javaIdName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            if (nameTypeMap.size == 0) {
                return null
            }

            val tableName = classModel.simpleName
            var stringId: String? = null

            val updateKvp = ArrayList<String>()

            classModel
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                        !CommonSqlDataTypeUtilities
                                .JavaObjectName
                                .equals(it.genericReturnType.typeName)
                }
                .sortedBy { it.name }
                .forEach {
                    val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                    val actualValue = it.invoke(updateModel)
                    val stringValue = CommonSqlDataTypeUtilities.getFormattedString(actualValue)

                    if (javaIdName.equals(actualName)) {
                        stringId = stringValue
                    }
                    else if (this.javaTypeToSqlType.containsKey(it.returnType.name)) {
                        updateKvp.add(
                                actualName + CommonSqlDataTypeUtilities.Equals + stringValue)
                    }
                    else if(nameTypeMap.containsKey(actualName) &&
                            nameTypeMap[actualName]!!.isForeignKey) {
                        val foreignObject = it.invoke(updateModel)

                        if (foreignObject != null) {
                            val instanceValue = (foreignObject as IEntity<*>).id
                            val strValue = CommonSqlDataTypeUtilities
                                    .getFormattedString(instanceValue)
                            updateKvp.add(actualName + CommonSqlDataTypeUtilities.Equals + strValue)
                        }
                        else {
                            updateKvp.add(actualName + CommonSqlDataTypeUtilities.Is + CommonSqlDataTypeUtilities.Null)
                        }
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

    override fun <K, T : IEntity<K>> buildInsertIntoTable(
            classModel: Class<T>,
            newInsertModel: T): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()

            CommonSqlDataTypeUtilities.getNameTypes(
                    classModel,
                    this.javaIdName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            val values = ArrayList<String>()

            classModel
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                        !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                .sortedBy { it.name }
                .forEach {
                    val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                    if (nameTypeMap.containsKey(actualName)) {
                        val instanceValue = it.invoke(newInsertModel)
                        values.add(CommonSqlDataTypeUtilities.getFormattedString(instanceValue))
                    }
                    else if (nameTypeMap.containsKey(actualName) &&
                            nameTypeMap[actualName]!!.isForeignKey) {
                        val actualForeignObject = it.invoke(newInsertModel)

                        if (actualForeignObject != null) {
                            val instanceValue = (actualForeignObject as IEntity<*>).id
                            val strValue = CommonSqlDataTypeUtilities
                                    .getFormattedString(instanceValue)
                            values.add(strValue)
                        }
                        else {
                            values.add(CommonSqlDataTypeUtilities.Null)
                        }
                    }
                }

            val insertSql = java.lang.String.format(
                    InsertIntoTableSingleTemplate,
                    classModel.simpleName,
                    values.joinToString(CommonSqlDataTypeUtilities.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun <K, T : IEntity<K>> buildCreateTable(classType: Class<T>): String? {
        val nameTypes = CommonSqlDataTypeUtilities.getNameTypes(
                classType,
                this.javaIdName,
                this.javaTypeToSqlType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        for (nameType in CommonSqlDataTypeUtilities.getNameTypes(
                classType,
                this.javaIdName,
                this.javaTypeToSqlType)) {
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
            classType.simpleName,
            workspace.toString(),
            10)

        return createTableSql
    }
}
