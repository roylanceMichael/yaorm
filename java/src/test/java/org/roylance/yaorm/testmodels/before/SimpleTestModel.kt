package org.roylance.yaorm.testmodels.before

import org.roylance.yaorm.models.IEntity
import java.util.*

class SimpleTestModel(
        override var id:String = UUID.randomUUID().toString(),
        var fName:String="",
        var lName:String="",
        var mName:String=""): IEntity {
    companion object {
        val MNameName = "mName"
    }
}