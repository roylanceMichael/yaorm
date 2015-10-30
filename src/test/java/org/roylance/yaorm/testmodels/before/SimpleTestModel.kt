package org.roylance.yaorm.testmodels.before

import org.roylance.yaorm.models.IEntity

class SimpleTestModel(
        public override var id:Int=0,
        public var fName:String="",
        public var lName:String="",
        public var mName:String=""): IEntity<Int> {
    companion object {
        public val MNameName = "mName"
    }
}