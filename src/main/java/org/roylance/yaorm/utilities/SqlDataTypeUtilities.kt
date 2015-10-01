package org.roylance.yaorm.utilities

public object SqlDataTypeUtilities {
    private val Set = "set"

    private val Null = "null"

    private val SingleQuote = "'"

    private val DoubleSingleQuote = "''"

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
