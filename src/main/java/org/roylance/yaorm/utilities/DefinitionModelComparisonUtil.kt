package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.DifferenceModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import java.util.*

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
            return
        }

        IndexModelComparisonUtil.addDifferenceIfDifference(
                currentDefinitionModel.name,
                currentDefinitionModel.indexModel,
                otherDefinitionModel.indexModel,
                differenceReports)

        // convert both to dictionaries
        val currentDictionary = HashMap<String, PropertyDefinitionModel>()
        val otherDictionary = HashMap<String, PropertyDefinitionModel>()

        currentDefinitionModel
            .properties
            .forEach {
                currentDictionary.put(it.name, it)
            }

        otherDefinitionModel
            .properties
            .forEach {
                otherDictionary.put(it.name, it)
            }

        // let's cycle through current first
        currentDictionary
            .keys
            .forEach {
                if (otherDictionary.containsKey(it)) {
                    PropertyModelComparisonUtil
                        .addDifferenceIfDifferent(
                                currentDefinitionModel.name,
                                currentDictionary[it],
                                otherDictionary[it],
                                differenceReports)
                }
                else {
                    PropertyModelComparisonUtil
                            .addDifferenceIfDifferent(
                                currentDefinitionModel.name,
                                currentDictionary[it],
                                null,
                                differenceReports)
                }
            }

        otherDictionary
            .keys
            .filter { !currentDictionary.containsKey(it) }
            .forEach {
                PropertyModelComparisonUtil
                        .addDifferenceIfDifferent(
                                currentDefinitionModel.name,
                                null,
                                otherDictionary[it],
                                differenceReports)
            }
    }
}
