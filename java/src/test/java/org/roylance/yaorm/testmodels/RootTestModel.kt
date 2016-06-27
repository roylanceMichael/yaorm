package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.EntityCollection
import org.roylance.yaorm.models.IEntity
import java.util.*

class RootTestModel(
        override var id:String=UUID.randomUUID().toString(),
        var name:String="",
        var commonChildTests:EntityCollection<ChildTestModel>
            = EntityCollection(ChildTestModel::class.java)) : IEntity