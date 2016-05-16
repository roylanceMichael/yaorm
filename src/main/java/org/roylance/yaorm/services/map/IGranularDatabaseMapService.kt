package org.roylance.yaorm.services.map

import org.roylance.yaorm.models.entity.EntityResultModel
import org.roylance.yaorm.models.migration.DefinitionModel

interface IGranularDatabaseMapService : AutoCloseable {
    fun isAvailable(): Boolean
    fun executeUpdateQuery(query:String): EntityResultModel
    fun executeSelectQuery(definitionModel:DefinitionModel, query:String): IMapCursor
    fun commit()
}
