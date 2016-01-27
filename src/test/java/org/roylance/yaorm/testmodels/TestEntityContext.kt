package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.EntityContext
import org.roylance.yaorm.services.IEntityService
import java.util.*

public class TestEntityContext (
    public val anotherTestModelService: IEntityService<AnotherTestModel>,
    public val beaconBroadcastService: IEntityService<BeaconBroadcastModel>,
    migrationService: IEntityService<MigrationModel>) : EntityContext(
    Arrays.asList(anotherTestModelService, beaconBroadcastService),
    migrationService,
    "TestEntityContext") {
}
