package org.roylance.yaorm.models.migration

class DifferenceModel(
        val entityType: String,
        val operation: String,
        val name: String,
        val indexModel: IndexModel?=null,
        val propertyDefinition: PropertyDefinitionModel?=null,
        val definitionModel: DefinitionModel?=null) {
        companion object {
                const val OperationCreate = "create"
                const val OperationDrop = "drop"

                const val EntityTypeIndex = "index"
                const val EntityTypeColumn = "column"
                const val EntityTypeTable = "table"
        }
}