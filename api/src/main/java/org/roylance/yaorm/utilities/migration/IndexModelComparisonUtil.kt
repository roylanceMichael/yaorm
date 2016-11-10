package org.roylance.yaorm.utilities.migration

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.utilities.YaormUtils

object IndexModelComparisonUtil {
    fun addDifferenceIfDifference(
            name:String,
            currentIndex: YaormModel.Index?,
            otherIndex: YaormModel.Index?,
            differenceReports: MutableList<YaormModel.Difference>,
            tableDefinition: YaormModel.TableDefinition) {
        if (currentIndex == null && otherIndex != null) {
            val difference = YaormModel.Difference.newBuilder()
                .setEntityType(YaormModel.Difference.EntityType.INDEX)
                .setOperation(YaormModel.Difference.Operation.DROP)
                .setName(name)
                .setIndex(otherIndex)
                .setTableDefinition(tableDefinition)
                .build()

            differenceReports.add(difference)
        }

        if (currentIndex != null && otherIndex == null) {
            val difference = YaormModel.Difference.newBuilder()
                    .setEntityType(YaormModel.Difference.EntityType.INDEX)
                    .setOperation(YaormModel.Difference.Operation.CREATE)
                    .setName(name)
                    .setIndex(currentIndex)
                    .setTableDefinition(tableDefinition)
                    .build()

            differenceReports.add(difference)
        }

        if (currentIndex != null && otherIndex != null) {
            val currentColumnNames =  currentIndex
                    .columnNamesList
                    .sortedBy { it.name }
                    .joinToString(YaormUtils.Comma)

            val currentIncludeNames = currentIndex
                    .includeNamesList
                    .sortedBy { it.name }
                    .joinToString(YaormUtils.Comma)

            val otherColumnNames = otherIndex
                    .columnNamesList
                    .sortedBy { it.name }
                    .joinToString(YaormUtils.Comma)

            val otherIncludeNames = otherIndex
                    .includeNamesList
                    .sortedBy { it.name }
                    .joinToString(YaormUtils.Comma)

            if (currentColumnNames != otherColumnNames || currentIncludeNames != otherIncludeNames) {
                val dropIndexDifference = YaormModel.Difference.newBuilder()
                        .setEntityType(YaormModel.Difference.EntityType.INDEX)
                        .setOperation(YaormModel.Difference.Operation.DROP)
                        .setName(name)
                        .setIndex(otherIndex)
                        .setTableDefinition(tableDefinition)
                        .build()

                differenceReports.add(dropIndexDifference)

                val createIndexDifference = YaormModel.Difference.newBuilder()
                        .setEntityType(YaormModel.Difference.EntityType.INDEX)
                        .setOperation(YaormModel.Difference.Operation.CREATE)
                        .setName(name)
                        .setIndex(currentIndex)
                        .setTableDefinition(tableDefinition)
                        .build()

                differenceReports.add(createIndexDifference)
            }
        }
    }
}
