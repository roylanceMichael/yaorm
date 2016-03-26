package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity
import java.util.*

class ChildTestModel(
        override var id:String=UUID.randomUUID().toString(),
        var name:String="",
        var commonRootModelId: String = "",
        var commonRootModel:RootTestModel?=null) : IEntity
