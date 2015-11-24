package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity

class ChildTestModel(
        override var id:Long=0,
        var name:String="",
        var rootModel:RootTestModel?=null) : IEntity<Long>
