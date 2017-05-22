package org.roylance.yaorm.services.mysql.normal

import org.junit.Test
import org.roylance.yaorm.utilities.common.IProtoContextTest
import org.roylance.yaorm.utilities.common.ProtoContextTestUtilities

class MySQLProtoContextTest: MySQLBase(), IProtoContextTest {
    @Test
    override fun simplePassThroughTest() {
        ProtoContextTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddColumnTest() {
        ProtoContextTestUtilities.migrationAddColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationRemoveColumnTest() {
        ProtoContextTestUtilities.migrationRemoveColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddTableTest() {
        ProtoContextTestUtilities.migrationAddTableTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun complexMergeTest() {
        ProtoContextTestUtilities.complexMergeTest(buildEntityService(), cleanup())
    }

    @Test
    override fun complexMerge2Test() {
        ProtoContextTestUtilities.complexMerge2Test(buildEntityService(), cleanup())
    }
}
