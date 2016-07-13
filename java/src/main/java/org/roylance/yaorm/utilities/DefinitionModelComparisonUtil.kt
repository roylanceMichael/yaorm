package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.YaormModel
import java.util.*

object DefinitionModelComparisonUtil {
    fun addDifferenceIfDifferent(
            currentDefinitionModel: YaormModel.TableDefinition,
            otherDefinitionModel: YaormModel.TableDefinition?,
            differenceReports: MutableList<YaormModel.Difference>) {

        if (otherDefinitionModel == null) {
            val newDifferenceModel = YaormModel.Difference.newBuilder()
                .setEntityType(YaormModel.Difference.EntityType.TABLE)
                .setOperation(YaormModel.Difference.Operation.CREATE)
                .setName(currentDefinitionModel.name)
                .setTableDefinition(currentDefinitionModel)

            differenceReports.add(newDifferenceModel.build())
            return
        }

        IndexModelComparisonUtil.addDifferenceIfDifference(
                currentDefinitionModel.name,
                currentDefinitionModel.index,
                otherDefinitionModel.index,
                differenceReports)

        // convert both to dictionaries
        val currentDictionary = HashMap<String, YaormModel.ColumnDefinition>()
        val otherDictionary = HashMap<String, YaormModel.ColumnDefinition>()

        currentDefinitionModel
            .columnDefinitionsList
            .forEach {
                currentDictionary.put(it.name, it)
            }

        otherDefinitionModel
            .columnDefinitionsList
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
