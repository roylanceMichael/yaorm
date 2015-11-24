package org.roylance.yaorm.models.migration

class DifferenceModel(
        val entityType: String,
        val operation: String,
        val name: String,
        val indexModel: IndexModel?=null,
        val propertyDefinition: PropertyDefinitionModel?=null,
        val definitionModel: DefinitionModel?=null) {
        companion object {
                val OperationCreate = "create"
                val OperationDrop = "drop"

                val EntityTypeIndex = "index"
                val EntityTypeColumn = "column"
                val EntityTypeTable = "table"
        }
}