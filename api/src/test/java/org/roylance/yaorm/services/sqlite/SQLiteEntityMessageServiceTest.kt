package org.roylance.yaorm.services.sqlite

import org.junit.Test
import org.roylance.yaorm.utilities.common.EntityMessageServiceTestUtilities
import org.roylance.yaorm.utilities.common.IEntityMessageServiceTest
import java.util.*

class SQLiteEntityMessageServiceTest: SQLiteBase(), IEntityMessageServiceTest {
    @Test
    override fun simplePassThroughWithReportTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.simplePassThroughWithReportTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleCreateTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.simpleCreateTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleLoadAndCreateTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.simpleLoadAndCreateTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexLoadAndCreateTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.complexLoadAndCreateTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexLoadAndCreate2Test() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.complexLoadAndCreate2Test(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexLoadAndCreateProtectedTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.complexLoadAndCreateProtectedTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleUserAndUserDeviceTestTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.simpleUserAndUserDeviceTestTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleGetTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.simpleGetTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleIndexTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.simpleIndexTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun bulkInsertTest() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.bulkInsertTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun bulkInsert1Test() {
        val uuid = UUID.randomUUID().toString()
        EntityMessageServiceTestUtilities.bulkInsert1Test(buildEntityService(uuid), cleanup(uuid))
    }
}
