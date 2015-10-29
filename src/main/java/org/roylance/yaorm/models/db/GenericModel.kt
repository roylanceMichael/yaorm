package org.roylance.yaorm.models.db

import org.roylance.yaorm.models.IEntity

/**
 * Created by mikeroylance on 10/29/15.
 */
public class GenericModel<T>(
        public override var id: T? = null,
        public var strVal: kotlin.String = "",
        public var intVal: kotlin.Int = 0,
        public var doubleVal: kotlin.Double = 0.0,
        public var boolVal: kotlin.Boolean = false) : IEntity<T?> {
    companion object {
        public val IdName = "id"
        public val StrValName = "strVal"
        public val IntValName = "intVal"
        public val DoubleValName = "doubleVal"
        public val BoolValName = "boolVal"
    }
}
