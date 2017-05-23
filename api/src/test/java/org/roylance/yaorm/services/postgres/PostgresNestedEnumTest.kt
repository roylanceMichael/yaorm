package org.roylance.yaorm.services.postgres

import org.junit.Test
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities

class PostgresNestedEnumTest: PostgresBase(), INestedEnumTest {
    @Test
    override fun simplePassThroughExecutionsTest() {
        ConnectionUtilities.getPostgresConnectionInfo()
        val sourceConnection = PostgresConnectionSourceFactory(
                ConnectionUtilities.postgresHost!!,
                ConnectionUtilities.postgresPort!!,
                ConnectionUtilities.postgresDatabase!!,
                ConnectionUtilities.postgresUserName!!,
                ConnectionUtilities.postgresPassword!!,
                false)

        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false,
                true)
        val generatorService = PostgresGeneratorService()
        val entityService = EntityService(granularDatabaseService, generatorService)
        NestedEnumTestUtilities.simplePassThroughExecutionsTest(entityService, cleanup())
    }

    @Test
    override fun simplePassThroughTest() {
        NestedEnumTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughTest2() {
        NestedEnumTestUtilities.simplePassThroughTest2(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleTablesTest() {
        NestedEnumTestUtilities.simpleTablesTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleTableDefinitionTest() {
        NestedEnumTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleTableDefinitionNullableTest() {
        NestedEnumTestUtilities.simpleTableDefinitionNullableTest(buildEntityService(), cleanup())
    }
}