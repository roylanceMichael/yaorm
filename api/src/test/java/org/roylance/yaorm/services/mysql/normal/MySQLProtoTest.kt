package org.roylance.yaorm.services.mysql.normal

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoTest
import org.roylance.yaorm.utilities.common.ProtoTestUtilities

class MySQLProtoTest: MySQLBase(), IProtoTest {
    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun singleQuoteSimplePassThroughTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.singleQuoteSimplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2Test() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simplePassThrough2Test(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughDefinitionTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleDefinitionBuilderTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTestTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaTestTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaTablesTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simplePassThroughEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2EmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simplePassThrough2EmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughDefinitionEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleDefinitionBuilderEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaTablesEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionEmptyAsNullTest() {
        if (!ConnectionUtilities.runMySQLTests()) {
            return
        }
        ProtoTestUtilities.simpleTableDefinitionEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }
}
