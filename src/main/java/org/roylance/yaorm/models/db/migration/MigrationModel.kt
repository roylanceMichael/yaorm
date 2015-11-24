package org.roylance.yaorm.models.db.migration

import org.roylance.yaorm.models.IEntity

class MigrationModel (
        override var id:Long=0L,
        var contextName: String="",
        var modelDefinitionJson: String="") : IEntity<Long> {
    companion object {
        val ContextName = "contextName"
    }
}
