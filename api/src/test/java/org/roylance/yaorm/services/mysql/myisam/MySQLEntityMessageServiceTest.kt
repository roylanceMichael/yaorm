package org.roylance.yaorm.services.mysql.myisam

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.EntityMessageServiceTestUtilities
import org.roylance.yaorm.utilities.common.IEntityMessageServiceTest

class MySQLEntityMessageServiceTest: MySQLISAMBase(), IEntityMessageServiceTest {
    @Test
    override fun simpleCreateTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleCreateTest(buildEntityService(), cleanup())
    }
    @Test
    override fun simpleLoadAndCreateTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleLoadAndCreateTest(buildEntityService(), cleanup())
    }
    @Test
    override fun complexLoadAndCreateTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateTest(buildEntityService(), cleanup())
    }
    @Test
    override fun complexLoadAndCreate2Test() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreate2Test(buildEntityService(), cleanup())
    }
    @Test
    override fun complexLoadAndCreateProtectedTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateProtectedTest(buildEntityService(), cleanup())
    }
    @Test
    override fun simpleGetTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleGetTest(buildEntityService(), cleanup())
    }
    @Test
    override fun bulkInsert1Test() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsert1Test(buildEntityService(), cleanup())
    }
    @Test
    override fun simplePassThroughWithReportTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePassThroughWithReportTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        // arrange
        EntityMessageServiceTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun childAddThenDeleteTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.childAddThenDeleteTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyChildSerializedProperly() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.verifyChildSerializedProperly(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyChildChangedAfterMergeProperly() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.verifyChildChangedAfterMergeProperly(buildEntityService(), cleanup())
    }

    @Test
    override fun additionalAddRemoveTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.additionalAddRemoveTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePersonTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePersonTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePersonFriendsTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePersonFriendsTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDagTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleDagTest(buildEntityService(), cleanup())
    }

    @Test
    override fun moreComplexDagTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.moreComplexDagTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleUserAndUserDeviceTestTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleUserAndUserDeviceTestTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleIndexTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleIndexTest(buildEntityService(), cleanup())
    }

    @Test
    override fun bulkInsertTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsertTest(buildEntityService(), cleanup())
    }
}
