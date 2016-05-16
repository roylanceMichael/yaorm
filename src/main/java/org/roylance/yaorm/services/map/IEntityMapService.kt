package org.roylance.yaorm.services.map

import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.IndexModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel

interface IEntityMapService {
    val indexDefinition: IndexModel?

    fun createTable(definition: DefinitionModel): Boolean
    fun dropTable(definition: DefinitionModel): Boolean

    fun createIndex(indexModel: IndexModel, definition: DefinitionModel): Boolean
    fun dropIndex(indexModel: IndexModel, definition: DefinitionModel): Boolean

    fun createColumn(propertyDefinitionModel: PropertyDefinitionModel, definition: DefinitionModel): Boolean
    fun dropColumn(propertyDefinitionModel: PropertyDefinitionModel, definition: DefinitionModel): Boolean

    fun getCount(definition: DefinitionModel): Long

    fun getCustom(customSql: String, definition: DefinitionModel): List<Map<String, Any>>
    fun get(id: String, definition: DefinitionModel): Map<String, Any>
    fun getMany(n: Int=1000, definition: DefinitionModel): List<Map<String, Any>>
    fun where(whereClauseItem: WhereClauseItem, definition: DefinitionModel): List<Map<String, Any>>

    fun bulkInsert(instances: List<Map<String, Any>>, definition: DefinitionModel): Boolean
    fun createOrUpdate(entity: Map<String, Any>, definition: DefinitionModel): Boolean
    fun create(entity: Map<String, Any>, definition: DefinitionModel): Boolean
    fun update(entity: Map<String, Any>, definition: DefinitionModel): Boolean
    fun updateWithCriteria(
            newValues: Map<String, Any>,
            whereClauseItem: WhereClauseItem,
            definition: DefinitionModel): Boolean

    fun updateCustom(customSql: String): Boolean

    fun delete(id: String, definition: DefinitionModel): Boolean
    fun deleteAll(definition: DefinitionModel): Boolean
}
