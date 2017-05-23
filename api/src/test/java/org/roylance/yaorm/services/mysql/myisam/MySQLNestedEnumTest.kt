package org.roylance.yaorm.services.mysql.myisam

import org.junit.Test
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities

class MySQLNestedEnumTest: MySQLISAMBase(), INestedEnumTest {
    @Test
    override fun simplePassThroughExecutionsTest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    override fun simpleTablesTest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    override fun simpleTableDefinitionTest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    override fun simpleTableDefinitionNullableTest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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