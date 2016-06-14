package org.roylance.yaorm.utilities

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestModel
import org.roylance.yaorm.models.YaormModel
import java.util.*

class ProtobufUtilsTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        // act
        val definition = ProtobufUtils.buildDefinitionFromDescriptor(TestModel.SimpleInsertTest.getDescriptor())

        // assert
        definition!!
        Assert.assertTrue(definition.name?.equals("SimpleInsertTest")!!)
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals(CommonUtils.IdName) && it.type.name.equals(ProtobufUtils.ProtoStringName) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("display") && it.type.name.equals(ProtobufUtils.ProtoStringName) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_int32") && it.type.name.equals(ProtobufUtils.ProtoInt32Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_int64") && it.type.name.equals(ProtobufUtils.ProtoInt64Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_uint32") && it.type.name.equals(ProtobufUtils.ProtoUInt32Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_uint64") && it.type.name.equals(ProtobufUtils.ProtoUInt64Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_sint32") && it.type.name.equals(ProtobufUtils.ProtoSInt32Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_sint64") && it.type.name.equals(ProtobufUtils.ProtoSInt64Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_fixed32") && it.type.name.equals(ProtobufUtils.ProtoFixed32Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_fixed64") && it.type.name.equals(ProtobufUtils.ProtoFixed64Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_sfixed32") && it.type.name.equals(ProtobufUtils.ProtoSFixed32Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_sfixed64") && it.type.name.equals(ProtobufUtils.ProtoSFixed64Name) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_bool") && it.type.name.equals(ProtobufUtils.ProtoBoolName) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_bytes") && it.type.name.equals(ProtobufUtils.ProtoBytesName) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_double") && it.type.name.equals(ProtobufUtils.ProtoDoubleName) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("test_float") && it.type.name.equals(ProtobufUtils.ProtoFloatName) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("cool_type") && it.type.name.equals(ProtobufUtils.ProtoStringName) })
        Assert.assertTrue(definition.columnDefinitionsList.any { it.name.equals("child") && it.type.name.equals(ProtobufUtils.ProtoStringName) })
    }

    @Test
    fun moreComplexPassThroughTest() {
        // arrange
        // act
        val definition = ProtobufUtils.buildDefinitionGraph(TestModel.SimpleInsertTest.getDescriptor())

        // assert
        Assert.assertTrue(definition.mainTableDefinition.name.equals("SimpleInsertTest"))
        Assert.assertTrue(definition.tableDefinitionGraphsCount == 2)

        val foundEnumLinkerDefinition = definition.tableDefinitionGraphsList.first { YaormModel.TableDefinitionGraph.TableDefinitionGraphType.ENUM_TYPE.equals(it.definitionGraphType) }
        Assert.assertTrue(foundEnumLinkerDefinition.hasLinkerTableTable())
        foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.forEach { System.out.println(it.name) }
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.name.equals("${definition.mainTableDefinition.name}_CoolType_cool_types"))
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.any { CommonUtils.IdName.equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.any { "${definition.mainTableDefinition.name}".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundEnumLinkerDefinition.linkerTableTable.columnDefinitionsList.any { "CoolType".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })

        val foundMessageLinkerDefinition = definition.tableDefinitionGraphsList.first { YaormModel.TableDefinitionGraph.TableDefinitionGraphType.MESSAGE_TYPE.equals(it.definitionGraphType) }
        Assert.assertTrue(foundMessageLinkerDefinition.hasLinkerTableTable())
        Assert.assertTrue(foundMessageLinkerDefinition.hasOtherTableDefinition())

        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.name.equals("${definition.mainTableDefinition.name}_Child_childs"))
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitionsList.any { CommonUtils.IdName.equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitionsList.any { "${definition.mainTableDefinition.name}".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundMessageLinkerDefinition.linkerTableTable.columnDefinitionsList.any { "Child".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })

        System.out.println(foundMessageLinkerDefinition.otherTableDefinition.name)
        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.name.equals("Child"))
        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.columnDefinitionsList.any { CommonUtils.IdName.equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
        Assert.assertTrue(foundMessageLinkerDefinition.otherTableDefinition.columnDefinitionsList.any { "test_display".equals(it.name) && it.type.equals(YaormModel.ProtobufType.STRING) })
    }

    @Test
    fun moreComplexPassThroughTest2() {
        // arrange

        val testModel = TestModel.SimpleInsertTest.newBuilder()

        testModel.id = UUID.randomUUID().toString()
        testModel.coolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
        testModel.child = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).build()

        // act
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())

        // assert
        Assert.assertTrue(records.tableRecordsCount.equals(1))
        val foundRecords = records.tableRecordsList[0].records
        val firstRecord = foundRecords.recordsList[0]
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_int32") && it.int32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_int64") && it.int64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_uint32") && it.uint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_uint64") && it.uint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sint32") && it.sint32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sint64") && it.sint64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_fixed32") && it.fixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_fixed64") && it.fixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sfixed32") && it.sfixed32Holder.equals(0) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sfixed64") && it.sfixed64Holder.equals(0L) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_bool") && it.boolHolder.equals(false) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_bytes") && it.bytesHolder.equals(ByteString.EMPTY) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_double") && it.doubleHolder.equals(0.0) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_float") && it.floatHolder.equals(0F) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("cool_type") && it.stringHolder.equals(TestModel.SimpleInsertTest.CoolType.SURPRISED.name) })
        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("child") && it.stringHolder.equals(testModel.child.id) })
    }

    @Test
    fun moreComplexPassThroughTest3() {
        // arrange

        val testModel = TestModel.SimpleInsertTest.newBuilder()

        testModel.id = UUID.randomUUID().toString()
        testModel.coolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
        testModel.child = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).build()

        val subTestChild = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString())
        testModel.addChilds(subTestChild)

        val firstCoolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
        val secondCoolType = TestModel.SimpleInsertTest.CoolType.TEST

        testModel.addCoolTypes(firstCoolType)
        testModel.addCoolTypes(secondCoolType)

        // act
        val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())

        // assert
        Assert.assertTrue(records.tableRecordsCount.equals(2))
//        Assert.assertTrue(records.recordsCount.equals(4))
//        val firstRecord = records.recordsList[0]
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_int32") && it.int32Holder.equals(0) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_int64") && it.int64Holder.equals(0L) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_uint32") && it.uint32Holder.equals(0) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_uint64") && it.uint64Holder.equals(0L) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sint32") && it.sint32Holder.equals(0) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sint64") && it.sint64Holder.equals(0L) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_fixed32") && it.fixed32Holder.equals(0) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_fixed64") && it.fixed64Holder.equals(0L) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sfixed32") && it.sfixed32Holder.equals(0) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_sfixed64") && it.sfixed64Holder.equals(0L) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_bool") && it.boolHolder.equals(false) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_bytes") && it.bytesHolder.equals(ByteString.EMPTY) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_double") && it.doubleHolder.equals(0.0) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("test_float") && it.floatHolder.equals(0F) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("cool_type") && it.stringHolder.equals(TestModel.SimpleInsertTest.CoolType.SURPRISED.name) })
//        Assert.assertTrue(firstRecord.columnsList.any { it.definition.name.equals("child") && it.stringHolder.equals(testModel.child.id) })
    }
}
