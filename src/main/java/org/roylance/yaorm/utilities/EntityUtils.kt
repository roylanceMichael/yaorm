package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.entity.EntityDefinitionModel
import java.lang.reflect.Method
import java.util.*

object EntityUtils {

    private val IdNameLowercase = "id"
    private val IdName = "Id"
    private val NumberOfTotalFieldsWithId = 4

    fun doesClassHaveAMethodGetId(classModel: Class<*>):Boolean {
        return classModel
            .methods
            .any { it.name.equals(CommonSqlDataTypeUtilities.Get + IdName) }
    }

    fun <K, T : IEntity<K>> doesEntityHaveForeignObject(classModel: Class<T>):Boolean {
        return classModel
            .methods
            .filter {
                it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                    it.returnType is IEntity<*>
            }
        .any()
    }

    fun getEntityTuple(getMethod: Method, typeDict: Map<String, String>): ColumnNameTuple<String>? {
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
                return ColumnNameTuple(sqlColumnName, javaColumnName, dataType!!, true)
            }
        }
        return null
    }

    fun getAllForeignObjects(entityDefinition: Class<*>): List<EntityDefinitionModel<*>> {
        val getMethodMap = HashMap<String, Method>()
        entityDefinition
            .methods
            .filter {
                it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                this.doesClassHaveAMethodGetId(it.returnType) }
            .forEach {
                val propertyName = it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)
                getMethodMap[propertyName] = it
            }

        return entityDefinition
            .methods
            .filter {
                it.name.startsWith(CommonSqlDataTypeUtilities.Set) &&
                getMethodMap.containsKey(
                        it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
            }
            .map {
                val propertyName = it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)
                EntityDefinitionModel(
                    CommonSqlDataTypeUtilities.lowercaseFirstChar(propertyName),
                    getMethodMap[propertyName]!!,
                    it,
                    getMethodMap[propertyName]!!.returnType)
            }
    }

    fun buildWhereClauseOnId(entity:IEntity<*>):WhereClauseItem {
        if (entity.id == null) {
            return WhereClauseItem(
                    IdNameLowercase,
                    WhereClauseItem.Equals,
                    CommonSqlDataTypeUtilities.Null)
        }

        return WhereClauseItem(
            IdNameLowercase,
            WhereClauseItem.Equals,
            entity.id!!)
    }
}
