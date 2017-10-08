package org.roylance.yaorm.services.postgres

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoContextTest
import org.roylance.yaorm.utilities.common.ProtoContextTestUtilities

class PostgresProtoContextTest: PostgresBase(), IProtoContextTest {

    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoContextTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddColumnTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoContextTestUtilities.migrationAddColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationRemoveColumnTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoContextTestUtilities.migrationRemoveColumnTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun migrationAddTableTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoContextTestUtilities.migrationAddTableTest(buildEntityService(), buildEntityService(), cleanup())
    }

    @Test
    override fun complexMergeTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoContextTestUtilities.complexMergeTest(buildEntityService(), cleanup())
    }

    @Test
    override fun complexMerge2Test() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoContextTestUtilities.complexMerge2Test(buildEntityService(), cleanup())
    }
}
