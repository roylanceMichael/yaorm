package org.roylance.yaorm.services.postgres

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.*
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.utilities.*
import java.io.File
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
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, generatorService)
            val protoService = TestModelGMBuilder()
            val protoContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    protoService,
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())
            protoContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            protoContext.handleMigrations()

            // act
            val manyDags = protoContext
                    .entityMessageService
                    .getMany(TestingModel.Dag.getDefaultInstance())

            // assert
            Assert.assertTrue(manyDags.isEmpty())
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
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()

            // act
            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    HashMap(),
                    TestBase64Service())

            firstContext.entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())
            firstContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            firstContext.handleMigrations()

            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    HashMap(),
                    TestBase64Service())

            secondContext.handleMigrations()

            val simpleDag = TestingModelV2.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setNewField1("WOW, THIS IS NEW")

            secondContext.entityMessageService.merge(simpleDag.build())

            // assert
            val foundDag = secondContext.entityMessageService.get(simpleDag.build(), simpleDag.id)

            Assert.assertTrue(foundDag != null)
            Assert.assertTrue(foundDag!!.id == simpleDag.id)
            Assert.assertTrue(foundDag.newField1 == simpleDag.newField1)
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
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()

            // act
            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    HashMap(),
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
                    HashMap(),
                    TestBase64Service())

            firstContext.handleMigrations()

            // assert
            val foundDag = firstContext.entityMessageService.get(TestingModel.Dag.getDefaultInstance(), simpleDag.id)

            Assert.assertTrue(foundDag != null)
            Assert.assertTrue(foundDag!!.display == simpleDag.display)
            Assert.assertTrue(foundDag.id == simpleDag.id)
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
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()

            // act
            val thirdVersion = EntityProtoContext(
                    TestingModelV3.getDescriptor(),
                    TestModelGMv3Builder(),
                    EntityProtoService(granularDatabaseService, generatorService),
                    HashMap(),
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
                    HashMap(),
                    TestBase64Service())

            firstVersion.handleMigrations()

            // assert
            val foundDag = firstVersion.entityMessageService.get(TestingModel.Dag.getDefaultInstance(), simpleDag.id)

            Assert.assertTrue(foundDag != null)
            Assert.assertTrue(foundDag!!.display == simpleDag.display)
            Assert.assertTrue(foundDag.id == simpleDag.id)

            val migrationsFound = firstVersion.entityMessageService.getMany(YaormModel.Migration.getDefaultInstance())
            Assert.assertTrue(migrationsFound.size == 2)
        }
        finally {
        }
    }

    @Test
    fun complexMergeTest() {
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

            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    EntityProtoService(granularDatabaseService, generatorService),
                    HashMap(),
                    TestBase64Service())

            complexModelContext.entityMessageService.dropAndCreateEntireSchema(ComplexModel.getDescriptor())
            complexModelContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            complexModelContext.handleMigrations()

            val firstView = ComplexModel.View.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTitle("cool title")

            val firstForm = ComplexModel.Form.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("cool display")

            val firstQuestion = ComplexModel.Question.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("first question")

            val secondQuestion = ComplexModel.Question.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("second question")

            firstForm.addQuestions(firstQuestion)
            firstForm.addQuestions(secondQuestion)
            firstView.addForms(firstForm)

            // act
            complexModelContext.entityMessageService.merge(firstView.build())

            // assert
            val foundViews = complexModelContext.entityMessageService.getMany(ComplexModel.View.getDefaultInstance())

            Assert.assertTrue(foundViews.size == 1)
            Assert.assertTrue(foundViews[0].formsCount == 1)

            val foundForm = foundViews[0].formsList[0]
            Assert.assertTrue(foundForm.questionsCount == 2)
        }
        finally {
        }
    }

    @Test
    fun complexMerge2Test() {
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

            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    EntityProtoService(granularDatabaseService, generatorService),
                    HashMap(),
                    TestBase64Service())

            complexModelContext.entityMessageService.dropAndCreateEntireSchema(ComplexModel.getDescriptor())
            complexModelContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            complexModelContext.handleMigrations()

            val firstView = ComplexModel.View.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTitle("cool title")

            val firstForm = ComplexModel.Form.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("cool display")

            val firstQuestion = ComplexModel.Question.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("first question")

            val secondQuestion = ComplexModel.Question.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("second question")

            firstForm.addQuestions(firstQuestion)
            firstForm.addQuestions(secondQuestion)
            firstView.addForms(firstForm)

            complexModelContext.entityMessageService.merge(firstView.build())

            // act
            firstView.clearForms()
            firstForm.clearQuestions()
            firstForm.addQuestions(firstQuestion)
            firstView.addForms(firstForm)

            complexModelContext.entityMessageService.merge(firstView.build())

            // assert
            val foundViews = complexModelContext.entityMessageService.getMany(ComplexModel.View.getDefaultInstance())

            Assert.assertTrue(foundViews.size == 1)
            Assert.assertTrue(foundViews[0].formsCount == 1)

            val foundForm = foundViews[0].formsList[0]
            Assert.assertTrue(foundForm.questionsCount == 1)
        }
        finally {
        }
    }
}
