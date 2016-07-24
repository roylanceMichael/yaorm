package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityMessageService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.DagBuilder
import org.roylance.yaorm.utilities.TestModelGMBuilder
import org.roylance.yaorm.utilities.YaormUtils
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
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
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
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
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
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
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
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
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
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
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
    fun simpleUserAndUserDeviceTestTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath, "mike", "testing")
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
            entityMessageService.createEntireSchema(TestingModel.getDescriptor())

            val newUser = TestingModel.User.newBuilder().setId(UUID.randomUUID().toString()).setDisplay("ok")
            val userDevice = TestingModel.UserDevice.newBuilder().setId(UUID.randomUUID().toString()).setUser(newUser)

            // act
            entityMessageService.merge(userDevice.build())

            // assert
            val users = entityMessageService.getMany(TestingModel.User.getDefaultInstance())

            Assert.assertTrue(users.size == 1)
            Assert.assertTrue(users.first().id.equals(newUser.id))
            Assert.assertTrue(users.first().display.equals(newUser.display))
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun simpleGetTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath, "mike", "testing")
            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            // act
            val foundDag = entityMessageService.get(TestingModel.Dag.getDefaultInstance(), UUID.randomUUID().toString())

            // assert
            Assert.assertTrue(foundDag == null)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun simpleIndexTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(
                    database.absolutePath,
                    "mike",
                    "testing")

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()

            val customIndexes = HashMap<String, YaormModel.Index>()
            val index = YaormModel.Index
                    .newBuilder()
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(YaormUtils.IdName))
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(TestingModel.Dag.getDescriptor().findFieldByNumber(TestingModel.Dag.DISPLAY_FIELD_NUMBER).name))
                    .build()
            customIndexes[TestingModel.Dag.getDescriptor().name] = index

            val entityMessageService = EntityMessageService(protoService, entityService, customIndexes)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            // act
            val foundDag = entityMessageService.get(TestingModel.Dag.getDefaultInstance(), UUID.randomUUID().toString())

            // assert
            Assert.assertTrue(foundDag == null)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun bulkInsertTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(
                    database.absolutePath,
                    "mike",
                    "testing")

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityProtoService(granularDatabaseService, sqliteGeneratorService)
            val protoService = TestModelGMBuilder()

            val customIndexes = HashMap<String, YaormModel.Index>()
            val index = YaormModel.Index
                    .newBuilder()
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(YaormUtils.IdName))
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(TestingModel.Dag.getDescriptor().findFieldByNumber(TestingModel.Dag.DISPLAY_FIELD_NUMBER).name))
                    .build()
            customIndexes[TestingModel.Dag.getDescriptor().name] = index

            val entityMessageService = EntityMessageService(protoService, entityService, customIndexes)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            // act
            val manyDags = ArrayList<TestingModel.Dag>()
            var i = 0
            while (i < 100) {
                manyDags.add(DagBuilder().build())
                i++
            }

            entityMessageService.bulkInsert(manyDags)

            // assert
            val foundDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())
            Assert.assertTrue(foundDags.size == 100)
        }
        finally {
            database.deleteOnExit()
        }
    }
}
