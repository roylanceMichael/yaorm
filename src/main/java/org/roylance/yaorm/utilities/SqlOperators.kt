package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.YaormModel
import java.util.*

object SqlOperators {
    const val Equals:String = "="
    const val GreaterThan:String = ">"
    const val LessThan:String = "<"
    const val NotEquals:String = "!="

    const val And = "and"
    const val Or = "or"

    val TypeToOperatorStrings = object: HashMap<YaormModel.WhereClauseItem.OperatorType, String>() {
        init {
            put(YaormModel.WhereClauseItem.OperatorType.EQUALS, Equals)
            put(YaormModel.WhereClauseItem.OperatorType.GREATER_THAN, GreaterThan)
            put(YaormModel.WhereClauseItem.OperatorType.LESS_THAN, LessThan)
            put(YaormModel.WhereClauseItem.OperatorType.NOT_EQUALS, NotEquals)
        }
    }

    val TypeToConnectingStrings = object: HashMap<YaormModel.WhereClauseItem.ConnectingAndOr, String>() {
        init {
            put(YaormModel.WhereClauseItem.ConnectingAndOr.AND, And)
            put(YaormModel.WhereClauseItem.ConnectingAndOr.OR, Or)
        }
    }
}
