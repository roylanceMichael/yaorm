package org.roylance.yaorm.models.db.migration

import org.roylance.yaorm.models.IEntity

public class MigrationModel (
        public override var id:Long=0L,
        public var contextName: String="",
        public var modelDefinitionJson: String="") : IEntity<Long> {
    companion object {
        public val ContextName = "contextName"
    }
}
