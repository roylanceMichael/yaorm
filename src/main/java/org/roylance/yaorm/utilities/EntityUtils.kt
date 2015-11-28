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

    fun getProperties(item:Any):List<EntityDefinitionModel<*>> {
        val allGetMethods = item
            .javaClass
            .methods
            .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) }
            .map { it }
            .toMapBy { it.name.substring(CommonSqlDataTypeUtilities.GetSetLength) }

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
                    System.out.println(getMethodMap[propertyName]!!.returnType.toGenericString())

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
