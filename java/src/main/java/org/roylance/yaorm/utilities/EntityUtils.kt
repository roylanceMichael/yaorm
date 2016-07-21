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
                val propertyHolder = YaormUtils.buildColumn(result, it.getMethod.returnType, it.propertyName)
                returnRecord.addColumns(propertyHolder)
            }

        return returnRecord.build()
    }

    fun <T> getDefinitionProto(classType: Class<T>): YaormModel.TableDefinition {
        val propertyNames = classType
                .methods
                .filter { it.name.startsWith(YaormUtils.Set) }
                .map { it.name.substring(YaormUtils.GetSetLength) }

        val definition = YaormModel.TableDefinition.newBuilder().setName(classType.simpleName)
        var i = 0
        classType
                .methods
                .filter { it.name.startsWith(YaormUtils.Get) &&
                        propertyNames.contains(it.name.substring(YaormUtils.GetSetLength)) &&
                        !YaormUtils.JavaObjectName.equals(it.returnType.name) }
                .sortedBy { it.name }
                .map {
                    val name = YaormUtils.lowercaseFirstChar(
                            it.name.substring(YaormUtils.GetSetLength))

                    if (YaormUtils.JavaToProtoMap.containsKey(it.returnType)) {
                        val property = YaormModel.ColumnDefinition.newBuilder()
                            .setType(YaormUtils.JavaToProtoMap[it.returnType])
                            .setName(name)
                            .setIsKey(name == YaormUtils.IdName)
                            .setOrder(i)
                        definition.addColumnDefinitions(property)
                    }
                    else {
                        val property = YaormModel.ColumnDefinition.newBuilder()
                                .setType(YaormModel.ProtobufType.STRING)
                                .setName(name)
                                .setIsKey(name == YaormUtils.IdName)
                                .setOrder(i)
                        definition.addColumnDefinitions(property)
                    }
                    i++
                }

        return definition.build()
    }

    fun getProperties(item:Any):List<EntityDefinitionModel<*>> {
        val allGetMethods = HashMap<String, Method>()
        item
                .javaClass
                .methods
                .filter { it.name.startsWith(YaormUtils.Get) }
                .map { it }
                .forEach {
                    allGetMethods[it.name.substring(YaormUtils.GetSetLength)] = it
                }

        return item
            .javaClass
            .methods
            .filter {
                it.name.startsWith(YaormUtils.Set) &&
                    allGetMethods.containsKey(it.name.substring(YaormUtils.GetSetLength))
            }
            .map {
                val commonPropertyName = it.name.substring(YaormUtils.GetSetLength)
                val getter = allGetMethods[commonPropertyName]!!
                val setter = it
                val propertyName = YaormUtils.lowercaseFirstChar(commonPropertyName)
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
            .any { it.name.equals(YaormUtils.Get + IdName) }
    }

    fun isClassAListAndDoesTypeHaveGetId(classModel: Class<*>):Boolean {
        return EntityCollectionName.equals(classModel.name)
    }

    fun getAllForeignObjects(entityDefinition: Class<*>): List<EntityDefinitionModel<*>> {
        val getMethodMap = HashMap<String, Method>()
        entityDefinition
            .methods
            .filter {
                it.name.startsWith(YaormUtils.Get) &&
                this.doesClassHaveAMethodGetId(it.returnType) ||
                this.isClassAListAndDoesTypeHaveGetId(it.returnType)
            }
            .forEach {
                val propertyName = it.name.substring(YaormUtils.GetSetLength)
                getMethodMap[propertyName] = it
            }

        val singleEntities = entityDefinition
            .methods
            .filter {
                it.name.startsWith(YaormUtils.Set) &&
                getMethodMap.containsKey(it.name.substring(YaormUtils.GetSetLength))
            }
            .map {
                val propertyName = it.name.substring(YaormUtils.GetSetLength)

                if (this.isClassAListAndDoesTypeHaveGetId(
                                getMethodMap[propertyName]!!.returnType)) {

                    EntityDefinitionModel(
                            propertyName,
                            YaormUtils.lowercaseFirstChar(propertyName),
                            getMethodMap[propertyName]!!,
                            it,
                            getMethodMap[propertyName]!!.returnType,
                            EntityDefinitionModel.List)
                }
                else {
                    EntityDefinitionModel(
                            propertyName,
                            YaormUtils.lowercaseFirstChar(propertyName),
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
                .setDefinition(YaormModel.ColumnDefinition.newBuilder()
                        .setType(YaormModel.ProtobufType.STRING)
                        .setName(IdNameLowercase).setIsKey(true))
                .build()

        val whereClause = YaormModel.WhereClause.newBuilder()
                .setNameAndProperty(propertyHolder)
                .setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS)
                .build()

        return whereClause
    }
}
