package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.YaormModel

object IndexModelComparisonUtil {
    fun addDifferenceIfDifference(
        name:String,
        currentIndexModel: YaormModel.Index?,
        otherIndexModel: YaormModel.Index?,
        differenceReports: MutableList<YaormModel.Difference>) {
        if (currentIndexModel == null && otherIndexModel != null) {
            val difference = YaormModel.Difference.newBuilder()
                .setEntityType(YaormModel.Difference.EntityType.INDEX)
                .setOperation(YaormModel.Difference.Operation.DROP)
                .setName(name)
                .setIndex(otherIndexModel)
                .build()

            differenceReports.add(difference)
        }

        if (currentIndexModel != null && otherIndexModel == null) {
            val difference = YaormModel.Difference.newBuilder()
                    .setEntityType(YaormModel.Difference.EntityType.INDEX)
                    .setOperation(YaormModel.Difference.Operation.CREATE)
                    .setName(name)
                    .setIndex(currentIndexModel)
                    .build()

            differenceReports.add(difference)
        }

        if (currentIndexModel != null && otherIndexModel != null) {
            val currentColumnNames =  currentIndexModel
                    .columnNames
                    .values
                    .sortedBy { it.name }
                    .joinToString(CommonUtils.Comma)

            val currentIncludeNames = currentIndexModel
                    .includeNames
                    .values
                    .sortedBy { it.name }
                    .joinToString(CommonUtils.Comma)

            val otherColumnNames = otherIndexModel
                    .columnNames
                    .values
                    .sortedBy { it.name }
                    .joinToString(CommonUtils.Comma)

            val otherIncludeNames = otherIndexModel
                    .includeNames
                    .values
                    .sortedBy { it.name }
                    .joinToString(CommonUtils.Comma)

            if (!currentColumnNames.equals(otherColumnNames) ||
                !currentIncludeNames.equals(otherIncludeNames)) {
                val dropIndexDifference = YaormModel.Difference.newBuilder()
                        .setEntityType(YaormModel.Difference.EntityType.INDEX)
                        .setOperation(YaormModel.Difference.Operation.DROP)
                        .setName(name)
                        .setIndex(otherIndexModel)
                        .build()

                differenceReports.add(dropIndexDifference)

                val createIndexDifference = YaormModel.Difference.newBuilder()
                        .setEntityType(YaormModel.Difference.EntityType.INDEX)
                        .setOperation(YaormModel.Difference.Operation.CREATE)
                        .setName(name)
                        .setIndex(currentIndexModel)
                        .build()

                differenceReports.add(createIndexDifference)
            }
        }
    }
}
