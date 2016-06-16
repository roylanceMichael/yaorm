package org.roylance.yaorm.services.mysql

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.ProtobufUtils
import org.roylance.yaorm.utilities.TestModelGeneratedMessageBuilder
import java.sql.DriverManager
import java.util.*

class MySQLProtoTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)

            val testModel = TestModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()

            val subTestChild = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestModel.SimpleInsertTest.CoolType.TEST

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
            dropSchema()
        }
    }

    @Test
    fun simplePassThrough2Test() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

            val testModel = TestModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()

            val subTestChild = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestModel.Child>(TestModel.Child.getDefaultInstance(), entityService, subTestChild.id, protoService)

            // assert
            Assert.assertTrue(record is TestModel.Child)
            Assert.assertTrue(record.testDisplay.equals("second display"))
        }
        finally {
            dropSchema()
        }
    }

    @Test
    fun verifyTypesSavedAndReturnedCorrectlyTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

            val testModel = TestModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
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

            val subTestChild = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestModel.SimpleInsertTest>(TestModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService)

            // assert
            Assert.assertTrue(record is TestModel.SimpleInsertTest)
            Assert.assertTrue(record.coolType.equals(TestModel.SimpleInsertTest.CoolType.SURPRISED))
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
            dropSchema()
        }
    }

    @Test
    fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

            val testModel = TestModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
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

            val subTestChild = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestModel.SimpleInsertTest>(TestModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService)

            // assert
            Assert.assertTrue(record is TestModel.SimpleInsertTest)
            Assert.assertTrue(record.coolTypesCount == 2)
            Assert.assertTrue(record.coolTypesList.any { it.equals(TestModel.SimpleInsertTest.CoolType.SURPRISED) })
            Assert.assertTrue(record.coolTypesList.any { it.equals(TestModel.SimpleInsertTest.CoolType.TEST) })
        }
        finally {
            dropSchema()
        }
    }

    @Test
    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()

            val testModel = TestModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
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

            val subTestChild = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            val subTestChild2 = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("third display")
            val subTestChild3 = TestModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("fourth display")
            testModel.addChilds(subTestChild)
            testModel.addChilds(subTestChild2)
            testModel.addChilds(subTestChild3)

            val firstCoolType = TestModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build())
            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestModel.SimpleInsertTest>(TestModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService)

            // assert
            Assert.assertTrue(record is TestModel.SimpleInsertTest)
            Assert.assertTrue(record.childsCount == 3)
            Assert.assertTrue(record.childsList.any { it.testDisplay.equals(subTestChild.testDisplay) && it.id.equals(subTestChild.id) })
            Assert.assertTrue(record.childsList.any { it.testDisplay.equals(subTestChild2.testDisplay) && it.id.equals(subTestChild2.id) })
            Assert.assertTrue(record.childsList.any { it.testDisplay.equals(subTestChild3.testDisplay) && it.id.equals(subTestChild3.id) })
        }
        finally {
            dropSchema()
        }
    }

    companion object {
        private var host:String? = null
        private var userName:String? = null
        private var password:String? = null
        private var schema:String? = null

        fun dropSchema() {
            val connection = DriverManager.getConnection(
                    "jdbc:mysql://$host?user=$userName&password=$password&autoReconnect=true")
            val statement = connection.prepareStatement("drop database if exists $schema")
            statement.executeUpdate()
            statement.close()
            connection.close()
        }

        fun getConnectionInfo() {
            if (host == null) {
                val properties = Properties()
                val mysqlStream = MySQLProtoTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                host = properties.getProperty("host")
                password = properties.getProperty("password")
                userName = properties.getProperty("userName")
                schema = "test_${UUID.randomUUID().toString().replace("-", "")}"
            }
        }
    }
}
