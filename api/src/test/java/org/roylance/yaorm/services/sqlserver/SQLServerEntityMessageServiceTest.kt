package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.EntityMessageServiceTestUtilities
import org.roylance.yaorm.utilities.common.IEntityMessageServiceTest

class SQLServerEntityMessageServiceTest: SQLServerBase(), IEntityMessageServiceTest {
    @Test
    override fun moreComplexDagTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.moreComplexDagTest(buildEntityService())
    }
    @Test
    override fun simpleDagTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleDagTest(buildEntityService())
    }
    @Test
    override fun simplePersonFriendsTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePersonFriendsTest(buildEntityService())
    }
    @Test
    override fun simplePersonTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePersonTest(buildEntityService())
    }
    @Test
    override fun additionalAddRemoveTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.additionalAddRemoveTest(buildEntityService())
    }
    @Test
    override fun verifyChildChangedAfterMergeProperly() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.verifyChildChangedAfterMergeProperly(buildEntityService())
    }
    @Test
    override fun verifyChildSerializedProperly() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.verifyChildSerializedProperly(buildEntityService())
    }
    @Test
    override fun childAddThenDeleteTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.childAddThenDeleteTest(buildEntityService())
    }
    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePassThroughTest(buildEntityService())
    }

    @Test
    override fun simplePassThroughWithReportTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePassThroughWithReportTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleCreateTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleCreateTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleLoadAndCreateTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleLoadAndCreateTest(buildEntityService(), cleanup())
    }

    @Test
    override fun complexLoadAndCreateTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateTest(buildEntityService(), cleanup())
    }

    @Test
    override fun complexLoadAndCreate2Test() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreate2Test(buildEntityService(), cleanup())
    }

    @Test
    override fun complexLoadAndCreateProtectedTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateProtectedTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleUserAndUserDeviceTestTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleUserAndUserDeviceTestTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleGetTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleGetTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleIndexTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleIndexTest(buildEntityService(), cleanup())
    }

    @Test
    override fun bulkInsertTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsertTest(buildEntityService(), cleanup())
    }

    @Test
    override fun bulkInsert1Test() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsert1Test(buildEntityService(), cleanup())
    }
}