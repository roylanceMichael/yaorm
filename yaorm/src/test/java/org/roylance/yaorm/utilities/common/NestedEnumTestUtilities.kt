package org.roylance.yaorm.utilities.common

import org.junit.Assert
import org.roylance.common.service.IBuilder
import org.roylance.yaorm.ComplexModel
import org.roylance.yaorm.NestedEnumTest
import org.roylance.yaorm.services.EntityProtoContext
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.utilities.TestBase64Service
import java.util.*

object NestedEnumTestUtilities {
    fun simplePassThroughExecutionsTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val protoContext = EntityProtoContext(
                    NestedEnumTest.getDescriptor(),
                    entityService,
                    HashMap(),
                    TestBase64Service())
            protoContext.entityMessageService.dropAndCreateEntireSchema(NestedEnumTest.getDescriptor())

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
            Assert.assertTrue(protoContext.entityMessageService.getReport().executionsCount > 0)

            println(protoContext.entityMessageService.getReport().executionsList.map { it.rawSql }.joinToString("\n\n"))
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simplePassThroughTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val protoContext = EntityProtoContext(
                    NestedEnumTest.getDescriptor(),
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.entityMessageService.dropAndCreateEntireSchema(NestedEnumTest.getDescriptor())

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
            entityService.close()
            cleanup?.build()
        }
    }

    fun simplePassThroughTest2(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
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
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleTablesTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null, schemaName: String) {
        // arrange
        try {
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
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
            val tableNames = entityService.getTableNames(schemaName)

            // assert
            println(tableNames)
            assert(tableNames.size > 1)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleTableDefinitionTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null, schema: String) {
        // arrange
        try {
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
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
            val tableDefinition = entityService.getTableDefinition(schema, ComplexModel.Answer.getDescriptor().name)

            // assert
            println(tableDefinition)
            assert(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }

    fun simpleTableDefinitionNullableTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null, schema: String) {
        // arrange
        try {
            val protoContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
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
            val tableDefinition = entityService.getTableDefinition(schema, ComplexModel.Answer.getDescriptor().name)

            // assert
            println(tableDefinition)
            assert(tableDefinition.columnDefinitionsCount > 0)
        }
        finally {
            entityService.close()
            cleanup?.build()
        }
    }
}