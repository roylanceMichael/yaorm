package org.roylance.yaorm.services.postgres

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.TestingModelv2
import org.roylance.yaorm.TestingModelv3
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.TestModelGMBuilder
import org.roylance.yaorm.utilities.TestModelGMv2Builder
import org.roylance.yaorm.utilities.TestModelGMv3Builder
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
                    "TestingModel", HashMap())

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
                    contextName, HashMap())

            firstContext.entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())
            firstContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            firstContext.handleMigrations()

            val secondContext = EntityProtoContext(
                    TestingModelv2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap())

            secondContext.handleMigrations()

            val simpleDag = TestingModelv2.Dag.newBuilder()
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
                    TestingModelv2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap())

            secondContext.entityMessageService.dropAndCreateEntireSchema(TestingModelv2.getDescriptor())
            secondContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            secondContext.handleMigrations()

            val simpleDag = TestingModelv2.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("awesome display")
                    .setNewField1("WOW, THIS IS NEW")

            secondContext.entityMessageService.merge(simpleDag.build())

            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap())

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
                    TestingModelv3.getDescriptor(),
                    TestModelGMv3Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap())

            thirdVersion.entityMessageService.dropAndCreateEntireSchema(TestingModelv3.getDescriptor())
            thirdVersion.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            thirdVersion.handleMigrations()

            val simpleDag = TestingModelv3.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("awesome display")
                    .setNewField1("WOW, THIS IS NEW")

            thirdVersion.entityMessageService.merge(simpleDag.build())

            val firstVersion = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    contextName, HashMap())

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
