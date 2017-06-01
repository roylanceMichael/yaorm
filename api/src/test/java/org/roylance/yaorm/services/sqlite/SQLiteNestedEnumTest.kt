package org.roylance.yaorm.services.sqlite

import org.junit.Test
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities
import java.util.*

class SQLiteNestedEnumTest: SQLiteBase(), INestedEnumTest {
    @Test
    override fun simplePassThroughExecutionsTest() {
        val uuid = UUID.randomUUID().toString()
        val sourceConnection = SQLiteConnectionSourceFactory(uuid)
        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false,
                true)
        val sqliteGeneratorService = SQLiteGeneratorService()
        val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
        NestedEnumTestUtilities.simplePassThroughExecutionsTest(entityService, cleanup(uuid))
    }

    @Test
    override fun simplePassThroughTest() {
        val uuid = UUID.randomUUID().toString()
        NestedEnumTestUtilities.simplePassThroughTest(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simplePassThroughTest2() {
        val uuid = UUID.randomUUID().toString()
        NestedEnumTestUtilities.simplePassThroughTest2(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun simpleTablesTest() {
        val uuid = UUID.randomUUID().toString()
        NestedEnumTestUtilities.simpleTablesTest(buildEntityService(uuid), cleanup(uuid), uuid)
    }

    @Test
    override fun simpleTableDefinitionTest() {
        val uuid = UUID.randomUUID().toString()
        NestedEnumTestUtilities.simpleTableDefinitionTest(buildEntityService(uuid), cleanup(uuid), uuid)
    }

    @Test
    override fun simpleTableDefinitionNullableTest() {
        val uuid = UUID.randomUUID().toString()
        NestedEnumTestUtilities.simpleTableDefinitionNullableTest(buildEntityService(uuid), cleanup(uuid), uuid)
    }
}