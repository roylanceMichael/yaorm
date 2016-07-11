package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.entity.EntityDefinitionModel
import java.lang.reflect.Method
import java.util.*

object EntityUtils {
    const private val IdNameLowercase = "id"
    const private val IdName = "Id"
    const private val EntityCollectionName = "org.roylance.yaorm.models.EntityCollection"

    fun getRecordsFromObjects(
            entityDefinitions: List<EntityDefinitionModel<*>>,
            objects: List<Any>): YaormModel.Records {
        val records = YaormModel.Records.newBuilder()
        objects.forEach { records.addRecords(getRecordFromObject(entityDefinitions, it)) }
        return records.build()
    }

    fun getRecordFromObject(entityDefinitions: List<EntityDefinitionModel<*>>, item: Any) : YaormModel.Record {
        val returnRecord = YaormModel.Record.newBuilder()
        entityDefinitions
            .forEach {
                val result = it.getMethod.invoke(item)
                val propertyHolder = CommonUtils.buildColumn(result, it.getMethod.returnType, it.propertyName)
                returnRecord.mutableColumns[it.propertyName] = (propertyHolder)
            }

        return returnRecord.build()
    }

    fun <T> getDefinitionProto(classType: Class<T>): YaormModel.TableDefinition {
        val propertyNames = classType
                .methods
                .filter { it.name.startsWith(CommonUtils.Set) }
                .map { it.name.substring(CommonUtils.GetSetLength) }
                .toHashSet()

        val definition = YaormModel.TableDefinition.newBuilder().setName(classType.simpleName)
        classType
                .methods
                .filter { it.name.startsWith(CommonUtils.Get) &&
                        propertyNames.contains(it.name.substring(CommonUtils.GetSetLength)) &&
                        !CommonUtils.JavaObjectName.equals(it.returnType.name) }
                .map {
                    val name = CommonUtils.lowercaseFirstChar(
                            it.name.substring(CommonUtils.GetSetLength))

                    if (CommonUtils.JavaToProtoMap.containsKey(it.returnType)) {
                        val property = YaormModel.ColumnDefinition.newBuilder()
                            .setType(CommonUtils.JavaToProtoMap[it.returnType])
                            .setName(name)
                            .setIsKey(name == CommonUtils.IdName)
                        definition.mutableColumnDefinitions.put(it.name, (property).build())
                    }
                    else {
                        val property = YaormModel.ColumnDefinition.newBuilder()
                                .setType(YaormModel.ProtobufType.STRING)
                                .setName(name)
                                .setIsKey(name == CommonUtils.IdName)
                        definition.mutableColumnDefinitions.put(it.name, (property).build())
                    }
                }

        return definition.build()
    }

    fun getProperties(item:Any):List<EntityDefinitionModel<*>> {
        val allGetMethods = HashMap<String, Method>()
        item
                .javaClass
                .methods
                .filter { it.name.startsWith(CommonUtils.Get) }
                .map { it }
                .forEach {
                    allGetMethods[it.name.substring(CommonUtils.GetSetLength)] = it
                }

        return item
            .javaClass
            .methods
            .filter {
                it.name.startsWith(CommonUtils.Set) &&
                    allGetMethods.containsKey(it.name.substring(CommonUtils.GetSetLength))
            }
            .map {
                val commonPropertyName = it.name.substring(CommonUtils.GetSetLength)
                val getter = allGetMethods[commonPropertyName]!!
                val setter = it
                val propertyName = CommonUtils.lowercaseFirstChar(commonPropertyName)
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
            .any { it.name.equals(CommonUtils.Get + IdName) }
    }

    fun isClassAListAndDoesTypeHaveGetId(classModel: Class<*>):Boolean {
        return EntityCollectionName.equals(classModel.name)
    }

    fun getAllForeignObjects(entityDefinition: Class<*>): List<EntityDefinitionModel<*>> {
        val getMethodMap = HashMap<String, Method>()
        entityDefinition
            .methods
            .filter {
                it.name.startsWith(CommonUtils.Get) &&
                this.doesClassHaveAMethodGetId(it.returnType) ||
                this.isClassAListAndDoesTypeHaveGetId(it.returnType)
            }
            .forEach {
                val propertyName = it.name.substring(CommonUtils.GetSetLength)
                getMethodMap[propertyName] = it
            }

        val singleEntities = entityDefinition
            .methods
            .filter {
                it.name.startsWith(CommonUtils.Set) &&
                getMethodMap.containsKey(it.name.substring(CommonUtils.GetSetLength))
            }
            .map {
                val propertyName = it.name.substring(CommonUtils.GetSetLength)

                if (this.isClassAListAndDoesTypeHaveGetId(
                                getMethodMap[propertyName]!!.returnType)) {

                    EntityDefinitionModel(
                            propertyName,
                            CommonUtils.lowercaseFirstChar(propertyName),
                            getMethodMap[propertyName]!!,
                            it,
                            getMethodMap[propertyName]!!.returnType,
                            EntityDefinitionModel.List)
                }
                else {
                    EntityDefinitionModel(
                            propertyName,
                            CommonUtils.lowercaseFirstChar(propertyName),
                            getMethodMap[propertyName]!!,
                            it,
                            getMethodMap[propertyName]!!.returnType,
                            EntityDefinitionModel.Single)
                }
            }

        return singleEntities
    }

    fun buildWhereClauseOnIdProto(entity:IEntity):YaormModel.WhereClause {
        val propertyHolder = YaormModel.Column.newBuilder()
                .setStringHolder(entity.id)
                .setDefinition(YaormModel.ColumnDefinition.newBuilder().setType(YaormModel.ProtobufType.STRING).setName(IdNameLowercase).setIsKey(true))
                .build()

        val whereClause = YaormModel.WhereClause.newBuilder()
                .setNameAndProperty(propertyHolder)
                .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                .build()

        return whereClause
    }
}