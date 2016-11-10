package org.roylance.yaorm.services.postgres

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.ProtobufUtils
import org.roylance.yaorm.utilities.TestModelGMBuilder
import org.roylance.yaorm.utilities.TestingModelUtilities
import java.util.*

class PostgresProtoTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
        }
    }

    @Test
    fun simplePassThrough2Test() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
            val protoService = TestModelGMBuilder()

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
        }
    }

    @Test
    fun verifyTypesSavedAndReturnedCorrectlyTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
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
        }
    }

    @Test
    fun verifyRepeatedNumsSavedAndReturnedCorrectlyTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
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
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(TestingModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService, HashMap(), HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolTypesCount == 2)
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.SURPRISED })
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.TEST })
        }
        finally {
        }
    }

    @Test
    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
            val protoService = TestModelGMBuilder()

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
                    protoService,
                    HashMap(), HashMap())

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
        }
    }

    @Test
    fun metaDataTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModelUtilities.buildSampleRootObject()

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
        }
    }

    @Test
    fun simpleSchemaTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
            val schemaNames = entityService.getSchemaNames()

            // assert
            println(schemaNames)
            Assert.assertTrue(schemaNames.isNotEmpty())
        }
        finally {
        }
    }

    @Test
    fun simpleTableNamesTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
            val tableNames = entityService.getTableNames("public")

            // assert
            println(tableNames)
            Assert.assertTrue(tableNames.isNotEmpty())
        }
        finally {
        }
    }

    @Test
    fun simpleTableDefinitionTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
            val tableDefinition = entityService.getTableDefinition("public", "simpleinserttest")

            // assert
            println(tableDefinition)
            Assert.assertTrue(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
        }
    }

    ////
    @Test
    fun simplePassThroughEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
        }
    }

    @Test
    fun simplePassThrough2EmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
            val protoService = TestModelGMBuilder()

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
        }
    }

    @Test
    fun verifyTypesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
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
        }
    }

    @Test
    fun verifyRepeatedNumsSavedAndReturnedCorrectlyEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
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
            val record = ProtobufUtils.getProtoObjectFromBuilderSingle<TestingModel.SimpleInsertTest>(TestingModel.SimpleInsertTest.getDefaultInstance(), entityService, testModel.id, protoService, HashMap(), HashMap())

            // assert
            Assert.assertTrue(record is TestingModel.SimpleInsertTest)
            Assert.assertTrue(record!!.coolTypesCount == 2)
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.SURPRISED })
            Assert.assertTrue(record.coolTypesList.any { it == TestingModel.SimpleInsertTest.CoolType.TEST })
        }
        finally {
        }
    }

    @Test
    fun verifyRepeatedMessagesSavedAndReturnedCorrectlyEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
            val protoService = TestModelGMBuilder()

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
                    protoService,
                    HashMap(), HashMap())

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
        }
    }

    @Test
    fun metaDataEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModelUtilities.buildSampleRootObject()

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
        }
    }

    @Test
    fun simpleSchemaEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
            val schemaNames = entityService.getSchemaNames()

            // assert
            println(schemaNames)
            Assert.assertTrue(schemaNames.isNotEmpty())
        }
        finally {
        }
    }

    @Test
    fun simpleTableNamesEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
            val tableNames = entityService.getTableNames("public")

            // assert
            println(tableNames)
            Assert.assertTrue(tableNames.isNotEmpty())
        }
        finally {
        }
    }

    @Test
    fun simpleTableDefinitionEmptyAsNullTest() {
        // arrange
        ConnectionUtilities.getPostgresConnectionInfo()
        try {
            val sourceConnection = PostgresConnectionSourceFactory(
                    ConnectionUtilities.postgresHost!!,
                    ConnectionUtilities.postgresPort!!,
                    ConnectionUtilities.postgresDatabase!!,
                    ConnectionUtilities.postgresUserName!!,
                    ConnectionUtilities.postgresPassword!!,
                    false)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, generatorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTestDisplay("second display")
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
            val tableDefinition = entityService.getTableDefinition("public", "simpleinserttest")

            // assert
            println(tableDefinition)
            Assert.assertTrue(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
        }
    }
}
