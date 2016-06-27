package org.roylance.yaorm.models.entity

import java.lang.reflect.Method

class EntityDefinitionModel<T>(
        val commonPropertyName: String,
        val propertyName: String,
        val getMethod: Method,
        val setMethod: Method,
        val entityDefinition: Class<T>,
        val type: String) {
    companion object {
        const val Single = "single"
        const val List = "list"
    }
}
