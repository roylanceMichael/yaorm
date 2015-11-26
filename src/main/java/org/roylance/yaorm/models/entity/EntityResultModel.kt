package org.roylance.yaorm.models.entity

class EntityResultModel<K>(
        var generatedKeys: List<K>?=null,
        var successful: Boolean=false)
