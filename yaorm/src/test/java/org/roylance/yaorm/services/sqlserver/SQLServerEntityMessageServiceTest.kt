package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.EntityMessageServiceTestUtilities
import org.roylance.yaorm.utilities.common.IEntityMessageServiceTest
import java.util.*

class SQLServerEntityMessageServiceTest: SQLServerBase(), IEntityMessageServiceTest {
    @Test
    override fun moreComplexDagTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.moreComplexDagTest(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun simpleDagTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simpleDagTest(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun simplePersonFriendsTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simplePersonFriendsTest(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun simplePersonTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simplePersonTest(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun additionalAddRemoveTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.additionalAddRemoveTest(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun verifyChildChangedAfterMergeProperly() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.verifyChildChangedAfterMergeProperly(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun verifyChildSerializedProperly() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.verifyChildSerializedProperly(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun childAddThenDeleteTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.childAddThenDeleteTest(buildEntityService(uuid), cleanup(uuid))
    }
    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simplePassThroughTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simplePassThroughWithReportTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simplePassThroughWithReportTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleCreateTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simpleCreateTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleLoadAndCreateTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simpleLoadAndCreateTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexLoadAndCreateTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.complexLoadAndCreateTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexLoadAndCreate2Test() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.complexLoadAndCreate2Test(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexLoadAndCreateProtectedTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.complexLoadAndCreateProtectedTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleUserAndUserDeviceTestTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simpleUserAndUserDeviceTestTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleGetTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simpleGetTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleIndexTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.simpleIndexTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun bulkInsertTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.bulkInsertTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun bulkInsert1Test() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val uuid = ConnectionUtilities.buildSafeUUID()
        EntityMessageServiceTestUtilities.bulkInsert1Test(buildEntityService(uuid), cleanup(uuid))
    }
}