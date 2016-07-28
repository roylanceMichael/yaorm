package org.roylance.yaorm.utilities

import org.roylance.yaorm.YaormModel
import java.util.*

object SqlOperators {
    const val Equals:String = "="
    const val GreaterThan:String = ">"
    const val LessThan:String = "<"
    const val NotEquals:String = "!="

    const val And = "and"
    const val Or = "or"

    val TypeToOperatorStrings = object: HashMap<YaormModel.WhereClause.OperatorType, String>() {
        init {
            put(YaormModel.WhereClause.OperatorType.EQUALS, Equals)
            put(YaormModel.WhereClause.OperatorType.GREATER_THAN, GreaterThan)
            put(YaormModel.WhereClause.OperatorType.LESS_THAN, LessThan)
            put(YaormModel.WhereClause.OperatorType.NOT_EQUALS, NotEquals)
        }
    }

    val TypeToConnectingStrings = object: HashMap<YaormModel.WhereClause.ConnectingAndOr, String>() {
        init {
            put(YaormModel.WhereClause.ConnectingAndOr.AND, And)
            put(YaormModel.WhereClause.ConnectingAndOr.OR, Or)
        }
    }
}
