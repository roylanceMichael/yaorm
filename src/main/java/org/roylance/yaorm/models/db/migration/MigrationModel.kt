package org.roylance.yaorm.models.db.migration

import org.roylance.yaorm.models.IEntity
import java.util.*

class MigrationModel (
        override var id:String=UUID.randomUUID().toString(),
        var contextName: String="",
        var modelDefinitionJson: String="",
        var insertDate: Long=Date().time) : IEntity<String> {
    companion object {
        val ContextName = "contextName"
    }
}
