package org.roylance.yaorm.services.mysql

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityMessageService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.TestModelGeneratedMessageBuilder
import java.sql.DriverManager
import java.util.*

class MySQLEntityMessageServiceTest {
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
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()
            entityProtoMessageService.createEntireSchema(testModel.build())

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

            // act
            val result = entityProtoMessageService.merge(testModel.build())

            // assert
            Assert.assertTrue(result)
            val foundMessage = entityProtoMessageService.get(testModel.build(), testModel.id)

            Assert.assertTrue(foundMessage != null)
            Assert.assertTrue(foundMessage.id.equals(testModel.id))
            Assert.assertTrue(foundMessage.coolType.equals(testModel.coolType))
            Assert.assertTrue(foundMessage.display.equals(testModel.display))
            Assert.assertTrue(foundMessage.testInt32.equals(testModel.testInt32))
            Assert.assertTrue(foundMessage.testInt64.equals(testModel.testInt64))
            Assert.assertTrue(foundMessage.testUint32.equals(testModel.testUint32))
            Assert.assertTrue(foundMessage.testUint64.equals(testModel.testUint64))
            Assert.assertTrue(foundMessage.testSint32.equals(testModel.testSint32))
            Assert.assertTrue(foundMessage.testSint64.equals(testModel.testSint64))
            Assert.assertTrue(foundMessage.testFixed32.equals(testModel.testFixed32))
            Assert.assertTrue(foundMessage.testFixed64.equals(testModel.testFixed64))
            Assert.assertTrue(foundMessage.testSfixed32.equals(testModel.testSfixed32))
            Assert.assertTrue(foundMessage.testSfixed64.equals(testModel.testSfixed64))
            Assert.assertTrue(foundMessage.testBool.equals(testModel.testBool))
            Assert.assertTrue(foundMessage.testBytes.equals(testModel.testBytes))
            Assert.assertTrue(foundMessage.testDouble.equals(testModel.testDouble))
            Assert.assertTrue(foundMessage.testFloat.equals(testModel.testFloat))
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
                val mysqlStream = MySQLEntityMessageServiceTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                host = properties.getProperty("host")
                password = properties.getProperty("password")
                userName = properties.getProperty("userName")
                schema = "test_${UUID.randomUUID().toString().replace("-", "")}"
            }
        }
    }
}
