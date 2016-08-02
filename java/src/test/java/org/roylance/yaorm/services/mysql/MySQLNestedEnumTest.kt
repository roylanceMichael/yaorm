package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.NestedEnumTest
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
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
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(granularDatabaseService, mySqlGeneratorService)
            val protoContext = EntityProtoContext(
                    NestedEnumTest.getDescriptor(),
                    NestedEnumGMBuilder(),
                    entityService,
                    "NestedModel", HashMap(),
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
}