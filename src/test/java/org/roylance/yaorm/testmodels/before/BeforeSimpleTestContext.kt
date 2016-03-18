package org.roylance.yaorm.testmodels.before

import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.EntityContext
import org.roylance.yaorm.services.IEntityService
import java.util.*

class BeforeSimpleTestContext(
        val simpleTestService: IEntityService<SimpleTestModel>,
        migrationService: IEntityService<MigrationModel>
) : EntityContext(Arrays.asList(simpleTestService), migrationService, "SimpleTest")
