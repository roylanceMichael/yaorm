package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.entity.EntityDefinitionModel
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import java.lang.reflect.Method
import java.util.*

object EntityUtils {

    const private val IdNameLowercase = "id"
    const private val IdName = "Id"
    const private val NumberOfTotalFieldsWithId = 4
    const private val EntityCollectionName = "org.roylance.yaorm.models.EntityCollection"

    fun getMapsFromObjects(
            entityDefinitions: List<EntityDefinitionModel<*>>,
            objects: List<Any>): List<Map<String, Any>> {
        return objects
            .map { currentObject ->
                this.getMapFromObject(entityDefinitions, currentObject)
            }
    }

    fun getMapFromObject(entityDefinitions: List<EntityDefinitionModel<*>>, item: Any) : Map<String, Any> {
        val returnMap = HashMap<String, Any>()

        entityDefinitions
            .forEach {
                returnMap[it.propertyName] = it.getMethod.invoke(item)
            }

        return returnMap
    }

    fun <T> getDefinition(classType: Class<T>): DefinitionModel {
        val propertyNames = classType
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                .map { it.name.substring(CommonSqlDataTypeUtilities.GetSetLength) }
                .toHashSet()

        val propertyDefinitions = classType
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                        propertyNames.contains(it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)) &&
                        !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.returnType.name) }
                .map {
                    val name = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                            it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
                    val javaType = it.returnType.name

                    if (javaType.equals(EntityCollectionName)) {
                        PropertyDefinitionModel(name, javaType, true)
                    }
                    else {
                        PropertyDefinitionModel(name, javaType)
                    }
                }

        return DefinitionModel(classType.simpleName,
                            propertyDefinitions,
                            null)
    }

    fun getProperties(item:Any):List<EntityDefinitionModel<*>> {
        val allGetMethods = HashMap<String, Method>()
        item
                .javaClass
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) }
                .map { it }
                .forEach {
                    allGetMethods[it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)] = it
                }

        return item
            .javaClass
            .methods
            .filter {
                it.name.startsWith(CommonSqlDataTypeUtilities.Set) &&
                    allGetMethods.containsKey(it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
            }
            .map {
                val commonPropertyName = it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)
                val getter = allGetMethods[commonPropertyName]!!
                val setter = it
                val propertyName = CommonSqlDataTypeUtilities.lowercaseFirstChar(commonPropertyName)
                var entityType = EntityDefinitionModel.Single
                if (EntityCollectionName.equals(getter.returnType.name)) {
                    entityType = EntityDefinitionModel.List
                }
                EntityDefinitionModel(
                        commonPropertyName,
                        propertyName,
                        getter,
                        setter,
                        item.javaClass,
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
                                getMethodMap[propertyName]!!.returnType)) {

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

    fun buildWhereClauseOnId(entity:IEntity):WhereClauseItem {
        return WhereClauseItem(
            IdNameLowercase,
            WhereClauseItem.Equals,
                entity.id)
    }
}
