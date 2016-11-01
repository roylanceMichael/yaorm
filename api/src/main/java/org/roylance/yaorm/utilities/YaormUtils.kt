@file:Suppress("UNCHECKED_CAST")

package org.roylance.yaorm.utilities

import com.google.protobuf.ByteString
import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.IKeywordHandler
import java.util.*

object YaormUtils {
    private const val DoubleSingleQuote = "''"
    private const val IndexTableNameLength = 15

    const val IdName = "id"
    const val EmptyString = ""

    const val SingleQuote = "'"
    const val DoubleQuote = "\""
    const val AccentQuote = "`"
    const val Null = "null"
    const val Space = " "
    const val Comma = ","
    const val Equals = "="
    const val SemiColon = ";"
    const val CarriageReturn = '\n'
    const val SpacedUnion = "${CarriageReturn}union "
    const val Underscore = "_"

    const val SpacedAnd = " and "
    const val And = "and"
    const val Or = "or"

    const val Is = "is"
    const val LeftParen = "("
    const val RightParen = ")"

    fun buildColumn(value: Any?,
                    propertyDefinition:YaormModel.ColumnDefinition):YaormModel.Column {
        val returnHolder = YaormModel.Column.newBuilder()
        returnHolder.definition = propertyDefinition

        val notNullValueAsString = if (value == null) "" else value.toString()

        if (propertyDefinition.type == YaormModel.ProtobufType.STRING) {
            returnHolder.stringHolder = notNullValueAsString.toString()
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.BOOL) {
            returnHolder.boolHolder = getBool(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.INT32) {
            returnHolder.int32Holder = if (value == null) 0 else getInt(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.INT64) {
            returnHolder.int64Holder = if (value == null) 0L else getLong(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.FIXED32) {
            returnHolder.fixed32Holder = if (value == null) 0 else getInt(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.FIXED64) {
            returnHolder.fixed64Holder =  if (value == null) 0L else getLong(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SFIXED32) {
            returnHolder.sfixed32Holder = if (value == null) 0 else getInt(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SFIXED64) {
            returnHolder.sfixed64Holder =  if (value == null) 0L else getLong(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.UINT32) {
            returnHolder.uint32Holder = if (value == null) 0 else getInt(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.UINT64) {
            returnHolder.uint64Holder = if (value == null) 0L else getLong(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SINT32) {
            returnHolder.sint32Holder = if (value == null) 0 else getInt(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SINT64) {
            returnHolder.sint64Holder = if (value == null) 0L else getLong(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.DOUBLE) {
            returnHolder.doubleHolder = if (value == null) 0.0 else getDouble(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.FLOAT) {
            returnHolder.floatHolder = if (value == null) 0F else getFloat(notNullValueAsString)
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.BYTES) {
            if (value is ByteString) {
                returnHolder.bytesHolder = value
            }
            else if (value is String) {
                returnHolder.bytesHolder = ByteString.copyFromUtf8(value)
            }
            else {
                returnHolder.bytesHolder = ByteString.EMPTY
            }
        }

        return returnHolder.build()
    }

    fun getAnyObject(holder:YaormModel.Column):Any? {
        val propertyDefinition = holder.definition
        if (propertyDefinition.type == YaormModel.ProtobufType.STRING) {
            return holder.stringHolder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.BOOL) {
            return holder.boolHolder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.INT32) {
            return holder.int32Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.INT64) {
            return holder.int64Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.FIXED32) {
            return holder.fixed32Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.FIXED64) {
            return holder.fixed64Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SFIXED32) {
            return holder.sfixed32Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SFIXED64) {
            return holder.sfixed64Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SINT32) {
            return holder.sint32Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.SINT64) {
            return holder.sint64Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.UINT32) {
            return holder.uint32Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.UINT64) {
            return holder.uint64Holder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.DOUBLE) {
            return holder.doubleHolder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.FLOAT) {
            return holder.floatHolder
        }

        if (propertyDefinition.type == YaormModel.ProtobufType.BYTES) {
            return holder.bytesHolder
        }
        return null
    }

    fun getFormattedString(value: YaormModel.Column): String {
        if (!value.hasDefinition()) {
            return Null
        }

        if (value.definition.type == YaormModel.ProtobufType.STRING) {
            return SingleQuote + value.stringHolder.replace(SingleQuote, DoubleSingleQuote) + SingleQuote
        }

        if (value.definition.type == YaormModel.ProtobufType.BOOL) {
            return Integer.toString(if (value.boolHolder) 1 else 0)
        }

        if (value.definition.type == YaormModel.ProtobufType.INT32) {
            return value.int32Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.INT64) {
            return value.int64Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.FIXED32) {
            return value.fixed32Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.FIXED64) {
            return value.fixed64Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.SFIXED32) {
            return value.sfixed32Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.SFIXED64) {
            return value.sfixed64Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.UINT32) {
            return value.uint32Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.UINT64) {
            return value.uint64Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.SINT32) {
            return value.sint32Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.SINT64) {
            return value.sint64Holder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.DOUBLE) {
            return value.doubleHolder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.FLOAT) {
            return value.floatHolder.toString()
        }

        if (value.definition.type == YaormModel.ProtobufType.BYTES) {
            return SingleQuote + value.bytesHolder.toStringUtf8() + SingleQuote
        }

        return Null
    }

    fun lowercaseFirstChar(input:String):String {
        if (input.length == 0) {
            return input
        }

        if (input.length == 1) {
            return input.toLowerCase()
        }

        val firstChar = input[0].toLowerCase()
        return firstChar + input.substring(1)
    }

    fun buildIndexName(tableName: String, columnNames:List<String>) : String {
        var alteredTableName = tableName
        if (tableName.length > IndexTableNameLength) {
            alteredTableName = tableName.substring(0, IndexTableNameLength)
        }
        val joinedColumnNames = columnNames.sortedBy { it }.joinToString(Underscore)
        val columnNamesHash = customHash(joinedColumnNames).toString().replace("-", Underscore)
        return "$alteredTableName$Underscore$columnNamesHash${Underscore}idx"
    }

    fun getNameTypes(
            definition: YaormModel.TableDefinition,
            javaIdType: YaormModel.ProtobufType,
            javaTypeToSqlType: Map<YaormModel.ProtobufType, String>): List<ColumnNameTuple<String>> {
        val nameTypes = ArrayList<ColumnNameTuple<String>>()
        var foundIdColumnName = false

        // let's handle the types now
        definition
                .columnDefinitionsList
                .sortedBy { it.order }
                .forEach {
                    val columnName = it.name
                    val javaType = it.type
                    val sqlColumnName = YaormUtils.lowercaseFirstChar(it.name)
                    val javaColumnName = columnName

                    if (javaTypeToSqlType.containsKey(javaType)) {
                        val dataType = javaTypeToSqlType[javaType]
                        if (IdName == sqlColumnName) {
                            foundIdColumnName = true
                        }
                        nameTypes.add(ColumnNameTuple(sqlColumnName, javaColumnName, dataType!!))
                    }
                    else if (javaTypeToSqlType.containsKey(javaIdType)) {
                        nameTypes.add(ColumnNameTuple(
                                sqlColumnName,
                                javaColumnName,
                                javaTypeToSqlType[javaIdType]!!,
                                true))
                    }
                }

        if (!foundIdColumnName) {
            return ArrayList()
        }

        return nameTypes
    }

    fun buildWhereClause(whereClauseItem: YaormModel.WhereClause, keywordHandler:IKeywordHandler):String {
        val filterItems = StringBuilder()
        var currentWhereClauseItem: YaormModel.WhereClause? = whereClauseItem

        while (currentWhereClauseItem != null) {
            if (currentWhereClauseItem.operatorType == YaormModel.WhereClause.OperatorType.IN &&
                    currentWhereClauseItem.inItemsCount > 0) {
                val items = currentWhereClauseItem.inItemsList.map {
                    val tempColumn = buildColumn(it, currentWhereClauseItem!!.nameAndProperty.definition)
                    val stringValue = getFormattedString(tempColumn)
                    stringValue
                }.joinToString()

                filterItems.append(keywordHandler.buildKeyword(currentWhereClauseItem.nameAndProperty.definition.name) +
                        Space +
                        "in ($items)" +
                        Space)
            }
            else {
                val stringValue = YaormUtils
                        .getFormattedString(currentWhereClauseItem.nameAndProperty)
                filterItems.append(
                        keywordHandler.buildKeyword(currentWhereClauseItem.nameAndProperty.definition.name) +
                                SqlOperators.TypeToOperatorStrings[currentWhereClauseItem.operatorType] +
                                stringValue +
                                Space)
            }

            if (currentWhereClauseItem.connectingAndOr != YaormModel.WhereClause.ConnectingAndOr.NONE &&
                currentWhereClauseItem.hasConnectingWhereClause()) {
                filterItems.append(currentWhereClauseItem.connectingAndOr.name + YaormUtils.Space)
                currentWhereClauseItem = currentWhereClauseItem.connectingWhereClause
            }
            else {
                break
            }
        }

        return filterItems.toString().trim()
    }

    fun checkIfOk(definition: YaormModel.TableDefinition):Boolean {
        return definition.columnDefinitionsList.any { it.name == IdName }
    }

    fun getLastWord(item:String):String {
        val splitItems = item.split(".")
        if (splitItems.size > 0) {
            return splitItems[splitItems.size - 1]
        }
        return item
    }

    fun buildMapFromRecord(record:YaormModel.Record):Map<String, Any?> {
        val returnMap = HashMap<String, Any?>()
        record.columnsList.forEach {
            val item = this.getAnyObject(it)
            returnMap[it.definition.name] = item
        }
        return returnMap
    }

    fun buildIdColumnDefinition(): YaormModel.ColumnDefinition {
        return YaormModel.ColumnDefinition.newBuilder()
                .setColumnType(YaormModel.ColumnDefinition.ColumnType.SCALAR)
                .setType(YaormModel.ProtobufType.STRING)
                .setName(YaormUtils.IdName)
                .build()
    }

    fun buildIdColumn(id: String): YaormModel.Column {
        return YaormModel.Column.newBuilder()
            .setStringHolder(id)
            .setDefinition(buildIdColumnDefinition())
            .build()
    }

    fun getIdColumn(columns:List<YaormModel.ColumnDefinition>):YaormModel.ColumnDefinition? {
        return columns.firstOrNull { it.name == IdName }
    }

    fun getIdColumn(columns:List<YaormModel.Column>):YaormModel.Column? {
        return columns.firstOrNull { it.definition.name == IdName }
    }

    fun buildMapsFromRecords(records:YaormModel.Records):List<Map<String, Any?>> {
        return records.recordsList.map { buildMapFromRecord(it) }
    }

    fun <T> getValueFromRecord(name:String, record:YaormModel.Record):T? {
        val foundColumn = record.columnsList.firstOrNull { name == it.definition.name } ?: return  null
        return getAnyObject(foundColumn) as T
    }

    fun getValueFromRecordAny(name:String, record:YaormModel.Record):Any? {
        val foundColumn = record.columnsList.firstOrNull { name == it.definition.name } ?: return  null
        return getAnyObject(foundColumn)
    }

    fun customHash(value: String): Long {
        var largePrimeNumber = 3612342499L
        val valueLength = value.length

        for (i in 0..valueLength - 1) {
            largePrimeNumber = 31 * largePrimeNumber + value[i].toLong()
        }
        return largePrimeNumber
    }

    private fun bothItemsNull(firstObject:Any?, secondObject: Any?):Boolean {
        return firstObject == null && secondObject == null
    }

    private fun bothItemsNotNull(firstObject:Any?, secondObject: Any?):Boolean {
        return firstObject != null && secondObject != null
    }

    private fun getBool(item:String):Boolean {
        if (item == "0") {
            return false
        }
        if (item.toLowerCase() == "false") {
            return false
        }
        if (item.length == 0) {
            return false
        }
        return true
    }

    private fun getInt(item:String):Int {
        try {
            return item.toInt()
        }
        catch(e:NumberFormatException) {
            return 0
        }
    }

    private fun getLong(item:String):Long {
        try {
            return item.toLong()
        }
        catch(e:NumberFormatException) {
            return 0
        }
    }

    private fun getDouble(item:String):Double {
        try {
            return item.toDouble()
        }
        catch(e:NumberFormatException) {
            return 0.0
        }
    }

    private fun getFloat(item:String):Float {
        try {
            return item.toFloat()
        }
        catch(e:NumberFormatException) {
            return 0F
        }
    }
}
