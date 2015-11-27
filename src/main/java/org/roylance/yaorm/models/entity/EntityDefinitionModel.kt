package org.roylance.yaorm.models.entity

import java.lang.reflect.Method

class EntityDefinitionModel<T>(
        val propertyName: String,
        val getMethod: Method,
        val setMethod: Method,
        val entityDefinition: Class<T>,
        val type: String) {
    companion object {
        val Single = "single"
        val List = "list"
    }
}
