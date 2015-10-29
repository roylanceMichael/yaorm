package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.migration.DifferenceModel
import org.roylance.yaorm.models.migration.IndexModel

object IndexModelComparisonUtil {
    fun addDifferenceIfDifference(
        name:String,
        currentIndexModel: IndexModel?,
        otherIndexModel: IndexModel?,
        differenceReports: MutableList<DifferenceModel>) {
        if (currentIndexModel == null && otherIndexModel != null) {
            val differenceReport = DifferenceModel(
                    DifferenceModel.EntityTypeIndex,
                    DifferenceModel.OperationDrop,
                    name,
                    indexModel = otherIndexModel)

            differenceReports.add(differenceReport)
        }

        if (currentIndexModel != null && otherIndexModel == null) {
            val differenceReport = DifferenceModel(
                    DifferenceModel.EntityTypeIndex,
                    DifferenceModel.OperationCreate,
                    name,
                    indexModel = currentIndexModel)

            differenceReports.add(differenceReport)
        }

        if (currentIndexModel != null && otherIndexModel != null) {
            val currentColumnNames =  currentIndexModel
                    .columnNames
                    .sortedBy { it }
                    .joinToString(CommonSqlDataTypeUtilities.Comma)

            val currentIncludeNames = currentIndexModel
                    .includeNames
                    .sortedBy { it }
                    .joinToString(CommonSqlDataTypeUtilities.Comma)

            val otherColumnNames = otherIndexModel
                    .columnNames
                    .sortedBy { it }
                    .joinToString(CommonSqlDataTypeUtilities.Comma)

            val otherIncludeNames = otherIndexModel
                    .includeNames
                    .sortedBy { it }
                    .joinToString(CommonSqlDataTypeUtilities.Comma)

            if (!currentColumnNames.equals(otherColumnNames) ||
                !currentIncludeNames.equals(otherIncludeNames)) {
                // drop other, create current
                val dropIndexDifference = DifferenceModel(
                        DifferenceModel.EntityTypeIndex,
                        DifferenceModel.OperationDrop,
                        name,
                        indexModel = otherIndexModel)

                differenceReports.add(dropIndexDifference)

                val createIndexDifference = DifferenceModel(
                        DifferenceModel.EntityTypeIndex,
                        DifferenceModel.OperationCreate,
                        name,
                        indexModel = currentIndexModel)

                differenceReports.add(createIndexDifference)
            }
        }
    }
}
