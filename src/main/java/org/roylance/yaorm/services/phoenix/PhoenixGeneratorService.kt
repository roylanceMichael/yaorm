package org.roylance.yaorm.services.phoenix

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.Tuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.ISqlGeneratorService
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

/**
 * Created by mikeroylance on 10/26/15.
 */
public class PhoenixGeneratorService (
        public override val bulkInsertSize: Int = 500
) : ISqlGeneratorService {

    private val CreateInitialTableTemplate = "create table if not exists %s (%s)"
    private val InsertIntoTableSingleTemplate = "upsert into %s (%s) values (%s)"
    private val DeleteTableTemplate = "delete from %s where id=%s"
    private val WhereClauseTemplate = "select * from %s where %s"
    private val SelectAllTemplate = "select * from %s"
    private val ConstraintTemplate = "constraint"
    private val PKTemplate = "PK"
    private val PrimaryKey = "primary key"
    private val NotNull = "not null"

    private val PhoenixIntegerName = "integer"
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

    override fun <K, T : IEntity<K>> buildDropIndex(classType: Class<T>, columns: List<String>): Optional<String> {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns)

        return Optional.of("drop index if exists $indexName on ${classType.simpleName}")
    }

    override fun <K, T : IEntity<K>> buildIndex(classType: Class<T>, columns: List<String>): Optional<String> {
        val indexName = CommonSqlDataTypeUtilities.buildIndexName(columns)
        val joinedColumnNames = columns.joinToString { CommonSqlDataTypeUtilities.Comma }

        return Optional.of("create index if not exists $indexName on ${classType.simpleName} ($joinedColumnNames)")
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
            whereClauseItem: WhereClauseItem): Optional<String> {
        return Optional.absent()
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

    override public fun <K, T: IEntity<K>> buildWhereClause(classModel: Class<T>, whereClauseItem: WhereClauseItem): Optional<String> {
        val whereSql = java.lang.String.format(
                WhereClauseTemplate,
                classModel.simpleName,
                this.buildWhereClause(whereClauseItem))

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
        return this.buildInsertIntoTable(classModel, updateModel)
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

            return Optional.of(insertSql)
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.absent()
        }
    }

    override public fun <K, T: IEntity<K>> buildInitialTableCreate(classType: Class<T>): Optional<String> {
        val nameTypes = this.getNameTypes(classType)

        if (nameTypes.size == 0) {
            return Optional.absent()
        }

        val workspace = StringBuilder()

        val foundId = nameTypes.firstOrNull { javaIdName.equals(it.first) } ?: return Optional.absent()

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

        return Optional.of<String>(createTableSql)
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
