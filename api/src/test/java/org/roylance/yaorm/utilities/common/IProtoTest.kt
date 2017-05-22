package org.roylance.yaorm.utilities.common

import org.roylance.yaorm.utilities.ICommonTest

interface IProtoTest: ICommonTest {
    fun simplePassThroughTest()
    fun singleQuoteSimplePassThroughTest()
    fun simplePassThrough2Test()
    fun verifyTypesSavedAndReturnedCorrectlyTest()
    fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest()
    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest()
    fun simplePassThroughDefinitionTest()
    fun simpleDefinitionBuilderTest()
    fun simpleSchemaTestTest()
    fun simpleSchemaTablesTest()
    fun simpleTableDefinitionTest()
    fun simplePassThroughEmptyAsNullTest()
    fun simplePassThrough2EmptyAsNullTest()
    fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest()
    fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest()
    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest()
    fun simplePassThroughDefinitionEmptyAsNullTest()
    fun simpleDefinitionBuilderEmptyAsNullTest()
    fun simpleSchemaEmptyAsNullTest()
    fun simpleSchemaTablesEmptyAsNullTest()
    fun simpleTableDefinitionEmptyAsNullTest()
}