package org.roylance.yaorm.testmodels.before

import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.EntityContext
import org.roylance.yaorm.services.IEntityService
import java.util.*

class BeforeSimpleTestContext(
        public val simpleTestService: IEntityService<Int, SimpleTestModel>,
        migrationService: IEntityService<String, MigrationModel>
) : EntityContext(Arrays.asList(simpleTestService), migrationService, "SimpleTest")
