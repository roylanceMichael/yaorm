package org.roylance.yaorm.services

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity

/**
 * Created by mikeroylance on 10/15/15.
 */
public interface ISqlGeneratorService {
    public val javaIdName: String
    public val javaTypeToSqlType: Map<String, String>
    public val bulkInsertSize:Int

    public fun <K, T: IEntity<K>> buildDropTable(classType: Class<T>): String
    public fun <K, T: IEntity<K>> buildInitialTableCreate(classType: Class<T>): Optional<String>

    public fun <K, T: IEntity<K>> buildDeleteAll(classModel: Class<T>) : String
    public fun <K, T: IEntity<K>> buildBulkInsert(classModel: Class<T>, items: List<T>) : String
    public fun <K, T: IEntity<K>> buildSelectAll(classModel: Class<T>): String
    public fun <K, T: IEntity<K>> buildWhereClauseAnd(classModel: Class<T>, values: Map<String, Any>, operator:String): Optional<String>
    public fun <K, T: IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): Optional<String>
    public fun <K, T: IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): Optional<String>
    public fun <K, T: IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): Optional<String>
}
