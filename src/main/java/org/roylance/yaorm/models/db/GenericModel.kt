package org.roylance.yaorm.models.db

import org.roylance.yaorm.models.IEntity

class GenericModel(
        override var id: String = "",
        var strVal: String = "",
        var intVal: Int = 0,
        var longVal: Long = 0L,
        var doubleVal: Double = 0.0,
        var boolVal: Boolean = false) : IEntity<String> {
    companion object {
        val IdName = "id"
        val StrValName = "strVal"
        val IntValName = "intVal"
        val DoubleValName = "doubleVal"
        val BoolValName = "boolVal"
    }
}
