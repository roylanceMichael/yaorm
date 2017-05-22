package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoTest
import org.roylance.yaorm.utilities.common.ProtoTestUtilities

class SQLServerProtoTest: SQLServerBase(), IProtoTest {
    @Test
    override fun simplePassThroughTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun singleQuoteSimplePassThroughTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.singleQuoteSimplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2Test() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simplePassThrough2Test(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simplePassThroughDefinitionTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleDefinitionBuilderTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTestTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleSchemaTestTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleSchemaTablesTest(buildEntityService(), cleanup(), ConnectionUtilities.sqlServerSqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup(), ConnectionUtilities.sqlServerSqlSchema!!)
    }

    @Test
    override fun simplePassThroughEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simplePassThroughEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2EmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simplePassThrough2EmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simplePassThroughDefinitionEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleDefinitionBuilderEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleSchemaEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleSchemaTablesEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.sqlServerSqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) ProtoTestUtilities.simpleTableDefinitionEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.sqlServerSqlSchema!!)
    }
}