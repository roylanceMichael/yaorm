package org.roylance.yaorm.services.sqlite

import org.junit.Test
import org.roylance.yaorm.utilities.common.IProtoContextTest
import org.roylance.yaorm.utilities.common.ProtoContextTestUtilities
import java.util.*

class SQLiteEntityProtoContextTest: SQLiteBase(), IProtoContextTest {
    @Test
    override fun simplePassThroughTest() {
        val uuid = UUID.randomUUID().toString()
        ProtoContextTestUtilities.simplePassThroughTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun migrationAddColumnTest() {
        val uuid = UUID.randomUUID().toString()
        ProtoContextTestUtilities.migrationAddColumnTest(buildEntityService(uuid), buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun migrationRemoveColumnTest() {
        val uuid = UUID.randomUUID().toString()
        ProtoContextTestUtilities.migrationRemoveColumnTest(buildEntityService(uuid), buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun migrationAddTableTest() {
        val uuid = UUID.randomUUID().toString()
        ProtoContextTestUtilities.migrationAddTableTest(buildEntityService(uuid), buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexMergeTest() {
        val uuid = UUID.randomUUID().toString()
        ProtoContextTestUtilities.complexMergeTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun complexMerge2Test() {
        val uuid = UUID.randomUUID().toString()
        ProtoContextTestUtilities.complexMerge2Test(buildEntityService(uuid), cleanup(uuid))
    }
}
