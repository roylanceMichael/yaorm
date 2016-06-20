package org.roylance.yaorm.utilities

import com.google.protobuf.ByteString
import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.YaormModel
import java.util.*

object CommonUtils {
    private val SingleQuote = "'"
    private val DoubleSingleQuote = "''"

    const val IdName = "id"

    const val Null = "null"
    const val Space = " "
    const val Comma = ","
    const val Equals = "="
    const val SemiColon = ";"
    const val CarriageReturn = '\n'
    const val SpacedUnion = "${CarriageReturn}union "
    const val SpacedAnd = " and "
    const val And = "and"
    const val Or = "or"
    const val Underscore = "_"
    const val Is = "is"
    const val LeftParen = "("
    const val RightParen = ")"

    const val JavaFullyQualifiedStringName: String = "String"
    const val JavaObjectName: String = "java.lang.Object"
    const val JavaStringName: String = "java.lang.String"
    const val JavaDoubleName: String = "double"
    const val JavaAltObjectName: String = "Object"
    const val JavaAltDoubleName: String = "double"
    const val JavaAlt1DoubleName: String = "double"
    const val JavaIntegerName: String = "int"
    const val JavaAltIntegerName: String = "Integer"
    const val JavaAlt1IntegerName: String = "java.lang.Integer"
    const val JavaLongName: String = "long"
    const val JavaAltLongName: String = "Long"
    const val JavaAlt1LongName: String = "java.lang.Long"
    const val JavaByteName: String = "byte"
    const val JavaBooleanName: String = "boolean"
    const val JavaAltBooleanName: String = "Boolean"
    const val JavaAlt1BooleanName: String = "java.lang.Boolean"

    const val Get:String = "get"
    const val Set:String = "set"
    const val GetSetLength = Get.length

    val JavaToProtoMap = object: HashMap<Class<*>, YaormModel.ProtobufType>() {
        init {
            put(String::class.java, YaormModel.ProtobufType.STRING)
            put(Any::class.java, YaormModel.ProtobufType.STRING)
            put(Double::class.java, YaormModel.ProtobufType.DOUBLE)
            put(Int::class.java, YaormModel.ProtobufType.INT64)
            put(Float::class.java, YaormModel.ProtobufType.DOUBLE)
            put(Long::class.java, YaormModel.ProtobufType.INT64)
            put(Boolean::class.java, YaormModel.ProtobufType.BOOL)
            put(ByteArray::class.java, YaormModel.ProtobufType.BYTES)
        }
    }

