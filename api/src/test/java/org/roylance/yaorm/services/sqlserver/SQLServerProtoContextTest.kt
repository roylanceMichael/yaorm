package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoContextTest
import org.roylance.yaorm.utilities.common.ProtoContextTestUtilities

class SQLServerProtoContextTest: SQLServerBase(), IProtoContextTest {
    @Test
    override fun simplePassThroughTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoContextTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddColumnTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoContextTestUtilities.migrationAddColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationRemoveColumnTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoContextTestUtilities.migrationRemoveColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddTableTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoContextTestUtilities.migrationAddTableTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun complexMergeTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoContextTestUtilities.complexMergeTest(buildEntityService(), cleanup())
    }

    @Test
    override fun complexMerge2Test() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoContextTestUtilities.complexMerge2Test(buildEntityService(), cleanup())
    }
}