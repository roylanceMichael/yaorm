package org.roylance.yaorm.models

import org.roylance.yaorm.utilities.YaormUtils

interface IEntity {
    var id:String

    companion object {
        const val IdName = YaormUtils.IdName
    }
}
