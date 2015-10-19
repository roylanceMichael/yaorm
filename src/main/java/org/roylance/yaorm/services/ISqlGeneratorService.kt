package org.roylance.yaorm.services

import com.google.common.base.Optional
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem

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
    public fun <K, T: IEntity<K>> buildDeleteTable(classModel: Class<T>, primaryKey: K): Optional<String>
    public fun <K, T: IEntity<K>> buildDeleteWithCriteria(classModel: Class<T>, whereClauseItem: WhereClauseItem): String

    public fun <K, T: IEntity<K>> buildBulkInsert(classModel: Class<T>, items: List<T>) : String
    public fun <K, T: IEntity<K>> buildInsertIntoTable(classModel: Class<T>, newInsertModel: T): Optional<String>

    public fun <K, T: IEntity<K>> buildUpdateTable(classModel: Class<T>, updateModel: T): Optional<String>
    public fun <K, T: IEntity<K>> buildUpdateWithCriteria(classModel: Class<T>, newValues: Map<String, Any>, whereClauseItem: WhereClauseItem): Optional<String>

    public fun <K, T: IEntity<K>> buildSelectAll(classModel: Class<T>): String
    public fun <K, T: IEntity<K>> buildWhereClause(classModel: Class<T>, whereClauseItem: WhereClauseItem): Optional<String>

}
