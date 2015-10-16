package org.roylance.yaorm.models

import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import org.roylance.yaorm.utilities.SqlOperators

/**
 * Created by mikeroylance on 10/16/15.
 */
public class WhereClauseItem (
        public var leftSide:String,
        public var operator:String,
        public var rightSide:Any,
        public var connectingAndOr:String?=null,
        public var connectingWhereClause:WhereClauseItem?=null) {
    companion object {
        public val Equals:String = SqlOperators.Equals
        public val GreaterThan:String = SqlOperators.GreaterThan
        public val LessThan:String = SqlOperators.LessThan
        public val NotEquals:String = SqlOperators.NotEquals

        public val And:String = CommonSqlDataTypeUtilities.And
        public val Or:String = CommonSqlDataTypeUtilities.Or
    }
}
