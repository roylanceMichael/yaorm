package org.roylance.yaorm.utilities.common

interface IEntityMessageServiceTest {
    fun simpleCreateTest()
    fun simpleLoadAndCreateTest()
    fun complexLoadAndCreateTest()
    fun complexLoadAndCreate2Test()
    fun complexLoadAndCreateProtectedTest()
    fun simpleUserAndUserDeviceTestTest()
    fun simpleGetTest()
    fun simpleIndexTest()
    fun bulkInsertTest()
    fun bulkInsert1Test()
    fun simplePassThroughWithReportTest()
    fun moreComplexDagTest()
    fun simpleDagTest()
    fun simplePersonFriendsTest()
    fun simplePersonTest()
    fun additionalAddRemoveTest()
    fun verifyChildChangedAfterMergeProperly()
    fun verifyChildSerializedProperly()
    fun childAddThenDeleteTest()
    fun simplePassThroughTest()
}