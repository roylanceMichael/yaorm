package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.ColumnNameTuple
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.migration.DefinitionModel
import java.util.*

object CommonSqlDataTypeUtilities {
    private val SingleQuote = "'"
    private val DoubleSingleQuote = "''"

    val Null = "null"
    val Space = " "
    val Comma = ","
    val Equals = "="
    val SemiColon = ";"
    val CarriageReturn = '\n'
    val SpacedUnion = "${CarriageReturn}union "
    val SpacedAnd = " and "
    val And = "and"
    val Or = "or"
    val Underscore = "_"
    val Is = "is"
    val LeftParen = "("
    val RightParen = ")"

    val JavaFullyQualifiedStringName: String = "String"
    val JavaObjectName: String = "java.lang.Object"
    val JavaStringName: String = "java.lang.String"
    val JavaDoubleName: String = "double"
    val JavaAltObjectName: String = "Object"
    val JavaAltDoubleName: String = "double"
    val JavaAlt1DoubleName: String = "double"
    val JavaIntegerName: String = "int"
    val JavaAltIntegerName: String = "Integer"
    val JavaAlt1IntegerName: String = "java.lang.Integer"
    val JavaLongName: String = "long"
    val JavaAltLongName: String = "Long"
    val JavaAlt1LongName: String = "java.lang.Long"
    val JavaByteName: String = "byte"
    val JavaBooleanName: String = "boolean"
    val JavaAltBooleanName: String = "Boolean"
    val JavaAlt1BooleanName: String = "java.lang.Boolean"

    val Get:String = "get"
    val Set:String = "set"
    val GetSetLength = Get.length

    fun getFormattedString(value: Any?): String {
        if (value == null) {
            return Null
        }

        if (value is Int || value is Long || value is Double) {
            return value.toString()
        }

        if (value is String) {
            return SingleQuote + value.replace(SingleQuote, DoubleSingleQuote) + SingleQuote
        }

        if (value is Boolean) {
            return Integer.toString(if (value) 1 else 0)
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

        val firstChar = input.get(0).toLowerCase()
        return firstChar + input.substring(1)
    }

    fun getFirstWordInProperty(input:String):String {
        // going to lower case, just in case
        val lowerInput = this.lowercaseFirstChar(input)
        var returnString = StringBuilder()

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

    fun <T: IEntity> getNameTypes(
            classModel: Class<T>,
            javaIdName: String,
            javaTypeToSqlType: Map<String, String>): List<ColumnNameTuple<String>> {
        val nameTypes = ArrayList<ColumnNameTuple<String>>()
        var foundIdColumnName = false

        val propertyNames = classModel
                .methods
                .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                .map { it.name.substring(CommonSqlDataTypeUtilities.GetSetLength) }
                .toHashSet()

        // let's handle the types now
        classModel
                .methods
                .filter {
                    it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            propertyNames.contains(
                                    it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
                }
                .sortedBy { it.name }
                .forEach {
                    val columnName = it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)
                    val javaType = it.returnType.name

                    if (javaTypeToSqlType.containsKey(javaType)) {
                        val sqlColumnName = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
                        val javaColumnName = columnName
                        val dataType = javaTypeToSqlType[javaType]
                        if (javaIdName.equals(sqlColumnName)) {
                            foundIdColumnName = true
                        }
                        nameTypes.add(ColumnNameTuple(sqlColumnName, javaColumnName, dataType!!))
                    }
                    else {
                        val foundTuple = EntityUtils.getEntityTuple(it, javaTypeToSqlType)
                        if (foundTuple != null) {
                            nameTypes.add(foundTuple)
                        }
                    }
                }

        if (!foundIdColumnName) {
            return ArrayList()
        }

        return nameTypes
    }

    fun getNameTypes(
            definition: DefinitionModel,
            javaIdName: String,
            javaIdType: String,
            javaTypeToSqlType: Map<String, String>): List<ColumnNameTuple<String>> {
        val nameTypes = ArrayList<ColumnNameTuple<String>>()
        var foundIdColumnName = false

        // let's handle the types now
        definition
                .properties
                .sortedBy { it.name }
                .forEach {
                    val columnName = it.name
                    val javaType = it.type
                    val sqlColumnName = CommonSqlDataTypeUtilities.lowercaseFirstChar(it.name)
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

    fun buildWhereClause(whereClauseItem: WhereClauseItem):String {
        val filterItems = StringBuilder()
        var currentWhereClauseItem: WhereClauseItem? = whereClauseItem

        while (currentWhereClauseItem != null) {
            val stringValue = CommonSqlDataTypeUtilities
                    .getFormattedString(currentWhereClauseItem.rightSide)
            filterItems.append(
                    currentWhereClauseItem.leftSide +
                            currentWhereClauseItem.operator +
                            stringValue +
                            CommonSqlDataTypeUtilities.Space)

            if (currentWhereClauseItem.connectingAndOr != null) {
                filterItems.append(currentWhereClauseItem.connectingAndOr + CommonSqlDataTypeUtilities.Space)
            }

            currentWhereClauseItem = currentWhereClauseItem.connectingWhereClause
        }

        return filterItems.toString().trim()
    }
}
