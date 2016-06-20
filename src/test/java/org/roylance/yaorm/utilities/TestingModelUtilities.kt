package org.roylance.yaorm.utilities

import com.google.protobuf.ByteString
import org.roylance.yaorm.TestingModel
import java.util.*

object TestingModelUtilities {
    const val SimpleInsertTestDisplay = "random display"
    const val SimpleTestInt32 = 1
    const val SimpleTestInt64 = 2L
    const val SimpleTestUint32 = 3
    const val SimpleTestUint64 = 4L
    const val SimpleTestSint32 = 5
    const val SimpleTestSint64 = 6L
    const val SimpleTestFixed32 = 7
    const val SimpleTestFixed64 = 8L
    const val SimpleTestSfixed32 = 9
    const val SimpleTestSfixed64 = 10L
    const val SimpleTestBool = true
    const val SimpleTestDouble = 11.0
    const val SimpleTestFloat = 12.0F

    const val SimpleTestChildDisplay = "first display"
    const val SubTestChild = "second display"
    const val SubTestChild2 = "third display"
    const val SubTestChild3 = "fourth display"

    val SimpleTestBytes = ByteString.copyFromUtf8("what is this")

    val SubTestChildId = UUID.randomUUID().toString()
    val SubTestChild2Id = UUID.randomUUID().toString()
    val SubTestChild3Id = UUID.randomUUID().toString()

    val FirstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
    val SecondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

    val SubChildId = UUID.randomUUID().toString()
    val SubChildAnotherTestDisplay = "sub child test"
    val SubChildCoolTest = true

    val SubSubChildId = UUID.randomUUID().toString()
    val SubSubChildDisplay = "sub sub child test"

    fun buildSampleRootObject(): TestingModel.SimpleInsertTest.Builder {
        val testModel = TestingModel.SimpleInsertTest.newBuilder()

        testModel.id = UUID.randomUUID().toString()
        testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
        testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay(SimpleTestChildDisplay) .build()
        testModel.display = SimpleInsertTestDisplay
        testModel.testInt32 = SimpleTestInt32
        testModel.testInt64 = SimpleTestInt64
        testModel.testUint32 = SimpleTestUint32
        testModel.testUint64 = SimpleTestUint64
        testModel.testSint32 = SimpleTestSint32
        testModel.testSint64 = SimpleTestSint64
        testModel.testFixed32 = SimpleTestFixed32
        testModel.testFixed64 = SimpleTestFixed64
        testModel.testSfixed32 = SimpleTestSfixed32
        testModel.testSfixed64 = SimpleTestSfixed64
        testModel.testBool = SimpleTestBool
        testModel.testBytes = SimpleTestBytes
        testModel.testDouble = SimpleTestDouble
        testModel.testFloat = SimpleTestFloat

        val subTestChild = TestingModel.Child.newBuilder().setId(SubTestChildId).setTestDisplay(SubTestChild)
        val subTestChild2 = TestingModel.Child.newBuilder().setId(SubTestChild2Id).setTestDisplay(SubTestChild2)
        val subTestChild3 = TestingModel.Child.newBuilder().setId(SubTestChild3Id).setTestDisplay(SubTestChild3)

        testModel.addCoolTypes(FirstCoolType)
        testModel.addCoolTypes(SecondCoolType)

        val subChild = TestingModel.SubChild.newBuilder().setId(SubChildId).setAnotherTestDisplay(SubChildAnotherTestDisplay).setCoolTest(SubChildCoolTest)
        val subSubChild = TestingModel.SubSubChild.newBuilder().setId(SubSubChildId).setSubSubDisplay(SubSubChildDisplay).build()
        subChild.addSubSubChild(subSubChild)
        subTestChild.addSubChild(subChild)
        testModel.addChilds(subTestChild)
        testModel.addChilds(subTestChild2)
        testModel.addChilds(subTestChild3)

        testModel.addCoolTypes(FirstCoolType)
        testModel.addCoolTypes(SecondCoolType)

        return testModel
    }
}
