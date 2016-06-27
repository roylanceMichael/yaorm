package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.YaormModel

object PropertyModelComparisonUtil {
    fun addDifferenceIfDifferent(
            name:String,
            currentProperty: YaormModel.ColumnDefinition?,
            otherProperty: YaormModel.ColumnDefinition?,
            differenceReports: MutableList<YaormModel.Difference>) {
        if (currentProperty == null && otherProperty != null) {
            val difference = YaormModel.Difference.newBuilder()
                    .setEntityType(YaormModel.Difference.EntityType.COLUMN)
                    .setOperation(YaormModel.Difference.Operation.DROP)
                    .setName(name)
                    .setPropertyDefinition(otherProperty)
                    .build()

            differenceReports.add(difference)
        }

        if (currentProperty != null && otherProperty == null) {
            val difference = YaormModel.Difference.newBuilder()
                    .setEntityType(YaormModel.Difference.EntityType.COLUMN)
                    .setOperation(YaormModel.Difference.Operation.CREATE)
                    .setName(name)
                    .setPropertyDefinition(currentProperty)
                    .build()

            differenceReports.add(difference)
        }

        if (currentProperty != null && otherProperty != null &&
                (!currentProperty.name.equals(otherProperty.name) ||
                        !currentProperty.type.equals(otherProperty.type))) {
            val dropDifference = YaormModel.Difference.newBuilder()
                    .setEntityType(YaormModel.Difference.EntityType.COLUMN)
                    .setOperation(YaormModel.Difference.Operation.DROP)
                    .setName(name)
                    .setPropertyDefinition(otherProperty)
                    .build()
            differenceReports.add(dropDifference)

            val createDifference = YaormModel.Difference.newBuilder()
                    .setEntityType(YaormModel.Difference.EntityType.COLUMN)
                    .setOperation(YaormModel.Difference.Operation.CREATE)
                    .setName(name)
                    .setPropertyDefinition(currentProperty)
                    .build()

            differenceReports.add(createDifference)
        }
    }
}
