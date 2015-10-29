package org.roylance.yaorm.models.migration

class DifferenceModel(
        public val entityType: String,
        public val operation: String,
        public val name: String,
        public val indexModel: IndexModel?=null,
        public val propertyDefinition: PropertyDefinitionModel?=null,
        public val definitionModel: DefinitionModel?=null) {
        companion object {
                public val OperationCreate = "create"
                public val OperationDrop = "drop"

                public val EntityTypeIndex = "index"
                public val EntityTypeColumn = "column"
                public val EntityTypeTable = "table"
        }
}