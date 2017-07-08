@file:Suppress("UNCHECKED_CAST")

package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.models.CacheStore
import org.roylance.yaorm.models.entity.CachingObject
import org.roylance.yaorm.services.IEntityService
import java.util.*
import kotlin.collections.HashMap

internal class GetProtoObjects(
        private val entityService: IEntityService,
        private val definitions: MutableMap<String, YaormModel.TableDefinitionGraphs>,
        private val customIndexes: MutableMap<String, YaormModel.Index>) {
    private val cacheStore = CacheStore()

    internal fun <T: Message> build(message: T, entityIds: List<String>): List<T> {
        if (entityIds.isEmpty()) {
            return ArrayList()
        }

        var allFound = true
        entityIds.forEach { entityId ->
            if(!cacheStore.seenObject(message, entityId)) {
                allFound = false
            }
        }

        if (allFound) {
            return entityIds.map {
                cacheStore.getObject(message, it).build() as T
            }
        }

        val messageBuilder = message.toBuilder()
        val keysToReconcile = HashMap<String, HashSet<String>>()
        val messagesToReconcile = HashMap<String, Message>()

        // type - mainId - field name
        val normalReconciliation = HashMap<String, HashMap<String, HashMap<String, CachingObject>>>()
        val repeatedReconciliation = HashMap<String, HashMap<String, HashMap<String, CachingObject>>>()

        if (!definitions.containsKey(message.descriptorForType.name)) {
            this.definitions[message.descriptorForType.name] =
                    ProtobufUtils.buildDefinitionGraph(message.descriptorForType,
                            this.customIndexes)
        }

        val tableDefinitionGraph = this.definitions[message.descriptorForType.name]!!
        val whereClause = YaormModel.WhereClause.newBuilder()
                .setNameAndProperty(YaormModel.Column.newBuilder().setDefinition(YaormUtils.buildIdColumnDefinition()))
                .addAllInItems(entityIds)
                .setOperatorType(YaormModel.WhereClause.OperatorType.IN)
                .build()

        val childMessageHandler = object: IChildMessageHandler {
            override fun handle(fieldKey: Descriptors.FieldDescriptor,
                                idColumn: YaormModel.Column,
                                childBuilder: Message.Builder) {
                if (idColumn.stringHolder.isNotEmpty()) {
                    val mainId = ProtobufUtils.getIdFromMessage(childBuilder)
                    val childObject = messageBuilder.newBuilderForField (fieldKey).build()

                    if (!keysToReconcile.containsKey(childObject.descriptorForType.name)) {
                        keysToReconcile[childObject.descriptorForType.name] = HashSet()
                    }
                    if (!messagesToReconcile.containsKey(childObject.descriptorForType.name)) {
                        messagesToReconcile[childObject.descriptorForType.name] = childObject
                    }

                    keysToReconcile[childObject.descriptorForType.name]!!.add(idColumn.stringHolder)

                    if (!normalReconciliation.containsKey(childObject.descriptorForType.name)) {
                        normalReconciliation[childObject.descriptorForType.name] = HashMap()
                    }

                    if (!normalReconciliation[childObject.descriptorForType.name]!!.containsKey(mainId)) {
                        normalReconciliation[childObject.descriptorForType.name]!![mainId] = HashMap()
                    }

                    normalReconciliation[childObject.descriptorForType.name]!![mainId]!![fieldKey.name] =
                            CachingObject(childObject, fieldKey, mainId, listOf(idColumn.stringHolder).toMutableList())
                }
            }
        }

        val records = this.entityService.where(whereClause, tableDefinitionGraph.mainTableDefinition)
        val actuallyFoundIds = ArrayList<String>()

        records.recordsList.forEach { record ->
            val newBuilder = message.newBuilderForType()
            ConvertRecordsToProtobuf.build(newBuilder, record, childMessageHandler)
            val id = YaormUtils.getIdColumn(record.columnsList) ?: return@forEach

            cacheStore.saveObject(newBuilder, id.stringHolder)
            actuallyFoundIds.add(id.stringHolder)
        }

        val mainEnumColumnName = message.descriptorForType.name
        val groupWhereClause = YaormModel.WhereClause.newBuilder()
                .setNameAndProperty(YaormModel.Column.newBuilder()
                        .setDefinition(
                                YaormModel.ColumnDefinition.newBuilder()
                                        .setName(mainEnumColumnName)
                                        .setType(YaormModel.ProtobufType.STRING)))
                .addAllInItems(entityIds)
                .setOperatorType(YaormModel.WhereClause.OperatorType.IN)
                .build()


        // let's set all the enums, this should be n queries max, where n is the number of enums on a table
        // handle repeated enums for all
        message.descriptorForType
                .fields
                .filter { it.type.name == ProtobufUtils.ProtoEnumType && it.isRepeated }
                .forEach { fieldKey ->
                    val definitionForLinkerTable = tableDefinitionGraph.tableDefinitionGraphsList
                            .firstOrNull { it.columnName == fieldKey.name && it.otherName == fieldKey.enumType.name }
                            ?: return@forEach

                    val foundRecords = entityService.where(groupWhereClause, definitionForLinkerTable.linkerTableTable)
                    foundRecords.recordsList.forEach { record ->
                        val nameColumn = record.columnsList.firstOrNull { fieldKey.enumType.name == it.definition.name }
                        val entityColumn = record.columnsList.firstOrNull { mainEnumColumnName == it.definition.name }
                        if (nameColumn != null && entityColumn != null) {
                            val actualBuilder = cacheStore.getObject(message, entityColumn.stringHolder)
                            val enumToAdd = fieldKey.enumType.findValueByName(nameColumn.stringHolder.toUpperCase())
                            actualBuilder.addRepeatedField(fieldKey, enumToAdd)
                        }
                    }
                }

        val mainMessageColumnName = ProtobufUtils.buildLinkerMessageMainTableColumnName(message.descriptorForType.name)

        val groupMessageWhereClause = YaormModel.WhereClause.newBuilder()
                .setNameAndProperty(YaormModel.Column.newBuilder()
                        .setDefinition(
                                YaormModel.ColumnDefinition.newBuilder()
                                        .setName(mainMessageColumnName)
                                        .setType(YaormModel.ProtobufType.STRING)))
                .addAllInItems(entityIds)
                .setOperatorType(YaormModel.WhereClause.OperatorType.IN)
                .build()

        // handle messages for all
        message.descriptorForType
            .fields
            .filter { it.type.name == ProtobufUtils.ProtoMessageType && it.isRepeated }
            .forEach { fieldKey ->
                val definitionForLinkerTable = tableDefinitionGraph.tableDefinitionGraphsList
                        .firstOrNull { it.columnName == fieldKey.name && it.otherName == fieldKey.messageType.name }
                        ?: return@forEach

                val foundRecords = entityService.where(groupMessageWhereClause, definitionForLinkerTable.linkerTableTable)
                val otherColumnName = ProtobufUtils.buildLinkerMessageOtherTableColumnName(fieldKey.messageType.name)
                val subType = messageBuilder.newBuilderForField(fieldKey).build()

                foundRecords.recordsList
                        .forEach { record ->
                            val nameColumn = record.columnsList.firstOrNull { otherColumnName == it.definition.name }
                            val entityColumn = record.columnsList.firstOrNull { mainMessageColumnName == it.definition.name }

                            if (nameColumn != null && nameColumn.stringHolder.isNotEmpty() && entityColumn != null) {
                                if (!keysToReconcile.containsKey(fieldKey.messageType.name)) {
                                    keysToReconcile[fieldKey.messageType.name] = HashSet()
                                }

                                if (!messagesToReconcile.containsKey(fieldKey.messageType.name)) {
                                    messagesToReconcile[fieldKey.messageType.name] = subType
                                }

                                keysToReconcile[fieldKey.messageType.name]!!.add(nameColumn.stringHolder)

                                if (!repeatedReconciliation.containsKey(fieldKey.messageType.name)) {
                                    repeatedReconciliation[fieldKey.messageType.name] = HashMap()
                                }

                                if (!repeatedReconciliation[fieldKey.messageType.name]!!.containsKey(entityColumn.stringHolder)) {
                                    repeatedReconciliation[fieldKey.messageType.name]!![entityColumn.stringHolder] = HashMap()
                                }

                                if (repeatedReconciliation[fieldKey.messageType.name]!![entityColumn.stringHolder]!!.containsKey(fieldKey.name)) {
                                    repeatedReconciliation[fieldKey.messageType.name]!![entityColumn.stringHolder]!![fieldKey.name]!!.id.add(nameColumn.stringHolder)
                                }
                                else {
                                    repeatedReconciliation[fieldKey.messageType.name]!![entityColumn.stringHolder]!![fieldKey.name] = CachingObject(subType,
                                            fieldKey,
                                            entityColumn.stringHolder,
                                            listOf(nameColumn.stringHolder).toMutableList())
                                }
                            }
                        }
            }

        keysToReconcile.keys.forEach { childType ->
            val childBuilder = messagesToReconcile[childType]!!
            this.build(childBuilder, keysToReconcile[childType]!!.toList())

            normalReconciliation.filter { it.key == childType }
                    .flatMap { it.value.values }
                    .flatMap { it.values }.forEach { cachingObject ->
                val mainObject = cacheStore.getObject(message, cachingObject.mainId)
                val childObject = cacheStore.getObject(childBuilder, cachingObject.id.first())

                mainObject.setField(cachingObject.fieldKey, childObject.build())
            }

            repeatedReconciliation.filter { it.key == childType }
                    .flatMap { it.value.values }
                    .flatMap { it.values }.forEach { cachingObject ->
                val mainObject = cacheStore.getObject(message, cachingObject.mainId)

                mainObject.clearField(cachingObject.fieldKey)
                cachingObject.id.forEach { id ->
                    val childObject = cacheStore.getObject(childBuilder, id)
                    mainObject.addRepeatedField(cachingObject.fieldKey, childObject.build())
                }
            }
        }

        return actuallyFoundIds.map {
            cacheStore.getObject(message, it).build() as T
        }
    }
}