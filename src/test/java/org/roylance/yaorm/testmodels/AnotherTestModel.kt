package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity

class AnotherTestModel (
    override var id:String="",
    var description:String="",
    var gram:String="") : IEntity {
    companion object {
        val IdName = "id"
        val DescriptionName = "description"
        val GramName = "gram"
    }
}
