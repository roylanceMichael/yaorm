package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity

class RootTestModel(
        override var id:Long=0,
        var name:String="",
        var childTests:List<ChildTestModel>?=null) : IEntity<Long>
