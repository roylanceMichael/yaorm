package org.roylance.yaorm.services.postgres

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoTest
import org.roylance.yaorm.utilities.common.ProtoTestUtilities

class PostgresProtoTest: PostgresBase(), IProtoTest {
    @Test
    override fun simplePassThroughTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun singleQuoteSimplePassThroughTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.singleQuoteSimplePassThroughTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2Test() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simplePassThrough2Test(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughDefinitionTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleDefinitionBuilderTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTestTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaTestTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaTablesTest(buildEntityService(), cleanup(), ConnectionUtilities.postgresDatabase!!)
    }

    @Test
    override fun simpleTableDefinitionTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup(), ConnectionUtilities.postgresDatabase!!)
    }

    @Test
    override fun simplePassThroughEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThrough2EmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simplePassThrough2EmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simplePassThroughDefinitionEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simplePassThroughDefinitionEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleDefinitionBuilderEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleDefinitionBuilderEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaEmptyAsNullTest(buildEntityService(), cleanup())
    }

    @Test
    override fun simpleSchemaTablesEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleSchemaTablesEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.postgresDatabase!!)
    }

    @Test
    override fun simpleTableDefinitionEmptyAsNullTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        ProtoTestUtilities.simpleTableDefinitionEmptyAsNullTest(buildEntityService(), cleanup(), ConnectionUtilities.postgresDatabase!!)
    }
}
