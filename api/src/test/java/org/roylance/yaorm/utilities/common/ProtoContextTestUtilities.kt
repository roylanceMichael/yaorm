package org.roylance.yaorm.utilities.common

import org.junit.Assert
import org.roylance.common.service.IBuilder
import org.roylance.yaorm.*
import org.roylance.yaorm.services.EntityProtoContext
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.utilities.*
import java.util.*

object ProtoContextTestUtilities {
    fun simplePassThroughTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            cleanup?.build()
        }
    }

    fun migrationAddColumnTest(entityService: IEntityService, entityService2: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {

            // act
            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    entityService,
                    HashMap(),
                    TestBase64Service())

            firstContext.entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())
            firstContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            firstContext.handleMigrations()

            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    entityService2,
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
            cleanup?.build()
        }
    }

    fun migrationRemoveColumnTest(entityService: IEntityService, entityService2: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            // act
            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    entityService,
                    HashMap(),
                    TestBase64Service())

            secondContext.handleMigrations()

            secondContext.entityMessageService.dropAndCreateEntireSchema(TestingModelV2.getDescriptor())
            secondContext.entityMessageService.dropAndCreateEntireSchema(YaormModel.Migration.getDefaultInstance())

            val simpleDag = TestingModelV2.Dag.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setDisplay("awesome display")
                    .setNewField1("WOW, THIS IS NEW")

            secondContext.entityMessageService.merge(simpleDag.build())

            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    entityService2,
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
            cleanup?.build()
        }
    }

    fun migrationAddTableTest(entityService: IEntityService, entityService2: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            // act
            val thirdVersion = EntityProtoContext(
                    TestingModelV3.getDescriptor(),
                    TestModelGMv3Builder(),
                    entityService,
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
                    entityService2,
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
            cleanup?.build()
        }
    }

    fun complexMergeTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    entityService,
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
            cleanup?.build()
        }
    }

    fun complexMerge2Test(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    entityService,
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
            cleanup?.build()
        }
    }
}