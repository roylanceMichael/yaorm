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
        val Equals:String = SqlOperators.Equals
        val GreaterThan:String = SqlOperators.GreaterThan
        val LessThan:String = SqlOperators.LessThan
        val NotEquals:String = SqlOperators.NotEquals

        val And:String = CommonSqlDataTypeUtilities.And
        val Or:String = CommonSqlDataTypeUtilities.Or
    }
}
