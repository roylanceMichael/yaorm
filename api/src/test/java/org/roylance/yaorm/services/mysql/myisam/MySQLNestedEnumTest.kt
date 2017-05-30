package org.roylance.yaorm.services.mysql.myisam

import org.junit.Test
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.mysql.MySQLConnectionSourceFactory
import org.roylance.yaorm.services.mysql.MySQLGeneratorService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities

class MySQLNestedEnumTest: MySQLISAMBase(), INestedEnumTest {
    @Test
    override fun simplePassThroughExecutionsTest() {
        ConnectionUtilities.getMySQLConnectionInfo()
        val sourceConnection = MySQLConnectionSourceFactory(
                ConnectionUtilities.mysqlHost!!,
                ConnectionUtilities.mysqlSchema!!,
                ConnectionUtilities.mysqlUserName!!,
                ConnectionUtilities.mysqlPassword!!)

        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false)
        val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema, useMyISAM = true)
        val entityService = EntityService(granularDatabaseService, mySqlGeneratorService)
        NestedEnumTestUtilities.simplePassThroughExecutionsTest(entityService, cleanup())
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

    @Test
    override fun simplePassThroughTest() {
        NestedEnumTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughTest2() {
        NestedEnumTestUtilities.simplePassThroughTest2(buildEntityService(), cleanup())
    }
}