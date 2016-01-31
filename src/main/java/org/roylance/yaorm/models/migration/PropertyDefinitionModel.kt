package org.roylance.yaorm.models.migration

class PropertyDefinitionModel(
        var name:String,
        var type:String,
        var foreignCollection:Boolean = false)
