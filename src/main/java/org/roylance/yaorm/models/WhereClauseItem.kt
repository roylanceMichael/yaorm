package org.roylance.yaorm.models

import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlOperators

class WhereClauseItem (
        var leftSide:String,
        var operator:String,
        var rightSide:Any,
        var connectingAndOr:String?=null,
        var connectingWhereClause:WhereClauseItem?=null) {
    companion object {
        const val Equals:String = SqlOperators.Equals
        const val GreaterThan:String = SqlOperators.GreaterThan
        const val LessThan:String = SqlOperators.LessThan
        const val NotEquals:String = SqlOperators.NotEquals

        const val And:String = CommonSqlDataTypeUtilities.And
        const val Or:String = CommonSqlDataTypeUtilities.Or
    }
}
