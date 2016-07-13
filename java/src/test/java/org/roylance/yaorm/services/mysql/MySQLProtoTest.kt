package org.roylance.yaorm.services.mysql

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.ProtobufUtils
import org.roylance.yaorm.utilities.TestModelGeneratedMessageBuilder
import org.roylance.yaorm.utilities.TestingModelUtilities
import java.util.*

class MySQLProtoTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlSchema!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)

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
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun simplePassThrough2Test() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlSchema!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

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
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.Child>(TestingModel.Child.getDefaultInstance(), entityService, subTestChild.id, protoService)

            // assert
            Assert.assertTrue(record is TestingModel.Child)
            Assert.assertTrue(record!!.testDisplay.equals("second display"))
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun verifyTypesSavedAndReturnedCorrectlyTest() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlSchema!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(TestingModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService)

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolType.equals(TestingModel.SimpleInsertTest.CoolType.SURPRISED))
            Assert.assertTrue(record.display.equals(testModel.display))
            Assert.assertTrue(record.testInt32.equals(testModel.testInt32))
            Assert.assertTrue(record.testInt64.equals(testModel.testInt64))
            Assert.assertTrue(record.testUint32.equals(testModel.testUint32))
            Assert.assertTrue(record.testUint64.equals(testModel.testUint64))
            Assert.assertTrue(record.testSint32.equals(testModel.testSint32))
            Assert.assertTrue(record.testSint64.equals(testModel.testSint64))
            Assert.assertTrue(record.testFixed32.equals(testModel.testFixed32))
            Assert.assertTrue(record.testFixed64.equals(testModel.testFixed64))
            Assert.assertTrue(record.testSfixed32.equals(testModel.testSfixed32))
            Assert.assertTrue(record.testSfixed64.equals(testModel.testSfixed64))
            Assert.assertTrue(record.testBool.equals(testModel.testBool))
            Assert.assertTrue(record.testBytes.equals(testModel.testBytes))
            Assert.assertTrue(record.testDouble.equals(testModel.testDouble))
            Assert.assertTrue(record.testFloat.equals(testModel.testFloat))
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlSchema!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

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

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(TestingModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService)

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolTypesCount == 2)
            Assert.assertTrue(record.coolTypesList.any { it.equals(TestingModel.SimpleInsertTest.CoolType.SURPRISED) })
            Assert.assertTrue(record.coolTypesList.any { it.equals(TestingModel.SimpleInsertTest.CoolType.TEST) })
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlSchema!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

            val testModel = TestingModelUtilities.buildSampleRootObject()

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
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
                    protoService)

            // assert
            Assert.assertTrue(insertedRecord is TestingModel.SimpleInsertTest)
            System.out.println(insertedRecord!!.childsCount)
            Assert.assertTrue(insertedRecord.childsCount == 3)
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay.equals(TestingModelUtilities.SubTestChild) && it.id.equals(TestingModelUtilities.SubTestChildId) })
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay.equals(TestingModelUtilities.SubTestChild2) && it.id.equals(TestingModelUtilities.SubTestChild2Id) })
            Assert.assertTrue(insertedRecord.childsList.any { it.testDisplay.equals(TestingModelUtilities.SubTestChild3) && it.id.equals(TestingModelUtilities.SubTestChild3Id) })

            val subTestChildFound = insertedRecord.childsList.first { it.testDisplay.equals(TestingModelUtilities.SubTestChild) }
            Assert.assertTrue(subTestChildFound.subChildCount == 1)

            val anotherSubTestChild = subTestChildFound.subChildList.first()
            Assert.assertTrue(anotherSubTestChild.id.equals(anotherSubTestChild.id))
            Assert.assertTrue(anotherSubTestChild.anotherTestDisplay.equals(TestingModelUtilities.SubChildAnotherTestDisplay))
            Assert.assertTrue(anotherSubTestChild.subSubChildCount == 1)

            val subSubChildFound = anotherSubTestChild.subSubChildList.first()
            Assert.assertTrue(TestingModelUtilities.SubSubChildId.equals(subSubChildFound.id))
            Assert.assertTrue(TestingModelUtilities.SubSubChildDisplay.equals(subSubChildFound.subSubDisplay))
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }
}
