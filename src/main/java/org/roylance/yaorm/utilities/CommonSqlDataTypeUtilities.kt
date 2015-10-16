package org.roylance.yaorm.utilities

public object CommonSqlDataTypeUtilities {
    private val Null = "null"
    private val SingleQuote = "'"
    private val DoubleSingleQuote = "''"

    public val Space = " "
    public val Comma = ","
    public val Equals = "="
    public val SemiColon = ";"
    public val CarriageReturn = '\n'
    public val SpacedUnion = "${CarriageReturn}union "
    public val SpacedAnd = " and "
    public val And = "and"
    public val Or = "or"


    public val JavaFullyQualifiedStringName: String = "String"
    public val JavaObjectName: String = "java.lang.Object"
    public val JavaStringName: String = "java.lang.String"
    public val JavaDoubleName: String = "double"
    public val JavaAltDoubleName: String = "double"
    public val JavaAlt1DoubleName: String = "double"
    public val JavaIntegerName: String = "int"
    public val JavaAltIntegerName: String = "Integer"
    public val JavaAlt1IntegerName: String = "java.lang.Integer"
    public val JavaLongName: String = "long"
    public val JavaAltLongName: String = "Long"
    public val JavaAlt1LongName: String = "java.lang.Long"
    public val JavaByteName: String = "byte"
    public val JavaBooleanName: String = "boolean"
    public val JavaAltBooleanName: String = "Boolean"
    public val JavaAlt1BooleanName: String = "java.lang.Boolean"

    public val Get:String = "get"
    public val Set:String = "set"
    public val GetSetLength = Get.length()

    public fun getFormattedString(value: Any?): String {
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

    public fun lowercaseFirstChar(input:String):String {
        if (input.length() == 0) {
            return input
        }

        if (input.length() == 1) {
            return input.toLowerCase()
        }

        val firstChar = input.get(0).toLowerCase()
        return firstChar + input.substring(1)
    }

    public fun convertPropertyToSetterName(propertyName: String): String {
        val firstChar = propertyName.get(0).toUpperCase()
        return Set + firstChar + propertyName.substring(1)
    }
}
