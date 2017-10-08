package org.roylance.yaorm.utilities.common

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.roylance.common.service.IBuilder
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.utilities.ProtobufUtils
import org.roylance.yaorm.utilities.TestingModelUtilities
import java.util.*

object ProtoTestUtilities {

    fun simplePassThroughTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            // act
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // assert
            Assert.assertTrue(true)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun singleQuoteSimplePassThroughTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display' just outside of lincoln'") .build()

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            // act
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // assert
            Assert.assertTrue(true)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simplePassThrough2Test(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.Child>(
                    TestingModel.Child.getDefaultInstance(),
                    entityService,
                    subTestChild.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.Child)
            Assert.assertTrue(record!!.testDisplay == "second display")
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun verifyTypesSavedAndReturnedCorrectlyTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
            testModel.display = "random display"
            testModel.testInt32 = 1
            testModel.testInt64 = 2
            testModel.testUint32 = 3
            testModel.testUint64 = 4
            testModel.testSint32 = 5
            testModel.testSint64 = 6
            testModel.testFixed32 = 7
            testModel.testFixed64 = 8
            testModel.testSfixed32 = 9
            testModel.testSfixed64 = 10
            testModel.testBool = true
            testModel.testBytes = ByteString.copyFromUtf8("what is this")
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(
                    TestingModel.SimpleInsertTest.getDefaultInstance(),
                    entityService,
                    testModel.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolType == TestingModel.SimpleInsertTest.CoolType.SURPRISED)
            Assert.assertTrue(record.display == testModel.display)
            Assert.assertTrue(record.testInt32 == testModel.testInt32)
            Assert.assertTrue(record.testInt64 == testModel.testInt64)
            Assert.assertTrue(record.testUint32 == testModel.testUint32)
            Assert.assertTrue(record.testUint64 == testModel.testUint64)
            Assert.assertTrue(record.testSint32 == testModel.testSint32)
            Assert.assertTrue(record.testSint64 == testModel.testSint64)
            Assert.assertTrue(record.testFixed32 == testModel.testFixed32)
            Assert.assertTrue(record.testFixed64 == testModel.testFixed64)
            Assert.assertTrue(record.testSfixed32 == testModel.testSfixed32)
            Assert.assertTrue(record.testSfixed64 == testModel.testSfixed64)
            Assert.assertTrue(record.testBool == testModel.testBool)
            Assert.assertTrue(record.testBytes == testModel.testBytes)
            Assert.assertTrue(record.testDouble == testModel.testDouble)
            Assert.assertTrue(record.testFloat == testModel.testFloat)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
            testModel.display = "random display"
            testModel.testInt32 = 1
            testModel.testInt64 = 2
            testModel.testUint32 = 3
            testModel.testUint64 = 4
            testModel.testSint32 = 5
            testModel.testSint64 = 6
            testModel.testFixed32 = 7
            testModel.testFixed64 = 8
            testModel.testSfixed32 = 9
            testModel.testSfixed64 = 10
            testModel.testBool = true
            testModel.testBytes = ByteString.copyFromUtf8("what is this")
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(
                    TestingModel.SimpleInsertTest.getDefaultInstance(),
                    entityService,
                    testModel.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolTypesCount == 2)
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.SURPRISED })
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.TEST })
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val testModel = TestingModelUtilities.buildSampleRootObject()

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val insertedRecord = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(
                    TestingModel.SimpleInsertTest.getDefaultInstance(),
                    entityService,
                    testModel.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(insertedRecord is TestingModel.SimpleInsertTest)
            System.out.println(insertedRecord!!.childsCount)
            Assert.assertTrue(insertedRecord.childsCount == 3)
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay == TestingModelUtilities.SubTestChild && it.id == TestingModelUtilities.SubTestChildId })
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay == TestingModelUtilities.SubTestChild2 && it.id == TestingModelUtilities.SubTestChild2Id })
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay == TestingModelUtilities.SubTestChild3 && it.id == TestingModelUtilities.SubTestChild3Id })

            val subTestChildFound = insertedRecord.childsList.first { it.testDisplay == TestingModelUtilities.SubTestChild }
            Assert.assertTrue(subTestChildFound.subChildCount == 1)

            val anotherSubTestChild = subTestChildFound.subChildList.first()
            Assert.assertTrue(anotherSubTestChild.id == anotherSubTestChild.id)
            Assert.assertTrue(anotherSubTestChild.anotherTestDisplay == TestingModelUtilities.SubChildAnotherTestDisplay)
            Assert.assertTrue(anotherSubTestChild.subSubChildCount == 1)

            val subSubChildFound = anotherSubTestChild.subSubChildList.first()
            Assert.assertTrue(TestingModelUtilities.SubSubChildId == subSubChildFound.id)
            Assert.assertTrue(TestingModelUtilities.SubSubChildDisplay == subSubChildFound.subSubDisplay)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simplePassThroughDefinitionTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            // act
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // assert
            Assert.assertTrue(true)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleDefinitionBuilderTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val tableDefinition = entityService.buildDefinitionFromSql("select * from SimpleInsertTest;")

            // assert
            System.out.println(tableDefinition.columnDefinitionsCount)
            tableDefinition.columnDefinitionsList.sortedBy { it.order }.forEach {
                System.out.println("${it.name}\t${it.type}\t${it.order}")
            }
            Assert.assertTrue(tableDefinition.columnDefinitionsCount == 18)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleSchemaTestTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val schemas = entityService.getSchemaNames()

            // assert
            println(schemas)
            Assert.assertTrue(schemas.isNotEmpty())
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleSchemaTablesTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null, schemaName: String) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val tableNames = entityService.getTableNames(schemaName)

            // assert
            println(tableNames)
            Assert.assertTrue(tableNames.isNotEmpty())
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleTableDefinitionTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null, schemaName: String) {
        // arrange
        try {

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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val tableDefinition = entityService.getTableDefinition(schemaName,
                    TestingModel.SimpleInsertTest.getDescriptor().name)

            // assert
            println(tableDefinition)
            Assert.assertTrue(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simplePassThroughEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            // act
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // assert
            Assert.assertTrue(true)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simplePassThrough2EmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.Child>(
                    TestingModel.Child.getDefaultInstance(),
                    entityService,
                    subTestChild.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.Child)
            Assert.assertTrue(record!!.testDisplay == "second display")
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
            testModel.display = "random display"
            testModel.testInt32 = 1
            testModel.testInt64 = 2
            testModel.testUint32 = 3
            testModel.testUint64 = 4
            testModel.testSint32 = 5
            testModel.testSint64 = 6
            testModel.testFixed32 = 7
            testModel.testFixed64 = 8
            testModel.testSfixed32 = 9
            testModel.testSfixed64 = 10
            testModel.testBool = true
            testModel.testBytes = ByteString.copyFromUtf8("what is this")
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(
                    TestingModel.SimpleInsertTest.getDefaultInstance(),
                    entityService,
                    testModel.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolType == TestingModel.SimpleInsertTest.CoolType.SURPRISED)
            Assert.assertTrue(record.display == testModel.display)
            Assert.assertTrue(record.testInt32 == testModel.testInt32)
            Assert.assertTrue(record.testInt64 == testModel.testInt64)
            Assert.assertTrue(record.testUint32 == testModel.testUint32)
            Assert.assertTrue(record.testUint64 == testModel.testUint64)
            Assert.assertTrue(record.testSint32 == testModel.testSint32)
            Assert.assertTrue(record.testSint64 == testModel.testSint64)
            Assert.assertTrue(record.testFixed32 == testModel.testFixed32)
            Assert.assertTrue(record.testFixed64 == testModel.testFixed64)
            Assert.assertTrue(record.testSfixed32 == testModel.testSfixed32)
            Assert.assertTrue(record.testSfixed64 == testModel.testSfixed64)
            Assert.assertTrue(record.testBool == testModel.testBool)
            Assert.assertTrue(record.testBytes == testModel.testBytes)
            Assert.assertTrue(record.testDouble == testModel.testDouble)
            Assert.assertTrue(record.testFloat == testModel.testFloat)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
            testModel.display = "random display"
            testModel.testInt32 = 1
            testModel.testInt64 = 2
            testModel.testUint32 = 3
            testModel.testUint64 = 4
            testModel.testSint32 = 5
            testModel.testSint64 = 6
            testModel.testFixed32 = 7
            testModel.testFixed64 = 8
            testModel.testSfixed32 = 9
            testModel.testSfixed64 = 10
            testModel.testBool = true
            testModel.testBytes = ByteString.copyFromUtf8("what is this")
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(
                    TestingModel.SimpleInsertTest.getDefaultInstance(),
                    entityService,
                    testModel.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolTypesCount == 2)
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.SURPRISED })
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.TEST })
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val testModel = TestingModelUtilities.buildSampleRootObject()

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val insertedRecord = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(
                    TestingModel.SimpleInsertTest.getDefaultInstance(),
                    entityService,
                    testModel.id,
                    HashMap(),
                    HashMap())

            // assert
            Assert.assertTrue(insertedRecord is TestingModel.SimpleInsertTest)
            System.out.println(insertedRecord!!.childsCount)
            Assert.assertTrue(insertedRecord.childsCount == 3)
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay == TestingModelUtilities.SubTestChild && it.id == TestingModelUtilities.SubTestChildId })
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay == TestingModelUtilities.SubTestChild2 && it.id == TestingModelUtilities.SubTestChild2Id })
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay == TestingModelUtilities.SubTestChild3 && it.id == TestingModelUtilities.SubTestChild3Id })

            val subTestChildFound = insertedRecord.childsList.first { it.testDisplay == TestingModelUtilities.SubTestChild }
            Assert.assertTrue(subTestChildFound.subChildCount == 1)

            val anotherSubTestChild = subTestChildFound.subChildList.first()
            Assert.assertTrue(anotherSubTestChild.id == anotherSubTestChild.id)
            Assert.assertTrue(anotherSubTestChild.anotherTestDisplay == TestingModelUtilities.SubChildAnotherTestDisplay)
            Assert.assertTrue(anotherSubTestChild.subSubChildCount == 1)

            val subSubChildFound = anotherSubTestChild.subSubChildList.first()
            Assert.assertTrue(TestingModelUtilities.SubSubChildId == subSubChildFound.id)
            Assert.assertTrue(TestingModelUtilities.SubSubChildDisplay == subSubChildFound.subSubDisplay)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simplePassThroughDefinitionEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            // act
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // assert
            Assert.assertTrue(true)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleDefinitionBuilderEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val tableDefinition = entityService.buildDefinitionFromSql("select * from SimpleInsertTest;")

            // assert
            System.out.println(tableDefinition.columnDefinitionsCount)
            tableDefinition.columnDefinitionsList.sortedBy { it.order }.forEach {
                System.out.println("${it.name}\t${it.type}\t${it.order}")
            }
            Assert.assertTrue(tableDefinition.columnDefinitionsCount == 18)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleSchemaEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val schemas = entityService.getSchemaNames()

            // assert
            println(schemas)
            Assert.assertTrue(schemas.isNotEmpty())
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleSchemaTablesEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null, schemaName: String) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val tableNames = entityService.getTableNames(schemaName)

            // assert
            println(tableNames)
            Assert.assertTrue(tableNames.isNotEmpty())
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleTableDefinitionEmptyAsNullTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null, schemaName: String) {
        // arrange
        try {
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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val tableDefinition = entityService.getTableDefinition(schemaName, TestingModel.SimpleInsertTest.getDescriptor().name)

            // assert
            println(tableDefinition)
            Assert.assertTrue(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }
}