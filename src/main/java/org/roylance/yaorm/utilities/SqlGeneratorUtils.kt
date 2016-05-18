package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.migration.DefinitionModel
import java.util.*

object SqlGeneratorUtils {
    fun buildInsertUpdateValues(definition: DefinitionModel,
                              nameTypeMap: Map<String, ColumnNameTuple<String>>,
                              model: Map<String, Any?>): Map<String, String> {
        val returnMap = TreeMap<String, String>()
        definition.properties
                .filter { !it.foreignCollection }
                .sortedBy { it.name }
                .forEach {
                    if (nameTypeMap.containsKey(it.name) &&
                        model.containsKey(it.name)) {
                        val instanceValue = model[it.name]

                        if (instanceValue is IEntity) {
                            returnMap[it.name] =
                                    CommonSqlDataTypeUtilities.getFormattedString(instanceValue.id)
                        }
                        else {
                            returnMap[it.name] =
                                    CommonSqlDataTypeUtilities.getFormattedString(instanceValue)
                        }
                    }
                }

        return returnMap
    }
}
