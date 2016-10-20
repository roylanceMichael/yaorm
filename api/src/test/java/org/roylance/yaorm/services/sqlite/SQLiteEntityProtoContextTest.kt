package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.*
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.*
import java.io.File
import java.util.*

class SQLiteEntityProtoContextTest {
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
            val protoService = TestModelGMBuilder()
            val protoContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    protoService,
                    entityService,
                    HashMap(),
                    TestBase64Service())

            protoContext.handleMigrations()

            // act
            val manyDags = protoContext
                    .entityMessageService
                    .getMany(TestingModel.Dag.getDefaultInstance())

            // assert
            Assert.assertTrue(manyDags.size == 0)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun migrationAddColumnTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            // act
            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
                    HashMap(),
                    TestBase64Service())

            firstContext.handleMigrations()

            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
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
            Assert.assertTrue(foundDag!!.id.equals(simpleDag.id))
            Assert.assertTrue(foundDag.newField1.equals(simpleDag.newField1))
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun migrationRemoveColumnTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            // act
            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
                    HashMap(),
                    TestBase64Service())

            secondContext.handleMigrations()

            val simpleDag = TestingModelV2.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("awesome display")
                    .setNewField1("WOW, THIS IS NEW")

            secondContext.entityMessageService.merge(simpleDag.build())

            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
                    HashMap(),
                    TestBase64Service())

            firstContext.handleMigrations()

            // assert
            val foundDag = firstContext.entityMessageService.get(TestingModel.Dag.getDefaultInstance(), simpleDag.id)

            Assert.assertTrue(foundDag != null)
            Assert.assertTrue(foundDag!!.display.equals(simpleDag.display))
            Assert.assertTrue(foundDag.id.equals(simpleDag.id))
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun migrationAddTableTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            // act
            val thirdVersion = EntityProtoContext(
                    TestingModelV3.getDescriptor(),
                    TestModelGMv3Builder(),
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
                    HashMap(),
                    TestBase64Service())

            thirdVersion.handleMigrations()

            val simpleDag = TestingModelV3.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("awesome display")
                    .setNewField1("WOW, THIS IS NEW")

            thirdVersion.entityMessageService.merge(simpleDag.build())

            val firstVersion = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
                    HashMap(),
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
            database.deleteOnExit()
        }
    }

    @Test
    fun complexMergeTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
                    HashMap(),
                    TestBase64Service())

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
            database.deleteOnExit()
        }
    }

    @Test
    fun complexMerge2Test() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    EntityProtoService(granularDatabaseService, sqliteGeneratorService),
                    HashMap(),
                    TestBase64Service())

            complexModelContext.handleMigrations()

            val firstView = ComplexModel.View.newBuilder()
                    .setId("first_view")
                    .setTitle("cool title")

            val firstForm = ComplexModel.Form.newBuilder()
                    .setId("first_form")
                    .setDisplay("cool display")

            val firstQuestion = ComplexModel.Question.newBuilder()
                    .setId("first_question")
                    .setDisplay("first question")

            val secondQuestion = ComplexModel.Question.newBuilder()
                    .setId("second_question")
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
            database.deleteOnExit()
        }
    }
}
