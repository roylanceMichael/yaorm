package org.roylance.yaorm.models

import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlOperators

/**
 * Created by mikeroylance on 10/16/15.
 */
public class WhereClauseItem (
        public val leftSide:String,
        public val operator:String,
        public val rightSide:Any,
        public val connectingAndOr:String?=null,
        public val connectingWhereClause:WhereClauseItem?=null) {
    companion object {
        public val Equals:String = SqlOperators.Equals
        public val GreaterThan:String = SqlOperators.GreaterThan
        public val LessThan:String = SqlOperators.LessThan
        public val NotEquals:String = SqlOperators.NotEquals

        public val And:String = CommonSqlDataTypeUtilities.And
        public val Or:String = CommonSqlDataTypeUtilities.Or
    }
}
