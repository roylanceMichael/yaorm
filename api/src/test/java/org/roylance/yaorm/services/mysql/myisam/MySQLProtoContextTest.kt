package org.roylance.yaorm.services.mysql.myisam

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.*
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.mysql.MySQLConnectionSourceFactory
import org.roylance.yaorm.services.mysql.MySQLGeneratorService
import org.roylance.yaorm.services.proto.EntityProtoContext
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.*
import java.util.*

class MySQLProtoContextTest {
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
            Assert.assertTrue(manyDags.isEmpty())
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun migrationAddColumnTest() {
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

            // act
            val firstContext = EntityProtoContext(
                    TestingModel.getDescriptor(),
                    TestModelGMBuilder(),
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
                    HashMap(),
                    TestBase64Service())

            firstContext.handleMigrations()

            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
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
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun migrationRemoveColumnTest() {
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

            // act
            val secondContext = EntityProtoContext(
                    TestingModelV2.getDescriptor(),
                    TestModelGMv2Builder(),
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
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
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
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
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun migrationAddTableTest() {
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

            // act
            val thirdVersion = EntityProtoContext(
                    TestingModelV3.getDescriptor(),
                    TestModelGMv3Builder(),
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
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
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
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
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun complexMergeTest() {
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

            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
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
            ConnectionUtilities.dropMySQLSchema()
        }
    }

    @Test
    fun complexMerge2Test() {
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

            val complexModelContext = EntityProtoContext(
                    ComplexModel.getDescriptor(),
                    ComplexModelBuilder,
                    EntityProtoService(granularDatabaseService, mySqlGeneratorService),
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
            ConnectionUtilities.dropMySQLSchema()
        }
    }
}
