package org.roylance.yaorm.testmodels.before

import org.roylance.yaorm.models.IEntity
import java.util.*

class SimpleTestModel(
        public override var id:String = UUID.randomUUID().toString(),
        public var fName:String="",
        public var lName:String="",
        public var mName:String=""): IEntity {
    companion object {
        public val MNameName = "mName"
    }
}