package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.migration.DifferenceModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel

object PropertyModelComparisonUtil {
    fun addDifferenceIfDifferent(
            name:String,
            currentProperty: PropertyDefinitionModel?,
            otherProperty: PropertyDefinitionModel?,
            differenceReports: MutableList<DifferenceModel>) {
        if (currentProperty == null && otherProperty != null) {
            val dropDifference = DifferenceModel(
                    DifferenceModel.EntityTypeColumn,
                    DifferenceModel.OperationDrop,
                    name,
                    propertyDefinition = otherProperty)
            differenceReports.add(dropDifference)
        }

        if (currentProperty != null && otherProperty == null) {
            val createDifference = DifferenceModel(
                    DifferenceModel.EntityTypeColumn,
                    DifferenceModel.OperationCreate,
                    name,
                    propertyDefinition = currentProperty)
            differenceReports.add(createDifference)
        }

        if (currentProperty != null && otherProperty != null &&
                (!currentProperty.name.equals(otherProperty.name) ||
                        !currentProperty.type.equals(otherProperty.type))) {
            val dropDifference = DifferenceModel(
                    DifferenceModel.EntityTypeColumn,
                    DifferenceModel.OperationDrop,
                    name,
                    propertyDefinition = otherProperty)
            differenceReports.add(dropDifference)

            val createDifference = DifferenceModel(
                    DifferenceModel.EntityTypeColumn,
                    DifferenceModel.OperationCreate,
                    name,
                    propertyDefinition = currentProperty)
            differenceReports.add(createDifference)
        }
    }
}
