package org.roylance.yaorm.services.mysql

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

class MySQLGeneratorService(private val schemaName: String) : ISqlGeneratorService {
    private val CreateInitialTableTemplate = "create table if not exists %s (%s)"
    private val InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);"
    private val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
    private val UpdateTableMultipleTemplate = "update %s set %s where %s;"
    private val DeleteTableTemplate = "delete from %s where id=%s;"
    private val PrimaryKey = "primary key"

    private val SqlIntegerName = "bigint"
    // http://dev.mysql.com/doc/refman/5.0/en/char.html - thank you
    private val SqlTextName = "varchar(4000)"
    private val SqlTextIdName = "varchar(40)"
    private val SqlRealName = "decimal"
    private val SqlBlobName = "blob"

    override val javaIdName: String = "id"

    override val javaTypeToSqlType: Map<String, String> = object : HashMap<String, String>() {
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

    override val bulkInsertSize: Int = 1000

    override fun <K, T : IEntity<K>> buildCountSql(
            classType: Class<T>): String {
        return "select count(1) as longVal from ${classType.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildCreateColumn(
            classType: Class<T>,
            columnName: String,
            javaType: String): String? {
        if (!this.javaTypeToSqlType.containsKey(javaType)) {
            return null
        }
        return "alter table " +
                "${this.schemaName}.${classType.simpleName} " +
                "add column $columnName ${this.javaTypeToSqlType[javaType]}"
    }

    override fun <K, T : IEntity<K>> buildDropColumn(
            classType: Class<T>, columnName: String): String? {
        return "alter table " +
                "${this.schemaName}.${classType.simpleName} " +
                "drop column $columnName"
    }

    override fun <K, T : IEntity<K>> buildCreateIndex(
            classType: Class<T>,
            columns: List<String>,
            includes: List<String>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns)
        val joinedColumnNames = columns.joinToString(CommonSqlDataTypeUtilities.Comma)
        val sqlStatement = "create index $indexName on " +
                "${this.schemaName}.${classType.simpleName} " +
                "($joinedColumnNames) using BTREE"
        return sqlStatement
    }

    override fun <K, T : IEntity<K>> buildDropIndex(
            classType: Class<T>, columns: List<String>): String? {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns)
        return "drop index $indexName on ${this.schemaName}.${classType.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildDropTable(
            classType: Class<T>): String {
        return "drop table if exists ${this.schemaName}.${classType.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildCreateTable(
            classType: Class<T>): String? {

        val nameTypes = CommonSqlDataTypeUtilities.getNameTypes(
                classType,
                this.javaIdName,
                this.javaTypeToSqlType)

        if (nameTypes.size == 0) {
            return null
        }

        val workspace = StringBuilder()

        for (nameType in nameTypes) {
            if (javaIdName.equals(nameType.sqlColumnName)) {
                // if type is string, let's limit it to 40 chars
                var dataType = nameType.dataType
                if (SqlTextName.equals(dataType)) {
                    dataType = SqlTextIdName
                }

                workspace
                        .append(nameType.sqlColumnName)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(dataType)
                        .append(CommonSqlDataTypeUtilities.Space)
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
                        .append(CommonSqlDataTypeUtilities.Comma)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(nameType.sqlColumnName)
                        .append(CommonSqlDataTypeUtilities.Space)
                        .append(dataType)
            }
        }

        // set primary key for javaId, always
        val createTableSql = java.lang.String.format(
                CreateInitialTableTemplate,
                classType.simpleName,
                workspace.toString())

        return createTableSql
    }

    override fun <K, T : IEntity<K>> buildDeleteAll(
            classModel: Class<T>): String {
        return "delete from ${classModel.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildDeleteTable(
            classModel: Class<T>,
            primaryKey: K): String? {
        val tableName = classModel.simpleName
        val deleteSql = java.lang.String.format(
                DeleteTableTemplate,
                tableName,
                CommonSqlDataTypeUtilities.getFormattedString(primaryKey))

        return deleteSql
    }

    override fun <K, T : IEntity<K>> buildDeleteWithCriteria(
            classModel: Class<T>, whereClauseItem: WhereClauseItem): String {
        val whereClause = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
        return "delete from ${classModel.simpleName} where $whereClause"    }

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
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) }
                .sortedBy { it.name }
                .forEach {
                    val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
                    if (nameTypeMap.containsKey(actualName) &&
                            !columnNames.contains(actualName)) {
                        columnNames.add(actualName)
                    }
                    else if (nameTypeMap.containsKey(actualName) &&
                            nameTypeMap[actualName]!!.isForeignKey) {
                        columnNames.add(actualName)
                    }
                }

        val commaSeparatedColumnNames = columnNames.joinToString(CommonSqlDataTypeUtilities.Comma)
        val initialStatement = "insert into ${this.schemaName}.$tableName ($commaSeparatedColumnNames) "
        val selectStatements = ArrayList<String>()

        items
                .forEach { instance ->
                    val valueColumnPairs = ArrayList<String>()

                    classModel
                            .methods
                            .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get)}
                            .sortedBy { it.name }
                            .forEach {
                                val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                        it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                                val javaType = it.returnType.name
                                if (nameTypeMap.containsKey(actualName) &&
                                        this.javaTypeToSqlType.containsKey(javaType)) {

                                    val instanceValue = it.invoke(instance)
                                    val cleansedValue = CommonSqlDataTypeUtilities
                                            .getFormattedString(instanceValue)

                                    if (valueColumnPairs.isEmpty()) {
                                        valueColumnPairs.add("select $cleansedValue as $actualName")
                                    }
                                    else {
                                        valueColumnPairs.add("$cleansedValue as $actualName")
                                    }
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

        val unionSeparatedStatements = selectStatements.joinToString(CommonSqlDataTypeUtilities.SpacedUnion)

        return "$initialStatement $unionSeparatedStatements${CommonSqlDataTypeUtilities.SemiColon}"
    }

    override fun <K, T : IEntity<K>> buildInsertIntoTable(
            classModel: Class<T>, newInsertModel: T): String? {
        try {
            val nameTypeMap = HashMap<String, ColumnNameTuple<String>>()

            CommonSqlDataTypeUtilities.getNameTypes(
                    classModel,
                    this.javaIdName,
                    this.javaTypeToSqlType)
                    .forEach { nameTypeMap.put(it.sqlColumnName, it) }

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            classModel
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) }
                    .sortedBy { it.name }
                    .forEach {
                        val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                        val javaType = it.returnType.name

                        if (nameTypeMap.containsKey(actualName) &&
                                this.javaTypeToSqlType.containsKey(javaType)) {
                            columnNames.add(actualName)

                            val instanceValue = it.invoke(newInsertModel)
                            values.add(CommonSqlDataTypeUtilities.getFormattedString(instanceValue))
                        }
                        else if (nameTypeMap.containsKey(actualName) &&
                                nameTypeMap[actualName]!!.isForeignKey) {
                            columnNames.add(actualName)
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
                    columnNames.joinToString(CommonSqlDataTypeUtilities.Comma),
                    values.joinToString(CommonSqlDataTypeUtilities.Comma))

            return insertSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun <K, T : IEntity<K>> buildUpdateTable(
            classModel: Class<T>, updateModel: T): String? {
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
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) }
                    .sortedBy { it.name }
                    .forEach {
                        val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))

                        val javaType = it.returnType.name
                        if (this.javaTypeToSqlType.containsKey(javaType)) {


                            val actualValue = it.invoke(updateModel)
                            val stringValue = CommonSqlDataTypeUtilities.getFormattedString(actualValue)

                            if (javaIdName.equals(actualName)) {
                                stringId = stringValue
                            }
                            else if (nameTypeMap.containsKey(actualName)) {
                                updateKvp.add(actualName + CommonSqlDataTypeUtilities.Equals + stringValue)
                            }
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
                    updateKvp.joinToString(
                            CommonSqlDataTypeUtilities.Comma +
                                    CommonSqlDataTypeUtilities.Space),
                    stringId!!)

            return updateSql
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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

            val tableName = classModel.simpleName
            var criteriaString: String = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
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
        }    }

    override fun <K, T : IEntity<K>> buildSelectAll(
            classModel: Class<T>): String {
        return "select * from ${this.schemaName}.${classModel.simpleName}"
    }

    override fun <K, T : IEntity<K>> buildWhereClause(
            classModel: Class<T>, whereClauseItem: WhereClauseItem): String? {
        val whereClause = CommonSqlDataTypeUtilities.buildWhereClause(whereClauseItem)
        return "select * from " +
                "${this.schemaName}.${classModel.simpleName} " +
                "where $whereClause"
    }
}
