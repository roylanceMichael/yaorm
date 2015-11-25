package org.roylance.yaorm.models

class ColumnNameTuple<T>(
        val sqlColumnName: T,
        val javaFieldName: T,
        val dataType: T,
        val isForeignKey: Boolean=false)