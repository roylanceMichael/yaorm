package org.roylance.yaorm.models.migration

public class DefinitionModel(
    public var name:String,
    public var properties:List<PropertyDefinitionModel>,
    public var indexModel:IndexModel?)

