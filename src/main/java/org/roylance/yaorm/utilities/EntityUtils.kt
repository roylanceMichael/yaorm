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
    private val EntityCollectionName = "org.roylance.yaorm.models.EntityCollection"

    fun areObjectsDifferent(firstItem:Any?, secondItem:Any?):Boolean {
        if (firstItem == null && secondItem != null) {
            return true
        }

        if (firstItem != null && secondItem == null) {
            return true
        }

        if (firstItem == null && secondItem == null) {
            return false
        }

        // get all properties
        if (firstItem!!.javaClass != secondItem!!.javaClass) {
            return true
        }

        return false
    }

    fun getProperties(item:Any):List<EntityDefinitionModel<*>> {
        val totalNames = HashMap<String, Int>()

        item
                .javaClass
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) ||
                        it.name.startsWith(CommonSqlDataTypeUtilities.Set)
                }
                .map { it.name.substring(CommonSqlDataTypeUtilities.GetSetLength) }
                .forEach {
                    if (totalNames.containsKey(it)) {
                        totalNames[it] = totalNames[it]!! + 1
                    }
                    else {
                        totalNames[it] = 1
                    }
                }

        return totalNames
                .keys
                .filter { totalNames.containsKey(it) && totalNames[it]!! == 2 }
                .map {
                    val getter = item.javaClass.methods
                            .first { "${CommonSqlDataTypeUtilities.Get}$it".equals(it.name) }
                    val setter = item.javaClass.methods
                            .first { "${CommonSqlDataTypeUtilities.Set}$it".equals(it.name) }
                    val commonPropertyName = it
                    val propertyName = CommonSqlDataTypeUtilities.lowercaseFirstChar(it)
                    val entityClass = getter.returnType
                    var entityType = EntityDefinitionModel.Single
                    if (EntityCollectionName.equals(getter.returnType.name)) {
                        entityType = EntityDefinitionModel.List
                    }

                    EntityDefinitionModel(
                            commonPropertyName,
                            propertyName,
                            setter,
                            getter,
                            entityClass,
                            entityType)
                }
    }

    fun doesClassHaveAMethodGetId(classModel: Class<*>):Boolean {
        return classModel
            .methods
            .any { it.name.equals(CommonSqlDataTypeUtilities.Get + IdName) }
    }

    fun isClassAListAndDoesTypeHaveGetId(classModel: Class<*>):Boolean {
        return EntityCollectionName.equals(classModel.name)
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
                this.doesClassHaveAMethodGetId(it.returnType) ||
                this.isClassAListAndDoesTypeHaveGetId(it.returnType)
            }
            .forEach {
                val propertyName = it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)
                getMethodMap[propertyName] = it
            }

        val singleEntities = entityDefinition
            .methods
            .filter {
                it.name.startsWith(CommonSqlDataTypeUtilities.Set) &&
                getMethodMap.containsKey(
                        it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
            }
            .map {
                val propertyName = it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)

                if (this
                        .isClassAListAndDoesTypeHaveGetId(
                                getMethodMap[propertyName]!!
                                        .returnType)) {
                    EntityDefinitionModel(
                            propertyName,
                            CommonSqlDataTypeUtilities.lowercaseFirstChar(propertyName),
                            getMethodMap[propertyName]!!,
                            it,
                            getMethodMap[propertyName]!!.returnType,
                            EntityDefinitionModel.List)
                }
                else {
                    EntityDefinitionModel(
                            propertyName,
                            CommonSqlDataTypeUtilities.lowercaseFirstChar(propertyName),
                            getMethodMap[propertyName]!!,
                            it,
                            getMethodMap[propertyName]!!.returnType,
                            EntityDefinitionModel.Single)
                }
            }

        return singleEntities
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
