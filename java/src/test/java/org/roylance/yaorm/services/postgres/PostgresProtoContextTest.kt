package org.roylance.yaorm.services.postgres

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.TestingModelV2
import org.roylance.yaorm.TestingModelV3
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.*
import java.util.*

class PostgresProtoContextTest {
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
                    sourceConnection.connectionSource,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
            val protoService = TestModelGMBuilder()
            val protoContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    protoService,
                    entityService,
                    "TestingModel", HashMap(),
                    TestBase64Service())

            protoContext.entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())
            protoContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            protoContext.handleMigrations()

            // act
            val manyDags = protoContext
                    .entityMessageService
                    .getMany(TestingModel.Dag.getDefaultInstance())

            // assert
            Assert.assertTrue(manyDags.size == 0)
        }
        finally {
        }
    }

    @Test
    fun migrationAddColumnTest() {
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
                    sourceConnection.connectionSource,
                    false)
            val generatorService = PostgresGeneratorService()

            val contextName = "TestingModel"

            // act
            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap(),
                    TestBase64Service())

            firstContext.entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())
            firstContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            firstContext.handleMigrations()

            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap(),
                    TestBase64Service())

            secondContext.handleMigrations()

            val simpleDag = TestingModelV2.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setNewField1("WOW, THIS IS NEW")

            secondContext.entityMessageService.merge(simpleDag.build())

            // assert
            val foundDag = secondContext.entityMessageService.get(simpleDag.build(), simpleDag.id)

            Assert.assertTrue(foundDag != null)
            Assert.assertTrue(foundDag!!.id.equals(simpleDag.id))
            Assert.assertTrue(foundDag.newField1.equals(simpleDag.newField1))
        }
        finally {
        }
    }

    @Test
    fun migrationRemoveColumnTest() {
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
                    sourceConnection.connectionSource,
                    false)
            val generatorService = PostgresGeneratorService()

            val contextName = "TestingModel"

            // act
            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap(),
                    TestBase64Service())

            secondContext.entityMessageService.dropAndCreateEntireSchema(TestingModelV2.getDescriptor())
            secondContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            secondContext.handleMigrations()

            val simpleDag = TestingModelV2.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("awesome display")
                    .setNewField1("WOW, THIS IS NEW")

            secondContext.entityMessageService.merge(simpleDag.build())

            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap(),
                    TestBase64Service())

            firstContext.handleMigrations()

            // assert
            val foundDag = firstContext.entityMessageService.get(TestingModel.Dag.getDefaultInstance(), simpleDag.id)

            Assert.assertTrue(foundDag != null)
            Assert.assertTrue(foundDag!!.display.equals(simpleDag.display))
            Assert.assertTrue(foundDag.id.equals(simpleDag.id))
        }
        finally {
        }
    }

    @Test
    fun migrationAddTableTest() {
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
                    sourceConnection.connectionSource,
                    false)
            val generatorService = PostgresGeneratorService()
            val contextName = "TestingModel"

            // act
            val thirdVersion = EntityProtoContext(
                    TestingModelV3.getDescriptor(),
                    TestModelGMv3Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap(),
                    TestBase64Service())

            thirdVersion.entityMessageService.dropAndCreateEntireSchema(TestingModelV3.getDescriptor())
            thirdVersion.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            thirdVersion.handleMigrations()

            val simpleDag = TestingModelV3.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("awesome display")
                    .setNewField1("WOW, THIS IS NEW")

            thirdVersion.entityMessageService.merge(simpleDag.build())

            val firstVersion = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap(),
                    TestBase64Service())

            firstVersion.handleMigrations()

            // assert
            val foundDag = firstVersion.entityMessageService.get(TestingModel.Dag.getDefaultInstance(), simpleDag.id)

            Assert.assertTrue(foundDag != null)
            Assert.assertTrue(foundDag!!.display.equals(simpleDag.display))
            Assert.assertTrue(foundDag.id.equals(simpleDag.id))

            val migrationsFound = firstVersion.entityMessageService.getMany(YaormModel.Migration.getDefaultInstance())
            Assert.assertTrue(migrationsFound.size == 2)
        }
        finally {
        }
    }
}
