package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.ProtobufUtils
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
                schema = "test_$${UUID.randomUUID().toString().replace("-", "")}"
            }
        }
    }
}
