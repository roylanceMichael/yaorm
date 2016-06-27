package org.roylance.yaorm.models

interface IEntity {
    var id:String

    companion object {
        const val IdName = "id"
    }
}
