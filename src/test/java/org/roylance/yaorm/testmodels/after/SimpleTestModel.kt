package org.roylance.yaorm.testmodels.after

import org.roylance.yaorm.models.IEntity
import java.util.*

class SimpleTestModel(
        public override var id:String = UUID.randomUUID().toString(),
        public var fName:String="",
        public var lName:String=""):IEntity
