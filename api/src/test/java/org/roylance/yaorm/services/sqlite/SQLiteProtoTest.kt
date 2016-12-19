package org.roylance.yaorm.services.sqlite

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.utilities.ProtobufUtils
import org.roylance.yaorm.utilities.TestModelGMBuilder
import java.io.File
import java.util.*

class SQLiteProtoTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)

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
            database.deleteOnExit()
        }
    }

    @Test
    fun simplePassThrough2Test() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()

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
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.Child>(TestingModel.Child.getDefaultInstance(), entityService, subTestChild.id, protoService, HashMap(), HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.Child)
            Assert.assertTrue(record!!.testDisplay == "second display")
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun verifyTypesSavedAndReturnedCorrectlyTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()

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
            testModel.testDouble = 11.55
            testModel.testFloat = 12.2324F

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
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(TestingModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService, HashMap(), HashMap())

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
            database.deleteOnExit()
        }
    }

    @Test
    fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()

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
            testModel.testDouble = 11.55
            testModel.testFloat = 12.2324F

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
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(TestingModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService, HashMap(), HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolTypesCount == 2)
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.SURPRISED })
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.TEST })
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()

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
            testModel.testDouble = 11.55
            testModel.testFloat = 12.2324F

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            val subTestChild2 = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("third display")
            val subTestChild3 = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("fourth display")
            testModel.addChilds(subTestChild)
            testModel.addChilds(subTestChild2)
            testModel.addChilds(subTestChild3)

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
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(TestingModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService, HashMap(), HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.childsCount == 3)
            Assert.assertTrue(record.childsList.any { it.testDisplay == subTestChild.testDisplay && it.id == subTestChild.id })
            Assert.assertTrue(record.childsList.any { it.testDisplay == subTestChild2.testDisplay && it.id == subTestChild2.id })
            Assert.assertTrue(record.childsList.any { it.testDisplay == subTestChild3.testDisplay && it.id == subTestChild3.id })
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun getMetaDataTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)

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
            testModel.testDouble = 11.55
            testModel.testFloat = 12.2324F

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            val subTestChild2 = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("third display")
            val subTestChild3 = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("fourth display")
            testModel.addChilds(subTestChild)
            testModel.addChilds(subTestChild2)
            testModel.addChilds(subTestChild3)

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
            database.deleteOnExit()
        }
    }
}
