package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.services.entity.ICursor
import org.roylance.yaorm.utilities.CommonUtils
import org.roylance.yaorm.utilities.EntityUtils
import java.lang.reflect.Method
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class JDBCCursor<T> (
        private val classModel: Class<T>,
        private val resultSet: ResultSet,
        private val preparedStatement: Statement) : ICursor<T> {

    private val cachedGetMethods: HashMap<String, Method> = HashMap()
    private val foreignGetMethods: HashMap<String, Method> = HashMap()
    private val foreignSetMethods: HashMap<String, Method> = HashMap()
    private val columnNamesFromResultSet: HashSet<String> = HashSet()

    private val typeToAction = object: HashMap<String, (label: String, resultSet: ResultSet) -> Any?>() {
        init {
            put(CommonUtils.JavaAlt1DoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(CommonUtils.JavaAlt1IntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(CommonUtils.JavaAlt1LongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(CommonUtils.JavaAlt1BooleanName,
                    { label, resultSet -> resultSet.getBoolean(label) })
            put(CommonUtils.JavaAltDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(CommonUtils.JavaAltLongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(CommonUtils.JavaAltIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(CommonUtils.JavaStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(CommonUtils.JavaFullyQualifiedStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(CommonUtils.JavaIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(CommonUtils.JavaDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(CommonUtils.JavaByteName,
                    { label, resultSet -> resultSet.getBlob(label) })
            put(CommonUtils.JavaBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(CommonUtils.JavaAltBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(CommonUtils.JavaAlt1BooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(CommonUtils.JavaLongName,
                    { label, resultSet -> resultSet.getLong(label) })
        }
    }

    fun moveNext(): Boolean {
        return this.resultSet.next()
    }

    fun <T: IEntity> getRecord(): T {
        val newInstance = this.classModel.newInstance()

        if (this.columnNamesFromResultSet.isEmpty()) {
            val totalColumns = this.resultSet.metaData.columnCount

            var iter = 1
            while (iter <= totalColumns) {
                // let's make sure we get the last one
                val lowercaseName = CommonUtils.getLastWord(this.resultSet
                        .metaData
                        .getColumnName(iter))
                        .toLowerCase()

                this.columnNamesFromResultSet.add(lowercaseName)
                iter++
            }
        }

        if (this.cachedGetMethods.isEmpty()) {
            this.classModel
                .methods
                .filter { it.name.startsWith(CommonUtils.Get) }
                .forEach {
                    val actualName = CommonUtils.lowercaseFirstChar(
                            it.name.substring(CommonUtils.Get.length))

                    val lowercaseName = actualName.toLowerCase()

                    if (this.columnNamesFromResultSet.contains(lowercaseName) &&
                        this.typeToAction.containsKey(it.returnType.simpleName)) {
                        this.cachedGetMethods.put(actualName, it)
                    }
                    else if (this.columnNamesFromResultSet.contains(lowercaseName) &&
                            EntityUtils.doesClassHaveAMethodGetId(it.returnType)) {

                        val foundIdGetter = it.returnType
                            .methods
                            .first { "${CommonUtils.Get}Id".equals(it.name) }

                        val foundIdSetter = it.returnType
                            .methods
                            .first { "${CommonUtils.Set}Id".equals(it.name) }

                        this.cachedGetMethods.put(actualName, it)

                        this.foreignGetMethods[actualName] = foundIdGetter
                        this.foreignSetMethods[actualName] = foundIdSetter
                    }
                }
        }

        // set all the properties that we can
        classModel
            .methods
            .filter { it.name.startsWith(CommonUtils.Set) }
            .forEach {
                val actualName = CommonUtils.lowercaseFirstChar(
                        it.name.substring(CommonUtils.Set.length))

                if (!this.cachedGetMethods.containsKey(actualName)) {
                    return@forEach
                }

                val javaType = this.cachedGetMethods[actualName]!!
                        .returnType
                        .simpleName

                if (this.cachedGetMethods.containsKey(actualName) &&
                        this.typeToAction.containsKey(javaType)) {
                    val newValue = this
                            .typeToAction[javaType]!!(actualName, this.resultSet)

                    if (newValue != null) {
                        it.invoke(newInstance, newValue)
                    }
                }
                else if (this.cachedGetMethods.containsKey(actualName) &&
                        this.foreignGetMethods.containsKey(actualName) &&
                        this.foreignSetMethods.containsKey(actualName)) {

                    val newForeignInstance = this.cachedGetMethods[actualName]!!
                            .returnType
                            .newInstance()

                    val foreignIdJavaType = this.foreignGetMethods[actualName]!!
                            .returnType.simpleName

                    if (!this.typeToAction.containsKey(foreignIdJavaType)) {
                        return@forEach
                    }

                    val newValue = this.typeToAction[foreignIdJavaType]!!(actualName, this.resultSet)

                    if (newValue != null) {
                        this.foreignSetMethods[actualName]!!.invoke(newForeignInstance, newValue)
                        it.invoke(newInstance, newForeignInstance)
                    }
                }
            }

        return newInstance as T
    }

    override fun <T : IEntity> getRecords(): List<T> {
        val returnItems = ArrayList<T>()
        try {
            while (this.moveNext()) {
                returnItems.add(this.getRecord())
            }
            return returnItems
        }
        finally {
            // mysql is having problems closing...
             this.preparedStatement.close()
        }
    }
}