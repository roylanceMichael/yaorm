package org.roylance.yaorm.models.db

import org.roylance.yaorm.models.IEntity

/**
 * Created by mikeroylance on 10/29/15.
 */
public class GenericModel(
        public override var id: String = "",
        public var strVal: String = "",
        public var intVal: Int = 0,
        public var longVal: Long = 0L,
        public var doubleVal: Double = 0.0,
        public var boolVal: Boolean = false) : IEntity<String> {
    companion object {
        public val IdName = "id"
        public val StrValName = "strVal"
        public val IntValName = "intVal"
        public val DoubleValName = "doubleVal"
        public val BoolValName = "boolVal"
    }
}
