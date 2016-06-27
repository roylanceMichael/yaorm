package org.roylance.yaorm.utilities

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.models.YaormModel
import java.util.*

class ProtobufUtilsTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        // act
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(TestingModel.SimpleInsertTest.getDescriptor())

        // assert
        definition!!
        Assert.assertTrue(definition.name?.equals("SimpleInsertTest")!!)
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals(CommonUtils.IdName) && it.type.name.equals(ProtobufUtils.ProtoStringName) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("display") && it.type.name.equals(ProtobufUtils.ProtoStringName) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_int32") && it.type.name.equals(ProtobufUtils.ProtoInt32Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_int64") && it.type.name.equals(ProtobufUtils.ProtoInt64Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_uint32") && it.type.name.equals(ProtobufUtils.ProtoUInt32Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_uint64") && it.type.name.equals(ProtobufUtils.ProtoUInt64Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_sint32") && it.type.name.equals(ProtobufUtils.ProtoSInt32Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_sint64") && it.type.name.equals(ProtobufUtils.ProtoSInt64Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_fixed32") && it.type.name.equals(ProtobufUtils.ProtoFixed32Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_fixed64") && it.type.name.equals(ProtobufUtils.ProtoFixed64Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_sfixed32") && it.type.name.equals(ProtobufUtils.ProtoSFixed32Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_sfixed64") && it.type.name.equals(ProtobufUtils.ProtoSFixed64Name) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_bool") && it.type.name.equals(ProtobufUtils.ProtoBoolName) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_bytes") && it.type.name.equals(ProtobufUtils.ProtoBytesName) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_double") && it.type.name.equals(ProtobufUtils.ProtoDoubleName) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("test_float") && it.type.name.equals(ProtobufUtils.ProtoFloatName) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("cool_type") && it.type.name.equals(ProtobufUtils.ProtoStringName) })
        Assert.assertTrue(definition.columnDefinitions.values.any { it.name.equals("child") && it.type.name.equals(ProtobufUtils.ProtoStringName) })
    }

    @Test
    fun moreComplexPassThroughTest() {
        // arrange
        // act
        val definition = ProtobufUtils.buildDefinitionGraph(TestingModel.SimpleInsertTest.getDescriptor())

        // assert
        Assert.assertTrue(definition.mainTableDefinition.name.equals("SimpleInsertTest"))
        Assert.assertTrue(definition.tableDefinitionGraphsCount == 5)

        val foundEnumLinkerDefinition = definition.tableDefinitionGraphsList.first { YaormModel.TableDefinitionGraph.TableDefinitionGraphType.ENUM_TYPE.equals(it.definitionGraphType) }
        Assert.assertTrue(foundEnumLinkerDefinition.hasLinkerTableTable())
        foundEnumLinkerDefinition.linkerTableTable.columnDefinitions.values.forEach { System.out.println(it.name) }
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.name.equals("${definition.mainTableDefinition.name}_CoolType_cool_types"))
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitions.values.any { CommonUtils.IdName.equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitions.values.any { "${definition.mainTableDefinition.name}".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitions.values.any { "CoolType".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })

        val foundMessageLinkerDefinition = definition.tableDefinitionGraphsList.first { YaormModel.TableDefinitionGraph.TableDefinitionGraphType.MESSAGE_TYPE.equals(it.definitionGraphType) }
        Assert.assertTrue(foundMessageLinkerDefinition.hasLinkerTableTable())
        Assert.assertTrue(foundMessageLinkerDefinition.hasOtherTableDefinition())

        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.name.equals("${definition.mainTableDefinition.name}_Child_childs"))
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitions.values.any { CommonUtils.IdName.equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitions.values.any { "${definition.mainTableDefinition.name}_main".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitions.values.any { "Child_other".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })

        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.name.equals("Child"))
        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.columnDefinitions.values.any { CommonUtils.IdName.equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.columnDefinitions.values.any { "test_display".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
    }

    @Test
    fun moreComplexPassThroughTest2() {
        // arrange
        val testModel = TestingModel.SimpleInsertTest.newBuilder()

        testModel.id = UUID.randomUUID().toString()
        testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
        testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).build()

        // act
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())

        // assert
        Assert.assertTrue(records.tableRecords.size.equals(10))
        val foundRecords = records.tableRecords.values.first { it.tableName.equals(TestingModel.SimpleInsertTest.getDescriptor().name) }
        val firstRecord = foundRecords.records.recordsList[0]
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_int32") && it.int32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_int64") && it.int64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_uint32") && it.uint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_uint64") && it.uint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sint32") && it.sint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sint64") && it.sint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_fixed32") && it.fixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_fixed64") && it.fixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sfixed32") && it.sfixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sfixed64") && it.sfixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_bool") && it.boolHolder.equals(false) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_bytes") && it.bytesHolder.equals(ByteString.EMPTY) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_double") && it.doubleHolder.equals(0.0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_float") && it.floatHolder.equals(0F) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("cool_type") && it.stringHolder.equals(TestingModel.SimpleInsertTest.CoolType.SURPRISED.name) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("child") && it.stringHolder.equals(testModel.child.id) })
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
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())

        // assert
        Assert.assertTrue(records.tableRecords.values.size.equals(10))

        // verify main record insert
        val simpleInsertTestRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SimpleInsertTest") }!!
        val firstRecord = simpleInsertTestRecords.records.recordsList.first()
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_int32") && it.int32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_int64") && it.int64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_uint32") && it.uint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_uint64") && it.uint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sint32") && it.sint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sint64") && it.sint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_fixed32") && it.fixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_fixed64") && it.fixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sfixed32") && it.sfixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sfixed64") && it.sfixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_bool") && it.boolHolder.equals(false) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_bytes") && it.bytesHolder.equals(ByteString.EMPTY) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_double") && it.doubleHolder.equals(0.0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_float") && it.floatHolder.equals(0F) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("cool_type") && it.stringHolder.equals(TestingModel.SimpleInsertTest.CoolType.SURPRISED.name) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("child") && it.stringHolder.equals(testModel.child.id) })

        val simpleInsertChildLinkerRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SimpleInsertTest_Child_childs") }!!
        val firstLinkerRecord = simpleInsertChildLinkerRecords.records.recordsList.first()
        Assert.assertTrue(firstLinkerRecord.columns.values.any { it.definition.name.equals("id") && it.stringHolder.equals("${testModel.id}~${subTestChild.id}") })
        Assert.assertTrue(firstLinkerRecord.columns.values.any { it.definition.name.equals("SimpleInsertTest_main") && it.stringHolder.equals(testModel.id) })
        Assert.assertTrue(firstLinkerRecord.columns.values.any { it.definition.name.equals("Child_other") && it.stringHolder.equals(subTestChild.id) })

        val simpleInsertEnumLinkerRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SimpleInsertTest_CoolType_cool_types") }!!
        simpleInsertEnumLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("id") && (it.stringHolder.equals("${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.SURPRISED.name}") || it.stringHolder.equals("${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.TEST.name}")) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("SimpleInsertTest") && it.stringHolder.equals(testModel.id) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("CoolType") && (it.stringHolder.equals(TestingModel.SimpleInsertTest.CoolType.SURPRISED.name) || it.stringHolder.equals(TestingModel.SimpleInsertTest.CoolType.TEST.name) ) })
        }

        val simpleInsertMessageLinkerRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("Child") }!!
        simpleInsertMessageLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("id") && (it.stringHolder.equals(testModel.child.id) || it.stringHolder.equals(subTestChild.id)) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("test_display") && (it.stringHolder.equals(testModel.child.testDisplay) || it.stringHolder.equals(subTestChild.testDisplay) ) })
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
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())

        // assert
        Assert.assertTrue(records.tableRecords.values.size.equals(10))

        // verify main record insert
        val simpleInsertTestRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SimpleInsertTest") }!!
        val firstRecord = simpleInsertTestRecords.records.recordsList.first()
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_int32") && it.int32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_int64") && it.int64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_uint32") && it.uint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_uint64") && it.uint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sint32") && it.sint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sint64") && it.sint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_fixed32") && it.fixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_fixed64") && it.fixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sfixed32") && it.sfixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_sfixed64") && it.sfixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_bool") && it.boolHolder.equals(false) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_bytes") && it.bytesHolder.equals(ByteString.EMPTY) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_double") && it.doubleHolder.equals(0.0) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("test_float") && it.floatHolder.equals(0F) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("cool_type") && it.stringHolder.equals(TestingModel.SimpleInsertTest.CoolType.SURPRISED.name) })
        Assert.assertTrue(firstRecord.columns.values.any { it.definition.name.equals("child") && it.stringHolder.equals(testModel.child.id) })

        val simpleInsertChildLinkerRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SimpleInsertTest_Child_childs") }!!
        val firstLinkerRecord = simpleInsertChildLinkerRecords.records.recordsList.first()
        Assert.assertTrue(firstLinkerRecord.columns.values.any { it.definition.name.equals("id") && it.stringHolder.equals("${testModel.id}~${subTestChild.id}") })
        Assert.assertTrue(firstLinkerRecord.columns.values.any { it.definition.name.equals("SimpleInsertTest_main") && it.stringHolder.equals(testModel.id) })
        Assert.assertTrue(firstLinkerRecord.columns.values.any { it.definition.name.equals("Child_other") && it.stringHolder.equals(subTestChild.id) })

        val simpleInsertEnumLinkerRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SimpleInsertTest_CoolType_cool_types") }!!
        simpleInsertEnumLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("id") && (it.stringHolder.equals("${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.SURPRISED.name}") || it.stringHolder.equals("${testModel.id}~${TestingModel.SimpleInsertTest.CoolType.TEST.name}")) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("SimpleInsertTest") && it.stringHolder.equals(testModel.id) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("CoolType") && (it.stringHolder.equals(TestingModel.SimpleInsertTest.CoolType.SURPRISED.name) || it.stringHolder.equals(TestingModel.SimpleInsertTest.CoolType.TEST.name) ) })
        }

        val simpleInsertMessageLinkerRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("Child") }!!
        simpleInsertMessageLinkerRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("id") && (it.stringHolder.equals(testModel.child.id) || it.stringHolder.equals(subTestChild.id)) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("test_display") && (it.stringHolder.equals(testModel.child.testDisplay) || it.stringHolder.equals(subTestChild.testDisplay) ) })
        }

        val subSubChildRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SubSubChild") }!!
        subSubChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("id") && (it.stringHolder.equals(subSubChild.id)) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("sub_sub_display") && (it.stringHolder.equals(subSubChild.subSubDisplay)) })
        }

        val childSubChildSubChildRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("Child_SubChild_sub_child") }!!
        childSubChildSubChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("Child_main") && (it.stringHolder.equals(subTestChild.id)) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("SubChild_other") && (it.stringHolder.equals(subChild.id)) })
        }

        val subChildSubSubChildSubSubChildRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SubChild_SubSubChild_sub_sub_child") }!!
        subChildSubSubChildSubSubChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("SubSubChild_other") && (it.stringHolder.equals(subSubChild.id)) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("SubChild_main") && (it.stringHolder.equals(subChild.id)) })
        }

        val subChildRecords = records.tableRecords.values.firstOrNull { it.tableName.equals("SubChild") }!!
        subChildRecords.records.recordsList.forEach {
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("id") && (it.stringHolder.equals(subChild.id)) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("another_test_display") && (it.stringHolder.equals(subChild.anotherTestDisplay)) })
            Assert.assertTrue(it.columns.values.any { it.definition.name.equals("cool_test") && (it.boolHolder.equals(true)) })
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
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())

        // assert
        Assert.assertTrue(true)
        val personRecords = records.tableRecords[TestingModel.Person.getDescriptor().name]!!
        Assert.assertTrue(personRecords.records.recordsList.any { it.columns[CommonUtils.IdName]!!.stringHolder.equals(testModel.id) })
        Assert.assertTrue(personRecords.records.recordsList.any { it.columns[CommonUtils.IdName]!!.stringHolder.equals(testModel.mother.id) })
        Assert.assertTrue(personRecords.records.recordsList.any { it.columns[CommonUtils.IdName]!!.stringHolder.equals(testModel.father.id) })
    }
}
