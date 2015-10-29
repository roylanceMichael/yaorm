package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.DifferenceModel

/**
 * Created by mikeroylance on 10/29/15.
 */
object DefinitionModelComparisonUtil {
    fun addDifferenceIfDifferent(
            currentDefinitionModel: DefinitionModel,
            otherDefinitionModel: DefinitionModel?,
            differenceReports: MutableList<DifferenceModel>) {

        if (otherDefinitionModel == null) {
            val newDifferenceModel = DifferenceModel(
                    DifferenceModel.EntityTypeTable,
                    DifferenceModel.OperationCreate,
                    currentDefinitionModel.name,
                    definitionModel = currentDefinitionModel)

            differenceReports.add(newDifferenceModel)
        }
    }
}
