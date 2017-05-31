package org.roylance.yaorm.services.sqlite

import org.junit.Test
import org.roylance.yaorm.utilities.common.IProtoTest
import org.roylance.yaorm.utilities.common.ProtoTestUtilities
import java.util.*

class SQLiteProtoTest: SQLiteBase(), IProtoTest {
    @Test
    override fun simplePassThroughTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simplePassThroughTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun singleQuoteSimplePassThroughTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.singleQuoteSimplePassThroughTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun simplePassThrough2Test() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simplePassThrough2Test(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun simplePassThroughDefinitionTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simplePassThroughDefinitionTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun simpleDefinitionBuilderTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simpleDefinitionBuilderTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun simpleSchemaTestTest() {
        // sqlite doesn't have schemas... all just files
    }

    @Test
    override fun simpleSchemaTablesTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simpleSchemaTablesTest(buildEntityService(fileName), cleanup(fileName), fileName)
    }

    @Test
    override fun simpleTableDefinitionTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simpleTableDefinitionTest(buildEntityService(fileName), cleanup(fileName), fileName)
    }

    @Test
    override fun simplePassThroughEmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simplePassThroughEmptyAsNullTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun simplePassThrough2EmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simplePassThrough2EmptyAsNullTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun simplePassThroughDefinitionEmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simplePassThroughDefinitionEmptyAsNullTest(buildEntityService(fileName), cleanup(fileName))
    }

    @Test
    override fun simpleDefinitionBuilderEmptyAsNullTest() {
        // not used for sqlite
    }

    @Test
    override fun simpleSchemaEmptyAsNullTest() {
        // not used for sqlite
    }

    @Test
    override fun simpleSchemaTablesEmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simpleSchemaTablesEmptyAsNullTest(buildEntityService(fileName), cleanup(fileName), fileName)
    }

    @Test
    override fun simpleTableDefinitionEmptyAsNullTest() {
        val fileName = UUID.randomUUID().toString()
        ProtoTestUtilities.simpleTableDefinitionEmptyAsNullTest(buildEntityService(fileName), cleanup(fileName), fileName)
    }
}
