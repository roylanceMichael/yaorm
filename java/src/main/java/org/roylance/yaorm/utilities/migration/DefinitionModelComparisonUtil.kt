package org.roylance.yaorm.utilities.migration

import org.roylance.yaorm.models.YaormModel
import java.util.*

object DefinitionModelComparisonUtil {
    fun addDifferenceIfDifferent(
            currentDefinition: YaormModel.TableDefinition,
            otherDefinition: YaormModel.TableDefinition?,
            differenceReports: MutableList<YaormModel.Difference>) {

        if (otherDefinition == null) {
            val newDifferenceModel = YaormModel.Difference.newBuilder()
                .setEntityType(YaormModel.Difference.EntityType.TABLE)
                .setOperation(YaormModel.Difference.Operation.CREATE)
                .setName(currentDefinition.name)
                .setTableDefinition(currentDefinition)

            differenceReports.add(newDifferenceModel.build())
            return
        }

        IndexModelComparisonUtil.addDifferenceIfDifference(
                currentDefinition.name,
                currentDefinition.index,
                otherDefinition.index,
                differenceReports,
                currentDefinition)

        // convert both to dictionaries
        val currentDictionary = HashMap<String, YaormModel.ColumnDefinition>()
        val otherDictionary = HashMap<String, YaormModel.ColumnDefinition>()

        currentDefinition
            .columnDefinitionsList
            .forEach {
                currentDictionary.put(it.name, it)
            }

        otherDefinition
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
                                currentDefinition.name,
                                currentDictionary[it],
                                otherDictionary[it],
                                differenceReports,
                                currentDefinition)
                }
                else {
                    PropertyModelComparisonUtil
                            .addDifferenceIfDifferent(
                                currentDefinition.name,
                                currentDictionary[it],
                                null,
                                differenceReports,
                                    currentDefinition)
                }
            }

        otherDictionary
            .keys
            .filter { !currentDictionary.containsKey(it) }
            .forEach {
                PropertyModelComparisonUtil
                        .addDifferenceIfDifferent(
                                currentDefinition.name,
                                null,
                                otherDictionary[it],
                                differenceReports,
                                currentDefinition)
            }
    }
}
