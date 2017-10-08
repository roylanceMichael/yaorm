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

    val TypeToOperatorStrings = HashMap<YaormModel.WhereClause.OperatorType, String>()

    val TypeToConnectingStrings = HashMap<YaormModel.WhereClause.ConnectingAndOr, String>()

    init {
        TypeToOperatorStrings.put(YaormModel.WhereClause.OperatorType.EQUALS, Equals)
        TypeToOperatorStrings.put(YaormModel.WhereClause.OperatorType.GREATER_THAN, GreaterThan)
        TypeToOperatorStrings.put(YaormModel.WhereClause.OperatorType.LESS_THAN, LessThan)
        TypeToOperatorStrings.put(YaormModel.WhereClause.OperatorType.NOT_EQUALS, NotEquals)

        TypeToConnectingStrings.put(YaormModel.WhereClause.ConnectingAndOr.AND, And)
        TypeToConnectingStrings.put(YaormModel.WhereClause.ConnectingAndOr.OR, Or)
    }
}
