package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.Tuple
import java.lang.reflect.Method

object EntityUtils {

    private val IdName = "Id"
    private val NumberOfTotalFieldsWithId = 4

    fun <K, T : IEntity<K>> doesEntityHaveForeignObject(classModel: Class<T>):Boolean {
        return classModel
            .methods
            .filter {
                it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                    it.returnType is IEntity<*>
            }
        .any()
    }

    fun getEntityTuple(getMethod: Method, typeDict: Map<String, String>):Tuple<String>? {
        val foundIds = getMethod
                .returnType
                .methods
                .filter {
                    it.name.equals(CommonSqlDataTypeUtilities.Get + IdName) ||
                    it.name.equals(CommonSqlDataTypeUtilities.Set + IdName)
                }

        if (foundIds.size == NumberOfTotalFieldsWithId) {
            val filteredId = foundIds
                    .firstOrNull {
                        typeDict.containsKey(it.returnType.name)
                    }

            if (filteredId != null) {
                val javaColumnName = getMethod
                        .name
                        .substring(CommonSqlDataTypeUtilities.GetSetLength)
                val sqlColumnName = CommonSqlDataTypeUtilities
                        .lowercaseFirstChar(javaColumnName)
                val dataType = typeDict[filteredId.returnType.name]
                return Tuple(sqlColumnName, javaColumnName, dataType!!, true)
            }
        }
        return null
    }
}
