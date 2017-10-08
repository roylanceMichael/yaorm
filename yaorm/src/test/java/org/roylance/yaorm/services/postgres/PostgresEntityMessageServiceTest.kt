package org.roylance.yaorm.services.postgres

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.EntityMessageServiceTestUtilities
import org.roylance.yaorm.utilities.common.IEntityMessageServiceTest

class PostgresEntityMessageServiceTest: PostgresBase(), IEntityMessageServiceTest {
    @Test
    override fun simpleCreateTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleCreateTest(buildEntityService())
    }

    @Test
    override fun simpleLoadAndCreateTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleLoadAndCreateTest(buildEntityService())
    }

    @Test
    override fun complexLoadAndCreateTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateTest(buildEntityService())
    }

    @Test
    override fun complexLoadAndCreate2Test() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreate2Test(buildEntityService())
    }

    @Test
    override fun complexLoadAndCreateProtectedTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateProtectedTest(buildEntityService())
    }

    @Test
    override fun simpleGetTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleGetTest(buildEntityService())
    }

    @Test
    override fun bulkInsert1Test() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsert1Test(buildEntityService())
    }

    @Test
    override fun simplePassThroughWithReportTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePassThroughWithReportTest(buildEntityService())
    }

    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePassThroughTest(buildEntityService())
    }

    @Test
    override fun childAddThenDeleteTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.childAddThenDeleteTest(buildEntityService())
    }

    @Test
    override fun verifyChildSerializedProperly() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.verifyChildSerializedProperly(buildEntityService())
    }

    @Test
    override fun verifyChildChangedAfterMergeProperly() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.verifyChildChangedAfterMergeProperly(buildEntityService())
    }

    @Test
    override fun additionalAddRemoveTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.additionalAddRemoveTest(buildEntityService())
    }

    @Test
    override fun simplePersonTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePersonTest(buildEntityService())
    }

    @Test
    override fun simplePersonFriendsTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePersonFriendsTest(buildEntityService())
    }

    @Test
    override fun simpleDagTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleDagTest(buildEntityService())
    }

    @Test
    override fun moreComplexDagTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.moreComplexDagTest(buildEntityService())
    }

    @Test
    override fun simpleUserAndUserDeviceTestTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleUserAndUserDeviceTestTest(buildEntityService())
    }

    @Test
    override fun simpleIndexTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleIndexTest(buildEntityService())
    }

    @Test
    override fun bulkInsertTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsertTest(buildEntityService())
    }
}
