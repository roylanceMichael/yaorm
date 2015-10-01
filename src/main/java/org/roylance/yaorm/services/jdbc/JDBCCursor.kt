package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.services.ICursor
import org.roylance.yaorm.utilities.SqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlUtilities
import java.lang.reflect.Method
import java.sql.ResultSet
import java.util.*

public class JDBCCursor<T> (
        private val classModel: Class<T>,
        private val resultSet: ResultSet) : ICursor<T> {

    private val cachedGetMethods: HashMap<String, Method> = HashMap()

    private val typeToAction = object: HashMap<String, (label: String, resultSet: ResultSet) -> Any>() {
        init {
            put(
                    SqlUtilities.JavaStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(
                    SqlUtilities.JavaFullyQualifiedStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(
                    SqlUtilities.JavaIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(
                    SqlUtilities.JavaDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(
                    SqlUtilities.JavaByteName,
                    { label, resultSet -> resultSet.getBlob(label) })
            put(
                    SqlUtilities.JavaBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(
                    SqlUtilities.JavaLongName,
                    { label, resultSet -> resultSet.getLong(label)})
        }
    }

    override fun moveNext(): Boolean {
        return this.resultSet.next()
    }

    override fun getRecord(): T {
        val newInstance: T = this.classModel.newInstance()

        if (cachedGetMethods.isEmpty()) {
            this.classModel
                    .methods
                    .filter { it.name.startsWith(SqlUtilities.Get) }
                    .forEach {
                        val actualName = SqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(SqlUtilities.Get.length()))
                        this.cachedGetMethods.put(actualName, it)
                    }
        }

        // set all the properties that we can
        classModel
                .methods
                .filter { it.name.startsWith(SqlUtilities.Set) }
                .forEach {
                    val actualName = SqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(SqlUtilities.Set.length()))

                    if (this.cachedGetMethods.containsKey(actualName) &&
                            this.resultSet.findColumn(actualName) >= 0) {
                        val javaType = this.cachedGetMethods
                                .get(actualName)!!
                                .returnType
                                .simpleName

                        if (this.typeToAction.containsKey(javaType)) {
                            val newValue = this
                                    .typeToAction
                                    .get(javaType)!!(actualName, this.resultSet)

                            it.invoke(newInstance, newValue)
                        }
                    }
                }

        return newInstance
    }
}