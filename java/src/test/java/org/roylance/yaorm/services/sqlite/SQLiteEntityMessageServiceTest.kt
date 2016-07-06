package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityMessageService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.DagBuilder
import org.roylance.yaorm.utilities.TestModelGeneratedMessageBuilder
import java.io.File
import java.util.*

class SQLiteEntityMessageServiceTest {
    @Test
    fun simpleCreateTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(null, granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            // act
            val manyDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())

            // assert
            Assert.assertTrue(manyDags.size == 0)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun simpleLoadAndCreateTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(null, granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            val newDag = DagBuilder().build()
            entityMessageService.merge(newDag)

            // act
            val manyDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())
            val anotherDag = DagBuilder().build()
            entityMessageService.merge(anotherDag)
            val moreDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())

            // assert
            Assert.assertTrue(manyDags.size == 1)
            Assert.assertTrue(moreDags.size == 2)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun complexLoadAndCreateTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(null, granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            val newDag = DagBuilder().build()
            entityMessageService.merge(newDag)

            // act
            val builder = newDag.toBuilder()
            val firstUncompleted = builder.uncompletedTasksBuilderList[0]
            builder.removeUncompletedTasks(0)
            builder.addProcessingTasks(firstUncompleted.setExecutionDate(Date().time).setStartDate(Date().time))
            entityMessageService.merge(builder.build())

            // assert
            val moreDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())
            Assert.assertTrue(moreDags.size == 1)
            val firstDag = moreDags.first()

            Assert.assertTrue(firstDag.uncompletedTasksCount == 9)
            Assert.assertTrue(firstDag.processingTasksCount == 1)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun complexLoadAndCreate2Test() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(null, granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            val newDag = DagBuilder().build()
            entityMessageService.merge(newDag)

            // act
            val builder = newDag.toBuilder()
            while (builder.uncompletedTasksCount > 0) {
                val lastIndex = builder.uncompletedTasksCount - 1
                val firstUncompleted = builder.uncompletedTasksBuilderList[lastIndex]
                builder.removeUncompletedTasks(lastIndex)
                builder.addProcessingTasks(firstUncompleted.setExecutionDate(Date().time).setStartDate(Date().time))
                entityMessageService.merge(builder.build())
            }

            // assert
            val moreDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())
            Assert.assertTrue(moreDags.size == 1)
            val firstDag = moreDags.first()

            Assert.assertTrue(firstDag.uncompletedTasksCount == 0)
            Assert.assertTrue(firstDag.processingTasksCount == 10)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun complexLoadAndCreateProtectedTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath, "mike", "testing")
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(null, granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGeneratedMessageBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            val newDag = DagBuilder().build()
            entityMessageService.merge(newDag)

            // act
            val builder = newDag.toBuilder()
            while (builder.uncompletedTasksCount > 0) {
                val lastIndex = builder.uncompletedTasksCount - 1
                val firstUncompleted = builder.uncompletedTasksBuilderList[lastIndex]
                builder.removeUncompletedTasks(lastIndex)
                builder.addProcessingTasks(firstUncompleted.setExecutionDate(Date().time).setStartDate(Date().time))
                entityMessageService.merge(builder.build())
            }

            // assert
            val moreDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())
            Assert.assertTrue(moreDags.size == 1)
            val firstDag = moreDags.first()

            Assert.assertTrue(firstDag.uncompletedTasksCount == 0)
            Assert.assertTrue(firstDag.processingTasksCount == 10)
        }
        finally {
            database.deleteOnExit()
        }
    }
}
