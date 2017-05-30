package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities
import java.util.*

class SQLServerNestedEnumTest: SQLServerBase(), INestedEnumTest {
    @Test
    override fun simplePassThroughExecutionsTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        val sourceConnection = SQLServerConnectionSourceFactory(
                ConnectionUtilities.sqlServerSqlHost!!,
                ConnectionUtilities.sqlServerSqlSchema!!,
                ConnectionUtilities.sqlServerSqlUserName!!,
                ConnectionUtilities.sqlServerSqlPassword!!)
        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false,
                true)
        val sqlGeneratorService = SQLServerGeneratorService()
        val entityService = EntityService(granularDatabaseService, sqlGeneratorService)
        NestedEnumTestUtilities.simplePassThroughExecutionsTest(entityService, cleanup())
    }

    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        NestedEnumTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughTest2() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        NestedEnumTestUtilities.simplePassThroughTest2(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleTablesTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        NestedEnumTestUtilities.simpleTablesTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleTableDefinitionTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        NestedEnumTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleTableDefinitionNullableTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        NestedEnumTestUtilities.simpleTableDefinitionNullableTest(buildEntityService(), cleanup())
    }
}