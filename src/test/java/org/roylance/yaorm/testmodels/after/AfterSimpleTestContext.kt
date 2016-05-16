package org.roylance.yaorm.testmodels.after

import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.entity.EntityContext
import org.roylance.yaorm.services.entity.IEntityService
import java.util.*

class AfterSimpleTestContext(
        val simpleTestService: IEntityService<SimpleTestModel>,
        migrationService: IEntityService<MigrationModel>
) : EntityContext(Arrays.asList(simpleTestService), migrationService, "SimpleTest")
