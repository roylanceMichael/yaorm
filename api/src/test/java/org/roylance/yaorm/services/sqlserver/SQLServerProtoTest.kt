package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IProtoTest
import org.roylance.yaorm.utilities.common.ProtoTestUtilities

class SQLServerProtoTest: SQLServerBase(), IProtoTest {
    @Test
    override fun simplePassThroughTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simplePassThroughTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun singleQuoteSimplePassThroughTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.singleQuoteSimplePassThroughTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simplePassThrough2Test() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simplePassThrough2Test(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simplePassThroughDefinitionTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simplePassThroughDefinitionTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simpleDefinitionBuilderTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleDefinitionBuilderTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simpleSchemaTestTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleSchemaTestTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simpleSchemaTablesTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleSchemaTablesTest(buildEntityService(uuid), cleanup(uuid), uuid)
        }
    }

    @Test
    override fun simpleTableDefinitionTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleTableDefinitionTest(buildEntityService(uuid), cleanup(uuid), uuid)
        }
    }

    @Test
    override fun simplePassThroughEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simplePassThroughEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simplePassThrough2EmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simplePassThrough2EmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simplePassThroughDefinitionEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simplePassThroughDefinitionEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simpleDefinitionBuilderEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleDefinitionBuilderEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simpleSchemaEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleSchemaEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid))
        }
    }

    @Test
    override fun simpleSchemaTablesEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleSchemaTablesEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid), uuid)
        }
    }

    @Test
    override fun simpleTableDefinitionEmptyAsNullTest() {
        if (ConnectionUtilities.runSQLServerTests()) {
            val uuid = ConnectionUtilities.buildSafeUUID()
            ProtoTestUtilities.simpleTableDefinitionEmptyAsNullTest(buildEntityService(uuid), cleanup(uuid), uuid)
        }
    }
}