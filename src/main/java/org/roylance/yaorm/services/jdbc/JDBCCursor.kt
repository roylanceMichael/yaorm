package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.lang.reflect.Method
import java.sql.ResultSet
import java.util.*

public class JDBCCursor<T> (
        private val classModel: Class<T>,
        private val resultSet: ResultSet) : ICursor<T> {

    private val cachedGetMethods: HashMap<String, Method> = HashMap()
    private val columnNamesFromResultSet: HashSet<String> = HashSet()

    private val typeToAction = object: HashMap<String, (label: String, resultSet: ResultSet) -> Any>() {
        init {
            put(
                    CommonSqlDataTypeUtilities.JavaObjectName,
                    { label, resultSet ->
                        val foundObject = resultSet.getObject(label)

                        if(foundObject is Int) {
                            foundObject
                        }
                        else if (foundObject is Long) {
                            foundObject
                        }
                        else if (foundObject is Double) {
                            foundObject
                        }
                        else {
                            foundObject as String
                        }
                    })
            put(
                    CommonSqlDataTypeUtilities.JavaAlt1DoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaAlt1IntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaAlt1LongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaAlt1BooleanName,
                    { label, resultSet -> resultSet.getBoolean(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaAltDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaAltLongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaAltIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaByteName,
                    { label, resultSet -> resultSet.getBlob(label) })
            put(
                    CommonSqlDataTypeUtilities.JavaBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(
                    CommonSqlDataTypeUtilities.JavaAltBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(
                    CommonSqlDataTypeUtilities.JavaAlt1BooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(
                    CommonSqlDataTypeUtilities.JavaLongName,
                    { label, resultSet -> resultSet.getLong(label) })
        }
    }

    override fun moveNext(): Boolean {
        return this.resultSet.next()
    }

    override fun <K, T: IEntity<K>> getRecord(): T {
        val newInstance = this.classModel.newInstance()!! as T

        if (this.columnNamesFromResultSet.isEmpty()) {
            val totalColumns = this.resultSet.metaData.columnCount

            var iter = 1
            while (iter <= totalColumns) {
                val lowercaseName = this.resultSet.metaData.getColumnName(iter).toLowerCase()
                this.columnNamesFromResultSet.add(lowercaseName)
                iter++
            }
        }

        if (this.cachedGetMethods.isEmpty()) {
            this.classModel
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) }
                    .forEach {
                        val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.Get.length))

                        val lowercaseName = actualName.toLowerCase()

                        if (this.columnNamesFromResultSet.contains(lowercaseName)) {
                            this.cachedGetMethods.put(actualName, it)
                        }
                    }
        }

        // set all the properties that we can
        classModel
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                .forEach {
                    val actualName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(CommonSqlDataTypeUtilities.Set.length))

                    if (this.cachedGetMethods.containsKey(actualName)) {
                        val javaType = this.cachedGetMethods[actualName]!!
                                .returnType
                                .simpleName

                        if (this.typeToAction.containsKey(javaType)) {
                            val newValue = this
                                    .typeToAction[javaType]!!(actualName, this.resultSet)

                            it.invoke(newInstance, newValue)
                        }
                    }
                }

        return newInstance
    }
}