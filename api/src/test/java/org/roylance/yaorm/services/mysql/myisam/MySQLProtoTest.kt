package org.roylance.yaorm.services.mysql.myisam

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoTest
import org.roylance.yaorm.utilities.common.ProtoTestUtilities

class MySQLProtoTest: MySQLISAMBase(), IProtoTest {
    @Test
    override fun simplePassThroughTest() {
        ProtoTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun singleQuoteSimplePassThroughTest() {
        ProtoTestUtilities.singleQuoteSimplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2Test() {
        ProtoTestUtilities.simplePassThrough2Test(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyTest() {
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionTest() {
        ProtoTestUtilities.simplePassThroughDefinitionTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderTest() {
        ProtoTestUtilities.simpleDefinitionBuilderTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTestTest() {
        ProtoTestUtilities.simpleSchemaTestTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesTest() {
        ProtoTestUtilities.simpleSchemaTablesTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionTest() {
        ProtoTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simplePassThroughEmptyAsNullTest() {
        ProtoTestUtilities.simplePassThroughEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2EmptyAsNullTest() {
        ProtoTestUtilities.simplePassThrough2EmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest() {
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionEmptyAsNullTest() {
        ProtoTestUtilities.simplePassThroughDefinitionEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderEmptyAsNullTest() {
        ProtoTestUtilities.simpleDefinitionBuilderEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaEmptyAsNullTest() {
        ProtoTestUtilities.simpleSchemaEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesEmptyAsNullTest() {
        ProtoTestUtilities.simpleSchemaTablesEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }

    @Test
    override fun simpleTableDefinitionEmptyAsNullTest() {
        ProtoTestUtilities.simpleTableDefinitionEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.mysqlSchema!!)
    }
}
