package org.roylance.yaorm.models

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.roylance.yaorm.YaormModel

class TypeModel(
        val columnName: String,
        val index: Int) {
    private var boolCount: Int = 0
    private var doubleCount: Int = 0
    private var intCount: Int = 0
    private var strCount: Int = 0
    private var maxStrSize: Int = 0

    fun addTest(item: String) {
        if (isInt(item)) {
            this.intCount++
        }
        else if (isDouble(item)) {
            this.doubleCount++
        }
        else if (isBoolean(item)) {
            this.boolCount++
        }
        else {
            this.strCount++
            if (item.length > this.maxStrSize) {
                this.maxStrSize = item.length
            }
        }
    }

    fun buildColumnDefinition(): YaormModel.ColumnDefinition {
        return YaormModel.ColumnDefinition.newBuilder()
            .setName(this.columnName)
            .setOrder(this.index)
            .setType(this.calculateType())
            .build()
    }

    fun calculateType(): YaormModel.ProtobufType {
        if (this.strCount > 0) {
            return YaormModel.ProtobufType.STRING
        }
        else if (this.doubleCount > 0) {
            return YaormModel.ProtobufType.DOUBLE
        }
        else if (this.intCount > 0) {
            return YaormModel.ProtobufType.INT64
        }
        return YaormModel.ProtobufType.BOOL
    }

    companion object {
        private const val LowercaseTrue = "true"
        private const val LowercaseFalse = "false"

        fun isBoolean(item: String): Boolean {
            val lowercaseItem = item.toLowerCase()
            return lowercaseItem == LowercaseTrue || lowercaseItem == LowercaseFalse
        }

        fun isInt(item: String): Boolean {
            if (item.isEmpty()) {
                return true
            }
            return StringUtils.isNumeric(item)
        }

        fun isDouble(item: String): Boolean {
            return NumberUtils.isNumber(item)
        }
    }
}