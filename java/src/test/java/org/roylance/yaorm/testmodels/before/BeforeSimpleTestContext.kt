package org.roylance.yaorm.testmodels.before

import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.entity.EntityContext
import org.roylance.yaorm.services.entity.IEntityService
import java.util.*

class BeforeSimpleTestContext(
        val simpleTestService: IEntityService<SimpleTestModel>,
        migrationService: IEntityService<MigrationModel>
) : EntityContext(Arrays.asList(simpleTestService), migrationService, "SimpleTest")
