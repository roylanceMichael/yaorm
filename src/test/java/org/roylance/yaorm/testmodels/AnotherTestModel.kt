package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity

class AnotherTestModel (
    override var id:String="",
    var description:String="",
    var gram:String="") : IEntity {
    companion object {
        public val IdName = "id"
        public val DescriptionName = "description"
        public val GramName = "gram"
    }
}
