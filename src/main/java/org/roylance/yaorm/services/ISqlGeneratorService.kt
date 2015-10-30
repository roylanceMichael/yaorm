package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem

public interface ISqlGeneratorService {
    public val javaIdName: String
    public val javaTypeToSqlType: Map<String, String>
    public val bulkInsertSize:Int

    public fun <K, T: IEntity<K>> buildCountSql(classType: Class<T>): String

    public fun <K, T: IEntity<K>> buildCreateColumn(classType: Class<T>, columnName:String, javaType: String): String?
    public fun <K, T: IEntity<K>> buildDropColumn(classType: Class<T>, columnName:String): String?

    public fun <K, T: IEntity<K>> buildCreateIndex(classType: Class<T>, columns: List<String>, includes: List<String>): String?
    public fun <K, T: IEntity<K>> buildDropIndex(classType: Class<T>, columns: List<String>): String?

    public fun <K, T: IEntity<K>> buildDropTable(classType: Class<T>): String
    public fun <K, T: IEntity<K>> buildCreateTable(classType: Class<T>): String?

    public fun <K, T: IEntity<K>> buildDeleteAll(classModel: Class<T>) : String
    public fun <K, T: IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): String?
    public fun <K, T: IEntity<K>> buildDeleteWithCriteria(classModel: Class<T>, whereClauseItem: WhereClauseItem): String

    public fun <K, T: IEntity<K>> buildBulkInsert(classModel: Class<T>, items: List<T>) : String
    public fun <K, T: IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): String?

    public fun <K, T: IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): String?
    public fun <K, T: IEntity<K>> buildUpdateWithCriteria(classModel: Class<T>, newValues: Map<String, Any>, whereClauseItem: WhereClauseItem): String?

    public fun <K, T: IEntity<K>> buildSelectAll(classModel: Class<T>): String
    public fun <K, T: IEntity<K>> buildWhereClause(classModel: Class<T>, whereClauseItem: WhereClauseItem): String?

}
