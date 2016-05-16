package org.roylance.yaorm.models.db

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities

class GenericModel(
        override var id: String = "",
        var strVal: String = "",
        var intVal: Int = 0,
        var longVal: Long = 0L,
        var doubleVal: Double = 0.0,
        var boolVal: Boolean = false) : IEntity {
    companion object {
        const val IdName = "id"
        const val StrValName = "strVal"
        const val IntValName = "intVal"
        const val LongValName = "longVal"
        const val DoubleValName = "doubleVal"
        const val BoolValName = "boolVal"

        fun buildDefinitionModel():DefinitionModel {
            val idProperty = PropertyDefinitionModel(name = IdName, type = CommonSqlDataTypeUtilities.JavaStringName)
            val strProperty = PropertyDefinitionModel(name = StrValName, type = CommonSqlDataTypeUtilities.JavaStringName)
            val intProperty = PropertyDefinitionModel(name = IntValName, type = CommonSqlDataTypeUtilities.JavaIntegerName)
            val longProperty = PropertyDefinitionModel(name = LongValName, type = CommonSqlDataTypeUtilities.JavaLongName)
            val doubleProperty = PropertyDefinitionModel(name = DoubleValName, type = CommonSqlDataTypeUtilities.JavaDoubleName)
            val boolProperty = PropertyDefinitionModel(name = BoolValName, type = CommonSqlDataTypeUtilities.JavaBooleanName)

            return DefinitionModel(name = GenericModel::class.java.simpleName,
                    properties = listOf(idProperty, strProperty, intProperty, longProperty, doubleProperty, boolProperty),
                    indexModel = null)
        }
    }
}
