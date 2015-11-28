package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.EntityCollection
import org.roylance.yaorm.models.IEntity

class RootTestModel(
        override var id:Long=0,
        var name:String="",
        var commonChildTests:EntityCollection<Long, ChildTestModel>
            = EntityCollection(ChildTestModel::class.java)) : IEntity<Long>
