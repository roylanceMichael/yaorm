package org.roylance.yaorm.models

import org.roylance.yaorm.utilities.CommonUtils

interface IEntity {
    var id:String

    companion object {
        const val IdName = CommonUtils.IdName
    }
}
