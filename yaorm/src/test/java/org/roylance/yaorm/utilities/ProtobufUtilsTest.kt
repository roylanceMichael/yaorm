package org.roylance.yaorm.utilities

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.YaormModel
import java.util.*

class ProtobufUtilsTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        // act
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(TestingModel.SimpleInsertTest.getDescriptor(), HashMap())

        // assert
        definition!!
        Assert.assertTrue(definition.name?.equals("SimpleInsertTest")!!)
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == YaormUtils.IdName && it.type.name == ProtobufUtils.ProtoStringName })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "display" && it.type.name == ProtobufUtils.ProtoStringName })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_int32" && it.type.name == ProtobufUtils.ProtoInt32Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_int64" && it.type.name == ProtobufUtils.ProtoInt64Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_uint32" && it.type.name == ProtobufUtils.ProtoUInt32Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_uint64" && it.type.name == ProtobufUtils.ProtoUInt64Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_sint32" && it.type.name == ProtobufUtils.ProtoSInt32Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_sint64" && it.type.name == ProtobufUtils.ProtoSInt64Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_fixed32" && it.type.name == ProtobufUtils.ProtoFixed32Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_fixed64" && it.type.name == ProtobufUtils.ProtoFixed64Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_sfixed32" && it.type.name == ProtobufUtils.ProtoSFixed32Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_sfixed64" && it.type.name == ProtobufUtils.ProtoSFixed64Name })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_bool" && it.type.name == ProtobufUtils.ProtoBoolName })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_bytes" && it.type.name == ProtobufUtils.ProtoBytesName })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_double" && it.type.name == ProtobufUtils.ProtoDoubleName })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "test_float" && it.type.name == ProtobufUtils.ProtoFloatName })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "cool_type" && it.type.name == ProtobufUtils.ProtoStringName })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name == "child" && it.type.name == ProtobufUtils.ProtoStringName })
    }

    @Test
    fun moreComplexPassThroughTest() {
        // arrange
        // act
        val definition = ProtobufUtils.buildDefinitionGraph(TestingModel.SimpleInsertTest.getDescriptor(), HashMap())

        // assert
        Assert.assertTrue(definition.mainTableDefinition.name == "SimpleInsertTest")
        Assert.assertTrue(definition.tableDefinitionGraphsCount == 5)

        val foundEnumLinkerDefinition = definition.tableDefinitionGraphsList.first { YaormModel.TableDefinitionGraph.TableDefinitionGraphType.ENUM_TYPE == it.definitionGraphType }
        Assert.assertTrue(foundEnumLinkerDefinition.hasLinkerTableTable())
        foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.forEach { System.out.println(it.name) }
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.name == "${definition.mainTableDefinition.name}_CoolType_cool_types")
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.any { YaormUtils.IdName == it.name && it.type == YaormModel.ProtobufType.STRING })
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.any { definition.mainTableDefinition.name == it.name && it.type == YaormModel.ProtobufType.STRING })
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.any { "CoolType" == it.name && it.type == YaormModel.ProtobufType.STRING })

        val foundMessageLinkerDefinition = definition.tableDefinitionGraphsList.first { YaormModel.TableDefinitionGraph.TableDefinitionGraphType.MESSAGE_TYPE == it.definitionGraphType }
        Assert.assertTrue(foundMessageLinkerDefinition.hasLinkerTableTable())
        Assert.assertTrue(foundMessageLinkerDefinition.hasOtherTableDefinition())

        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.name == "${definition.mainTableDefinition.name}_Child_childs")
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitionsList.any { YaormUtils.IdName == it.name && it.type == YaormModel.ProtobufType.STRING })
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitionsList.any { "${definition.mainTableDefinition.name}_main" == it.name && it.type == YaormModel.ProtobufType.STRING })
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitionsList.any { "Child_other" == it.name && it.type == YaormModel.ProtobufType.STRING })

        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.name == "Child")
        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.columnDefinitionsList.any { YaormUtils.IdName == it.name && it.type == YaormModel.ProtobufType.STRING })
        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.columnDefinitionsList.any { "test_display" == it.name && it.type == YaormModel.ProtobufType.STRING })
    }

    @Test
    fun moreComplexPassThroughTest2() {
        // arrange
        val testModel = TestingModel.SimpleInsertTest.newBuilder()

        testModel.id = UUID.randomUUID().toString()
        testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
        testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).build()

        // act
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())

        // assert
        Assert.assertTrue(records.tableRecordsList.size == 10)
        val foundRecords = records.tableRecordsList.first { it.tableName == TestingModel.SimpleInsertTest.getDescriptor().name }
        val firstRecord = foundRecords.records.recordsList[0]
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_int32" && it.int32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_int64" && it.int64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_uint32" && it.uint32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_uint64" && it.uint64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sint32" && it.sint32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sint64" && it.sint64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_fixed32" && it.fixed32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_fixed64" && it.fixed64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sfixed32" && it.sfixed32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sfixed64" && it.sfixed64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_bool" && it.boolHolder == false })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_bytes" && it.bytesHolder == ByteString.EMPTY })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_double" && it.doubleHolder == 0.0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_float" && it.floatHolder == 0F })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "cool_type" && it.stringHolder == TestingModel.SimpleInsertTest.CoolType.SURPRISED.name })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "child" && it.stringHolder == testModel.child.id })
    }


    @Test
    fun moreComplexPassThroughTest3() {
        // arrange
        val testModel = TestingModel.SimpleInsertTest.newBuilder()

        testModel.id = UUID.randomUUID().toString()
        testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
        testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()

        val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
        testModel.addChilds(subTestChild)

        val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
        val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

        testModel.addCoolTypes(firstCoolType)
        testModel.addCoolTypes(secondCoolType)

        // act
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())

        // assert
        Assert.assertTrue(records.tableRecordsList.size == 10)

        // verify main record insert
        val simpleInsertTestRecords = records.tableRecordsList.firstOrNull { it.tableName == "SimpleInsertTest" }!!
        val firstRecord = simpleInsertTestRecords.records.recordsList.first()
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_int32" && it.int32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_int64" && it.int64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_uint32" && it.uint32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_uint64" && it.uint64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sint32" && it.sint32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sint64" && it.sint64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_fixed32" && it.fixed32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_fixed64" && it.fixed64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sfixed32" && it.sfixed32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sfixed64" && it.sfixed64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_bool" && it.boolHolder == false })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_bytes" && it.bytesHolder == ByteString.EMPTY })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_double" && it.doubleHolder == 0.0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_float" && it.floatHolder == 0F })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "cool_type" && it.stringHolder == TestingModel.SimpleInsertTest.CoolType.SURPRISED.name })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "child" && it.stringHolder == testModel.child.id })

        val simpleInsertChildLinkerRecords = records.tableRecordsList.firstOrNull { it.tableName == "SimpleInsertTest_Child_childs" }!!
        val firstLinkerRecord = simpleInsertChildLinkerRecords.records.recordsList.first()
        Assert.assertTrue(firstLinkerRecord.columnsList.any { it.definition.name == "id" && it.stringHolder == "${testModel.id}~${subTestChild.id}" })
        Assert.assertTrue(firstLinkerRecord.columnsList.any { it.definition.name == "SimpleInsertTest_main" && it.stringHolder == testModel.id })
        Assert.assertTrue(firstLinkerRecord.columnsList.any { it.definition.name == "Child_other" && it.stringHolder == subTestChild.id })

        val simpleInsertEnumLinkerRecords = records.tableRecordsList.firstOrNull { it.tableName == "SimpleInsertTest_CoolType_cool_types" }!!
        simpleInsertEnumLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "id" && (it.stringHolder == "${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.SURPRISED.name}" || it.stringHolder == "${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.TEST.name}") })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "SimpleInsertTest" && it.stringHolder == testModel.id })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "CoolType" && (it.stringHolder == TestingModel.SimpleInsertTest.CoolType.SURPRISED.name || it.stringHolder == TestingModel.SimpleInsertTest.CoolType.TEST.name) })
        }

        val simpleInsertMessageLinkerRecords = records.tableRecordsList.firstOrNull { it.tableName == "Child" }!!
        simpleInsertMessageLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "id" && (it.stringHolder == testModel.child.id || it.stringHolder == subTestChild.id) })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "test_display" && (it.stringHolder == testModel.child.testDisplay || it.stringHolder == subTestChild.testDisplay) })
        }
    }

    @Test
    fun moreComplexPassThroughTest4() {
        // arrange
        val testModel = TestingModel.SimpleInsertTest.newBuilder()

        testModel.id = UUID.randomUUID().toString()
        testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
        testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()

        val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")

        val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
        val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

        testModel.addCoolTypes(firstCoolType)
        testModel.addCoolTypes(secondCoolType)

        val subChild = TestingModel.SubChild.newBuilder().setId(UUID.randomUUID().toString()).setAnotherTestDisplay("sub child test").setCoolTest(true)

        val subSubChild = TestingModel.SubSubChild.newBuilder().setId(UUID.randomUUID().toString()).setSubSubDisplay("sub sub child test").build()
        subChild.addSubSubChild(subSubChild)
        subTestChild.addSubChild(subChild)
        testModel.addChilds(subTestChild)

        // act
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())

        // assert
        Assert.assertTrue(records.tableRecordsList.size == 10)

        // verify main record insert
        val simpleInsertTestRecords = records.tableRecordsList.firstOrNull { it.tableName == "SimpleInsertTest" }!!
        val firstRecord = simpleInsertTestRecords.records.recordsList.first()
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_int32" && it.int32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_int64" && it.int64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_uint32" && it.uint32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_uint64" && it.uint64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sint32" && it.sint32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sint64" && it.sint64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_fixed32" && it.fixed32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_fixed64" && it.fixed64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sfixed32" && it.sfixed32Holder == 0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_sfixed64" && it.sfixed64Holder == 0L })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_bool" && it.boolHolder == false })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_bytes" && it.bytesHolder == ByteString.EMPTY })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_double" && it.doubleHolder == 0.0 })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "test_float" && it.floatHolder == 0F })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "cool_type" && it.stringHolder == TestingModel.SimpleInsertTest.CoolType.SURPRISED.name })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name == "child" && it.stringHolder == testModel.child.id })

        val simpleInsertChildLinkerRecords = records.tableRecordsList.firstOrNull { it.tableName == "SimpleInsertTest_Child_childs" }!!
        val firstLinkerRecord = simpleInsertChildLinkerRecords.records.recordsList.first()
        Assert.assertTrue(firstLinkerRecord.columnsList.any { it.definition.name == "id" && it.stringHolder == "${testModel.id}~${subTestChild.id}" })
        Assert.assertTrue(firstLinkerRecord.columnsList.any { it.definition.name == "SimpleInsertTest_main" && it.stringHolder == testModel.id })
        Assert.assertTrue(firstLinkerRecord.columnsList.any { it.definition.name == "Child_other" && it.stringHolder == subTestChild.id })

        val simpleInsertEnumLinkerRecords = records.tableRecordsList.firstOrNull { it.tableName == "SimpleInsertTest_CoolType_cool_types" }!!
        simpleInsertEnumLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "id" && (it.stringHolder == "${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.SURPRISED.name}" || it.stringHolder == "${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.TEST.name}") })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "SimpleInsertTest" && it.stringHolder == testModel.id })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "CoolType" && (it.stringHolder == TestingModel.SimpleInsertTest.CoolType.SURPRISED.name || it.stringHolder == TestingModel.SimpleInsertTest.CoolType.TEST.name) })
        }

        val simpleInsertMessageLinkerRecords = records.tableRecordsList.firstOrNull { it.tableName == "Child" }!!
        simpleInsertMessageLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "id" && (it.stringHolder == testModel.child.id || it.stringHolder == subTestChild.id) })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "test_display" && (it.stringHolder == testModel.child.testDisplay || it.stringHolder == subTestChild.testDisplay) })
        }

        val subSubChildRecords = records.tableRecordsList.firstOrNull { it.tableName == "SubSubChild" }!!
        subSubChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "id" && (it.stringHolder == subSubChild.id) })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "sub_sub_display" && (it.stringHolder == subSubChild.subSubDisplay) })
        }

        val childSubChildSubChildRecords = records.tableRecordsList.firstOrNull { it.tableName == "Child_SubChild_sub_child" }!!
        childSubChildSubChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "Child_main" && (it.stringHolder == subTestChild.id) })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "SubChild_other" && (it.stringHolder == subChild.id) })
        }

        val subChildSubSubChildSubSubChildRecords = records.tableRecordsList.firstOrNull { it.tableName == "SubChild_SubSubChild_sub_sub_child" }!!
        subChildSubSubChildSubSubChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "SubSubChild_other" && (it.stringHolder == subSubChild.id) })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "SubChild_main" && (it.stringHolder == subChild.id) })
        }

        val subChildRecords = records.tableRecordsList.firstOrNull { it.tableName == "SubChild" }!!
        subChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columnsList.any { it.definition.name == "id" && (it.stringHolder == subChild.id) })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "another_test_display" && (it.stringHolder == subChild.anotherTestDisplay) })
            Assert.assertTrue(it.columnsList.any { it.definition.name == "cool_test" && (it.boolHolder == true) })
        }
    }

    @Test
    fun moreComplexPassThroughTest5() {
        // arrange
        val testModel = TestingModel.Person.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setFirstName("Mike")
                .setLastName("Roylance")
                .setMother(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Terri").setLastName("Roylance"))
                .setFather(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Paul").setLastName("Roylance"))

        // act
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())

        // assert
        Assert.assertTrue(true)
        val personRecords = records.tableRecordsList.first { it.tableName == TestingModel.Person.getDescriptor().name }!!
        Assert.assertTrue(personRecords.records.recordsList.any { YaormUtils.getIdColumn(it.columnsList)!!.stringHolder == testModel.id })
        Assert.assertTrue(personRecords.records.recordsList.any { YaormUtils.getIdColumn(it.columnsList)!!.stringHolder == testModel.mother.id })
        Assert.assertTrue(personRecords.records.recordsList.any { YaormUtils.getIdColumn(it.columnsList)!!.stringHolder == testModel.father.id })
    }
}
