package org.roylance.yaorm.services.mysql.myisam

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoContextTest
import org.roylance.yaorm.utilities.common.ProtoContextTestUtilities

class MySQLProtoContextTest: MySQLISAMBase(), IProtoContextTest {
    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoContextTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddColumnTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoContextTestUtilities.migrationAddColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationRemoveColumnTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoContextTestUtilities.migrationRemoveColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddTableTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoContextTestUtilities.migrationAddTableTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun complexMergeTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoContextTestUtilities.complexMergeTest(buildEntityService(), cleanup())
    }

    @Test
    override fun complexMerge2Test() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoContextTestUtilities.complexMerge2Test(buildEntityService(), cleanup())
    }
}
