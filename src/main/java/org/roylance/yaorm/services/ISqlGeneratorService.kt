package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem

interface ISqlGeneratorService {
    val javaIdName: String
    val javaTypeToSqlType: Map<String, String>
    val bulkInsertSize:Int

    fun <K, T: IEntity<K>> buildCountSql(classType: Class<T>): String

    fun <K, T: IEntity<K>> buildCreateColumn(classType: Class<T>, columnName:String, javaType: String): String?
    fun <K, T: IEntity<K>> buildDropColumn(classType: Class<T>, columnName:String): String?

    fun <K, T: IEntity<K>> buildCreateIndex(classType: Class<T>, columns: List<String>, includes: List<String>): String?
    fun <K, T: IEntity<K>> buildDropIndex(classType: Class<T>, columns: List<String>): String?

    fun <K, T: IEntity<K>> buildDropTable(classType: Class<T>): String
    fun <K, T: IEntity<K>> buildCreateTable(classType: Class<T>): String?

    fun <K, T: IEntity<K>> buildDeleteAll(classModel: Class<T>) : String
    fun <K, T: IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): String?
    fun <K, T: IEntity<K>> buildDeleteWithCriteria(classModel: Class<T>, whereClauseItem: WhereClauseItem): String

    fun <K, T: IEntity<K>> buildBulkInsert(classModel: Class<T>, items: List<T>) : String
    fun <K, T: IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): String?

    fun <K, T: IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): String?
    fun <K, T: IEntity<K>> buildUpdateWithCriteria(classModel: Class<T>, newValues: Map<String, Any>, whereClauseItem: WhereClauseItem): String?

    fun <K, T: IEntity<K>> buildSelectAll(classModel: Class<T>, n: Int = 1000): String
    fun <K, T: IEntity<K>> buildWhereClause(classModel: Class<T>, whereClauseItem: WhereClauseItem): String?
}
