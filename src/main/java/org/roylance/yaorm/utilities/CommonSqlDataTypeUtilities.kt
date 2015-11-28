package org.roylance.yaorm.utilities

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

    fun convertPropertyToSetterName(propertyName: String): String {
        val firstChar = propertyName.get(0).toUpperCase()
        return Set + firstChar + propertyName.substring(1)
    }

    fun buildIndexName(columnNames:List<String>) : String {
        return "${columnNames.sortedBy { it }.joinToString(Underscore)}${Underscore}idx"
    }
}
