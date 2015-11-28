package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.EntityContext
import org.roylance.yaorm.services.IEntityService
import java.util.*

class ForeignEntityContext (
        val rootTestService: IEntityService<Long, RootTestModel>,
        val childTestService: IEntityService<Long, ChildTestModel>,
        migrationService: IEntityService<Long, MigrationModel>)
: EntityContext(
        Arrays.asList(rootTestService, childTestService),
        migrationService,
        "ForeignEntityContext")









