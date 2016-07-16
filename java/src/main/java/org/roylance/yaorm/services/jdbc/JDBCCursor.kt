package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.services.entity.ICursor
import org.roylance.yaorm.utilities.YaormUtils
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
            put(YaormUtils.JavaAlt1DoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(YaormUtils.JavaAlt1IntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(YaormUtils.JavaAlt1LongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(YaormUtils.JavaAlt1BooleanName,
                    { label, resultSet -> resultSet.getBoolean(label) })
            put(YaormUtils.JavaAltDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(YaormUtils.JavaAltLongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(YaormUtils.JavaAltIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(YaormUtils.JavaStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(YaormUtils.JavaFullyQualifiedStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(YaormUtils.JavaIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(YaormUtils.JavaDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(YaormUtils.JavaByteName,
                    { label, resultSet -> resultSet.getBlob(label) })
            put(YaormUtils.JavaBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(YaormUtils.JavaAltBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(YaormUtils.JavaAlt1BooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(YaormUtils.JavaLongName,
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
                val lowercaseName = YaormUtils.getLastWord(this.resultSet
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
                .filter { it.name.startsWith(YaormUtils.Get) }
                .forEach {
                    val actualName = YaormUtils.lowercaseFirstChar(
                            it.name.substring(YaormUtils.Get.length))

                    val lowercaseName = actualName.toLowerCase()

                    if (this.columnNamesFromResultSet.contains(lowercaseName) &&
                        this.typeToAction.containsKey(it.returnType.simpleName)) {
                        this.cachedGetMethods.put(actualName, it)
                    }
                    else if (this.columnNamesFromResultSet.contains(lowercaseName) &&
                            EntityUtils.doesClassHaveAMethodGetId(it.returnType)) {

                        val foundIdGetter = it.returnType
                            .methods
                            .first { "${YaormUtils.Get}Id".equals(it.name) }

                        val foundIdSetter = it.returnType
                            .methods
                            .first { "${YaormUtils.Set}Id".equals(it.name) }

                        this.cachedGetMethods.put(actualName, it)

                        this.foreignGetMethods[actualName] = foundIdGetter
                        this.foreignSetMethods[actualName] = foundIdSetter
                    }
                }
        }

        // set all the properties that we can
        classModel
            .methods
            .filter { it.name.startsWith(YaormUtils.Set) }
            .forEach {
                val actualName = YaormUtils.lowercaseFirstChar(
                        it.name.substring(YaormUtils.Set.length))

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