package org.roylance.yaorm.services.jdbc

import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.services.map.IMapCursor
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.CommonStringUtilities
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class JDBCMapCursor(
        private val definitionModel: DefinitionModel,
        private val resultSet: ResultSet,
        private val preparedStatement: Statement): IMapCursor {

    private val columnNamesFromResultSet: HashSet<String> = HashSet()

    private val typeToAction = object: HashMap<String, (label: String, resultSet: ResultSet) -> Any?>() {
        init {
            put(CommonSqlDataTypeUtilities.JavaAlt1DoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(CommonSqlDataTypeUtilities.JavaAlt1IntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(CommonSqlDataTypeUtilities.JavaAlt1LongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(CommonSqlDataTypeUtilities.JavaAlt1BooleanName,
                    { label, resultSet -> resultSet.getBoolean(label) })
            put(CommonSqlDataTypeUtilities.JavaAltDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(CommonSqlDataTypeUtilities.JavaAltLongName,
                    { label, resultSet -> resultSet.getLong(label) })
            put(CommonSqlDataTypeUtilities.JavaAltIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(CommonSqlDataTypeUtilities.JavaStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(CommonSqlDataTypeUtilities.JavaFullyQualifiedStringName,
                    { label, resultSet -> resultSet.getString(label) })
            put(CommonSqlDataTypeUtilities.JavaIntegerName,
                    { label, resultSet -> resultSet.getInt(label) })
            put(CommonSqlDataTypeUtilities.JavaDoubleName,
                    { label, resultSet -> resultSet.getDouble(label) })
            put(CommonSqlDataTypeUtilities.JavaByteName,
                    { label, resultSet -> resultSet.getBlob(label) })
            put(CommonSqlDataTypeUtilities.JavaBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(CommonSqlDataTypeUtilities.JavaAltBooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(CommonSqlDataTypeUtilities.JavaAlt1BooleanName, {
                label, resultSet -> resultSet.getInt(label) == 1 })
            put(CommonSqlDataTypeUtilities.JavaLongName,
                    { label, resultSet -> resultSet.getLong(label) })
        }
    }

    fun moveNext(): Boolean {
        return this.resultSet.next()
    }

    fun getRecord(): Map<String, Any> {
        val newInstance = HashMap<String, Any>()

        if (this.columnNamesFromResultSet.isEmpty()) {
            val totalColumns = this.resultSet.metaData.columnCount

            var iter = 1
            while (iter <= totalColumns) {
                // let's make sure we get the last one
                val lowercaseName = CommonStringUtilities.getLastWord(this.resultSet
                        .metaData
                        .getColumnName(iter))
                        .toLowerCase()

                this.columnNamesFromResultSet.add(lowercaseName)
                iter++
            }
        }

        // set all the properties that we can
        this.definitionModel
                .properties
                .forEach {
                    if (this.typeToAction.containsKey(it.type)) {
                        val newValue = this.typeToAction[it.type]!!(it.name, this.resultSet)
                        if (newValue != null) {
                            newInstance[it.name] = newValue
                        }
                    }
                }

        return newInstance
    }

    override fun getRecords(): List<Map<String, Any>> {
        val returnItems = ArrayList<Map<String, Any>>()
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