    fun buildColumn(value: Any?, propertyDefinition:YaormModel.ColumnDefinition):YaormModel.Column {
        val returnHolder = YaormModel.Column.newBuilder()
        returnHolder.definition = propertyDefinition

        val notNullValueAsString = if (value == null) "" else value.toString()

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.STRING)) {
            returnHolder.stringHolder = notNullValueAsString.toString()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.BOOL)) {
            returnHolder.boolHolder = if(notNullValueAsString == "1" || notNullValueAsString.toLowerCase().equals("true")) true else false
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.INT32)) {
            returnHolder.int32Holder = if (value == null) 0 else notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.INT64)) {
            returnHolder.int64Holder = if (value == null) 0L else notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FIXED32)) {
            returnHolder.fixed32Holder = if (value == null) 0 else notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FIXED64)) {
            returnHolder.fixed64Holder =  if (value == null) 0L else notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SFIXED32)) {
            returnHolder.sfixed32Holder = if (value == null) 0 else notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SFIXED64)) {
            returnHolder.sfixed64Holder =  if (value == null) 0L else notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.UINT32)) {
            returnHolder.uint32Holder = if (value == null) 0 else notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.UINT64)) {
            returnHolder.uint64Holder = if (value == null) 0L else notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SINT32)) {
            returnHolder.sint32Holder = if (value == null) 0 else notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SINT64)) {
            returnHolder.sint64Holder = if (value == null) 0L else notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.DOUBLE)) {
            returnHolder.doubleHolder = if (value == null) 0.0 else notNullValueAsString.toDouble()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FLOAT  )) {
            returnHolder.floatHolder = if (value == null) 0F else notNullValueAsString.toFloat()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.BYTES  )) {
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

    fun buildColumn(value: Any?, javaType: Class<*>, name:String):YaormModel.Column {
        val returnHolder = YaormModel.Column.newBuilder()
        val propertyDefinition = YaormModel.ColumnDefinition.newBuilder().setName(name).setIsKey(IdName.equals(name))

        if (JavaToProtoMap.containsKey(javaType)) {
            propertyDefinition.type = JavaToProtoMap[javaType]
        }
        else {
            propertyDefinition.type = YaormModel.ProtobufType.STRING
        }

        returnHolder.setDefinition(propertyDefinition)

        val notNullValueAsString = if (value == null) "" else value.toString()

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.STRING)) {
            returnHolder.stringHolder = notNullValueAsString.toString()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.BOOL)) {
            returnHolder.boolHolder = notNullValueAsString.toBoolean()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.INT32)) {
            returnHolder.int32Holder = notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.INT64)) {
            returnHolder.int64Holder = notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FIXED32)) {
            returnHolder.fixed32Holder = notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FIXED64)) {
            returnHolder.fixed64Holder = notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SFIXED32)) {
            returnHolder.sfixed32Holder = notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SFIXED64)) {
            returnHolder.sfixed64Holder = notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.UINT32)) {
            returnHolder.uint32Holder = notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.UINT64)) {
            returnHolder.uint64Holder = notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SINT32)) {
            returnHolder.sint32Holder = notNullValueAsString.toInt()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SINT64)) {
            returnHolder.sint64Holder = notNullValueAsString.toLong()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.DOUBLE)) {
            returnHolder.doubleHolder = notNullValueAsString.toDouble()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FLOAT)) {
            returnHolder.floatHolder = notNullValueAsString.toFloat()
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.BYTES)) {
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
        if (propertyDefinition.type.equals(YaormModel.ProtobufType.STRING)) {
            return holder.stringHolder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.BOOL)) {
            return holder.boolHolder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.INT32)) {
            return holder.int32Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.INT64)) {
            return holder.int64Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FIXED32)) {
            return holder.fixed32Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FIXED64)) {
            return holder.fixed64Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SFIXED32)) {
            return holder.sfixed32Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SFIXED64)) {
            return holder.sfixed64Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SINT32)) {
            return holder.sint32Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.SINT64)) {
            return holder.sint64Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.UINT32)) {
            return holder.uint32Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.UINT64)) {
            return holder.uint64Holder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.DOUBLE)) {
            return holder.doubleHolder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.FLOAT)) {
            return holder.floatHolder
        }

        if (propertyDefinition.type.equals(YaormModel.ProtobufType.BYTES)) {
            return holder.bytesHolder
        }
        return null
    }

    fun getFormattedString(value: YaormModel.Column): String {
        if (!value.hasDefinition()) {
            return Null
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.STRING)) {
            return SingleQuote + value.stringHolder.replace(SingleQuote, DoubleSingleQuote) + SingleQuote
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.BOOL)) {
            return Integer.toString(if (value.boolHolder) 1 else 0)
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.INT32)) {
            return value.int32Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.INT64)) {
            return value.int64Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.FIXED32)) {
            return value.fixed32Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.FIXED64)) {
            return value.fixed64Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.SFIXED32)) {
            return value.sfixed32Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.SFIXED64)) {
            return value.sfixed64Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.UINT32)) {
            return value.uint32Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.UINT64)) {
            return value.uint64Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.SINT32)) {
            return value.sint32Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.SINT64)) {
            return value.sint64Holder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.DOUBLE)) {
            return value.doubleHolder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.FLOAT)) {
            return value.floatHolder.toString()
        }

        if (value.definition.type.equals(YaormModel.ProtobufType.BYTES)) {
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

    fun getFirstWordInProperty(input:String):String {
        // going to lower case, just in case
        val lowerInput = this.lowercaseFirstChar(input)
        val returnString = StringBuilder()

        for (newChar in lowerInput) {
            if (newChar.isLetter()) {
                if (newChar.isLowerCase()) {
                    returnString.append(newChar)
                }
                else {
                    break
                }
            }
        }

        return returnString.toString()
    }

    fun buildIndexName(columnNames:List<String>) : String {
        return "${columnNames.sortedBy { it }.joinToString(Underscore)}${Underscore}idx"
    }

    fun getNameTypes(
            definition: YaormModel.TableDefinition,
            javaIdName: String,
            javaIdType: YaormModel.ProtobufType,
            javaTypeToSqlType: Map<YaormModel.ProtobufType, String>): List<ColumnNameTuple<String>> {
        val nameTypes = ArrayList<ColumnNameTuple<String>>()
        var foundIdColumnName = false

        // let's handle the types now
        definition
                .columnDefinitionsList
                .sortedBy { it.name }
                .forEach {
                    val columnName = it.name
                    val javaType = it.type
                    val sqlColumnName = CommonUtils.lowercaseFirstChar(it.name)
                    val javaColumnName = columnName

                    if (javaTypeToSqlType.containsKey(javaType)) {
                        val dataType = javaTypeToSqlType[javaType]
                        if (javaIdName.equals(sqlColumnName)) {
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

    fun buildWhereClause(whereClauseItem: YaormModel.WhereClause):String {
        val filterItems = StringBuilder()
        var currentWhereClauseItem: YaormModel.WhereClause? = whereClauseItem

        while (currentWhereClauseItem != null) {
            val stringValue = CommonUtils
                    .getFormattedString(currentWhereClauseItem.nameAndProperty)
            filterItems.append(
                    currentWhereClauseItem.nameAndProperty.definition.name +
                            SqlOperators.TypeToOperatorStrings[currentWhereClauseItem.operatorType] +
                            stringValue +
                            CommonUtils.Space)

            if (!currentWhereClauseItem.connectingAndOr.equals(YaormModel.WhereClause.ConnectingAndOr.NONE) &&
                currentWhereClauseItem.hasConnectingWhereClause()) {
                filterItems.append(currentWhereClauseItem.connectingAndOr.name + CommonUtils.Space)
                currentWhereClauseItem = currentWhereClauseItem.connectingWhereClause
            }
            else {
                break
            }
        }

        return filterItems.toString().trim()
    }

    fun checkIfOk(definition: YaormModel.TableDefinition):Boolean {
        return definition.columnDefinitionsList.any { it.name.equals(IdName) }
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

    fun buildMapsFromRecords(records:YaormModel.Records):List<Map<String, Any?>> {
        return records.recordsList.map { buildMapFromRecord(it) }
    }

    fun <T> getValueFromRecord(name:String, record:YaormModel.Record):T? {
        val foundColumn = record.columnsList.firstOrNull { name.equals(it.definition.name) } ?: return  null
        return getAnyObject(foundColumn) as T
    }

    fun getValueFromRecordAny(name:String, record:YaormModel.Record):Any? {
        val foundColumn = record.columnsList.firstOrNull { name.equals(it.definition.name) } ?: return  null
        return getAnyObject(foundColumn)
    }
}
