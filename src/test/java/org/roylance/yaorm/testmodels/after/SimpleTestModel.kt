package org.roylance.yaorm.testmodels.after

import org.roylance.yaorm.models.IEntity

class SimpleTestModel(
    public override var id:Int=0,
    public var fName:String="",
    public var lName:String=""):IEntity<Int>
