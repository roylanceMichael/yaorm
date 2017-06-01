package org.roylance.yaorm.services.mysql.normal

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.ComplexModel
import org.roylance.yaorm.NestedEnumTest
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.EntityProtoContext
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.mysql.MySQLConnectionSourceFactory
import org.roylance.yaorm.services.mysql.MySQLGeneratorService
import org.roylance.yaorm.utilities.ComplexModelBuilder
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.NestedEnumGMBuilder
import org.roylance.yaorm.utilities.TestBase64Service
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities
import java.util.*

class MySQLNestedEnumTest: MySQLBase(), INestedEnumTest {
    @Test
    override fun simplePassThroughExecutionsTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ConnectionUtilities.getMySQLConnectionInfo()
        val sourceConnection = MySQLConnectionSourceFactory(
                ConnectionUtilities.mysqlHost!!,
                ConnectionUtilities.mysqlSchema!!,
                ConnectionUtilities.mysqlUserName!!,
                ConnectionUtilities.mysqlPassword!!)

        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false,
                true)
        val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
        val entityService = EntityService(granularDatabaseService, mySqlGeneratorService)
        NestedEnumTestUtilities.simplePassThroughExecutionsTest(entityService, cleanup())
    }

    @Test
    override fun simpleTablesTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        NestedEnumTestUtilities.simpleTablesTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        NestedEnumTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionNullableTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        NestedEnumTestUtilities.simpleTableDefinitionNullableTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        NestedEnumTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughTest2() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        NestedEnumTestUtilities.simplePassThroughTest2(buildEntityService(), cleanup())
    }
}