package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.ComplexModel
import org.roylance.yaorm.NestedEnumTest
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.ComplexModelBuilder
import org.roylance.yaorm.utilities.NestedEnumGMBuilder
import org.roylance.yaorm.utilities.TestBase64Service
import org.roylance.yaorm.utilities.TestModelGMBuilder
import java.io.File
import java.util.*

class SQLiteNestedEnumTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = NestedEnumGMBuilder()
            val protoContext = EntityProtoContext(
                    NestedEnumTest.getDescriptor(),
                    protoService,
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
            database.deleteOnExit()
        }
    }

    @Test
    fun simplePassThroughTest2() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = ComplexModelBuilder
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    protoService,
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.handleMigrations()

            val firstFile = ComplexModel.MappedFile.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("first")

            val secondFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("second")
                    .setParent(firstFile)

            firstFile.addChildren(secondFile)

            val thirdFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("third")
                    .setParent(secondFile)

            secondFile.addChildren(thirdFile)

            // act
            protoContext.entityMessageService.merge(firstFile.build())

            // assert
            assert(true)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun simpleTablesTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = ComplexModelBuilder
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    protoService,
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.handleMigrations()

            val firstFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("first")

            val secondFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("second")
                    .setParent(firstFile)

            firstFile.addChildren(secondFile)

            val thirdFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("third")
                    .setParent(secondFile)

            secondFile.addChildren(thirdFile)
            protoContext.entityMessageService.merge(firstFile.build())

            // act
            val tableNames = entityService.getTableNames("doesn't matter")

            // assert
            println(tableNames)
            assert(tableNames.size > 1)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun simpleTableDefinitionTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = ComplexModelBuilder
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    protoService,
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.handleMigrations()

            val firstFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("first")

            val secondFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("second")
                    .setParent(firstFile)

            firstFile.addChildren(secondFile)

            val thirdFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("third")
                    .setParent(secondFile)

            secondFile.addChildren(thirdFile)
            protoContext.entityMessageService.merge(firstFile.build())

            // act
            val tableDefinition = entityService.getTableDefinition("doesn't matter", ComplexModel.Answer.getDescriptor().name)

            // assert
            println(tableDefinition)
            assert(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun simpleTableDefinitionNullableTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService(500, true)
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = ComplexModelBuilder
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    protoService,
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.handleMigrations()

            val firstFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("first")

            val secondFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("second")
                    .setParent(firstFile)

            firstFile.addChildren(secondFile)

            val thirdFile = ComplexModel.MappedFile.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName("third")
                    .setParent(secondFile)

            secondFile.addChildren(thirdFile)
            protoContext.entityMessageService.merge(firstFile.build())

            // act
            val tableDefinition = entityService.getTableDefinition("doesn't matter", ComplexModel.Answer.getDescriptor().name)

            // assert
            println(tableDefinition)
            assert(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
            database.deleteOnExit()
        }
    }
}