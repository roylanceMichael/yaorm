package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoContextTest
import org.roylance.yaorm.utilities.common.ProtoContextTestUtilities
import java.util.*

class SQLServerProtoContextTest: SQLServerBase(), IProtoContextTest {
    @Test
    override fun simplePassThroughTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoContextTestUtilities.simplePassThroughTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun migrationAddColumnTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoContextTestUtilities.migrationAddColumnTest(buildEntityService(uuid), buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun migrationRemoveColumnTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoContextTestUtilities.migrationRemoveColumnTest(buildEntityService(uuid), buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun migrationAddTableTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoContextTestUtilities.migrationAddTableTest(buildEntityService(uuid), buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun complexMergeTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoContextTestUtilities.complexMergeTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun complexMerge2Test() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoContextTestUtilities.complexMerge2Test(buildEntityService(uuid), cleanup(uuid))
        }
    }
}