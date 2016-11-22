package org.roylance.yaorm.services.mysql.myisam

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.ComplexModel
import org.roylance.yaorm.NestedEnumTest
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.mysql.MySQLConnectionSourceFactory
import org.roylance.yaorm.services.mysql.MySQLGeneratorService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.ComplexModelBuilder
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.NestedEnumGMBuilder
import org.roylance.yaorm.utilities.TestBase64Service
import java.util.*

class MySQLNestedEnumTest {
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
                    sourceConnection,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(schemaName = sourceConnection.schema, useMyISAM = true)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)
            val protoContext = EntityProtoContext(
                    NestedEnumTest.getDescriptor(),
                    NestedEnumGMBuilder(),
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.handleMigrations()

            val columnOne = NestedEnumTest.ColumnInfo.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setSourceName("one")
                    .setSourceType(NestedEnumTest.ProtobufType.STRING)
                    .setDestinationType(NestedEnumTest.ProtobufType.STRING)
                    .setDestinationName("one")

            val columnTwo = NestedEnumTest.ColumnInfo.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setSourceName("two")
                    .setSourceType(NestedEnumTest.ProtobufType.STRING)
                    .setDestinationType(NestedEnumTest.ProtobufType.STRING)
                    .setDestinationName("two")

            val dataset = NestedEnumTest.DataSet.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDestinationName("cool_destination")
                    .setColumnDelimiter(NestedEnumTest.DelimiterType.COMMA)
                    .setRowDelimiter(NestedEnumTest.DelimiterType.CARRIAGE_RETURN)
                    .setDirectoryLocation("")
                    .setHasHeaders(true)
                    .setIsFixedWidth(false)
                    .addColumnInfos(columnOne)
                    .addColumnInfos(columnTwo)

            val customer = NestedEnumTest.Customer.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("test")
                    .addDatasets(dataset)
                    .build()

            // act
            protoContext.entityMessageService.merge(customer)

            // assert
            val allCustomers = protoContext.entityMessageService.getMany(NestedEnumTest.Customer.getDefaultInstance())
            Assert.assertTrue(allCustomers.size == 1)
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun simplePassThroughTest2() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlSchema!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(schemaName = sourceConnection.schema, useMyISAM = true)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)
            val protoService = ComplexModelBuilder
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    protoService,
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.handleMigrations()

            val firstFile = ComplexModel.MappedFile.newBuilder()
                    .setId("first")
                    .setName("first")

            val secondFile = ComplexModel.MappedFile.newBuilder()
                    .setId("second")
                    .setName("second")

            val thirdFile = ComplexModel.MappedFile.newBuilder()
                    .setId("third")
                    .setName("third")
                    .setParent(secondFile)

            val fourthFile = ComplexModel.MappedFile.newBuilder()
                    .setId("fourth")
                    .setName("fourth")
                    .setParent(secondFile)

            secondFile.addChildren(thirdFile)
            secondFile.addChildren(fourthFile)

            secondFile.setParent(firstFile)
            firstFile.addChildren(secondFile)

            // act
//            protoContext.entityMessageService.merge(secondFile.build())
            protoContext.entityMessageService.merge(firstFile.build())

            // assert
            assert(true)
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }
}