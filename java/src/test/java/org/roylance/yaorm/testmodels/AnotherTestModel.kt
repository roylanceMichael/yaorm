package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity

class AnotherTestModel (
    override var id:String="",
    var description:String="",
    var gram:String="") : IEntity {
    companion object {
        const val IdName = "id"
        const val DescriptionName = "description"
        const val GramName = "gram"
    }
}
