package org.roylance.yaorm.utilities.common

import com.google.protobuf.ByteString
import org.junit.Assert
import org.roylance.common.service.IBuilder
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.EntityMessageService
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.postgres.PostgresConnectionSourceFactory
import org.roylance.yaorm.services.postgres.PostgresGeneratorService
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.utilities.*
import java.io.File
import java.util.*

object EntityMessageServiceTestUtilities {
    fun simpleCreateTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            // act
            val manyDags = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())

            // assert
            Assert.assertTrue(manyDags.isEmpty())
        }
        finally {
            cleanup?.build()
        }
    }

    fun simpleLoadAndCreateTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            cleanup?.build()
        }
    }

    fun complexLoadAndCreateTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            cleanup?.build()
        }
    }

    fun complexLoadAndCreate2Test(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            cleanup?.build()
        }
    }

    fun complexLoadAndCreateProtectedTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            cleanup?.build()
        }
    }

    fun simpleUserAndUserDeviceTestTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            Assert.assertTrue(users.first().id == newUser.id)
            Assert.assertTrue(users.first().display == newUser.display)
        }
        finally {
            cleanup?.build()
        }
    }

    fun simpleGetTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val protoService = TestModelGMBuilder()
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            // act
            val foundDag = entityMessageService.get(TestingModel.Dag.getDefaultInstance(), UUID.randomUUID().toString())

            // assert
            Assert.assertTrue(foundDag == null)
        }
        finally {
            cleanup?.build()
        }
    }

    fun simpleIndexTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            cleanup?.build()
        }
    }

    fun bulkInsertTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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
            cleanup?.build()
        }
    }

    fun bulkInsert1Test(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
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

            val manyDags = ArrayList<TestingModel.Dag>()
            var i = 0
            while (i < 100) {
                manyDags.add(DagBuilder().build())
                i++
            }

            entityMessageService.bulkInsert(manyDags)

            // act
            val objects = GetProtoObjects(
                    entityService,
                    protoService,
                    HashMap(),
                    HashMap())

            val results = objects.build(TestingModel.Dag.getDefaultInstance(), manyDags.map { it.id })

            // assert
            val actualResults = entityMessageService.getMany(TestingModel.Dag.getDefaultInstance())

            results.forEach { outerResult ->
                val foundComparable = actualResults.first { it.id == outerResult.id }
                Assert.assertTrue(foundComparable != null)
                Assert.assertTrue(outerResult.flattenedTasksCount == foundComparable.flattenedTasksCount)
                Assert.assertTrue(outerResult.uncompletedTasksCount == foundComparable.uncompletedTasksCount)

                val flattenedUnion = HashSet<String>()
                outerResult.flattenedTasksList.forEach { flattenedUnion.add(it.id) }
                foundComparable.flattenedTasksList.forEach { flattenedUnion.add(it.id) }

                Assert.assertTrue(flattenedUnion.size == outerResult.flattenedTasksCount)

                val uncompletedUnion = HashSet<String>()
                outerResult.uncompletedTasksList.forEach { uncompletedUnion.add(it.id) }
                foundComparable.uncompletedTasksList.forEach { uncompletedUnion.add(it.id) }

                Assert.assertTrue(uncompletedUnion.size == outerResult.uncompletedTasksCount)
            }
            Assert.assertTrue(actualResults.size == 100)
            Assert.assertTrue(results.size == 100)
            System.out.println(entityMessageService.getReport().callsToDatabase)
        }
        finally {
            cleanup?.build()
        }
    }

    fun simplePassThroughWithReportTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
        // arrange
        try {
            val entityProtoMessageService = EntityMessageService(TestModelGMBuilder(), entityService, HashMap())

            val testModel = TestingModel.SimpleInsertTest.newBuilder()
            entityProtoMessageService.dropAndCreateEntireSchema(testModel.build())

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()
            testModel.display = "random display"
            testModel.testInt32 = 1
            testModel.testInt64 = 2
            testModel.testUint32 = 3
            testModel.testUint64 = 4
            testModel.testSint32 = 5
            testModel.testSint64 = 6
            testModel.testFixed32 = 7
            testModel.testFixed64 = 8
            testModel.testSfixed32 = 9
            testModel.testSfixed64 = 10
            testModel.testBool = true
            testModel.testBytes = ByteString.copyFromUtf8("what is this")
            testModel.testDouble = 11.55
            testModel.testFloat = 12.2324F

            // act
            val result = entityProtoMessageService.merge(testModel.build())

            // assert
            Assert.assertTrue(result)
            val foundMessage = entityProtoMessageService.get(testModel.build(), testModel.id)

            Assert.assertTrue(foundMessage != null)
            Assert.assertTrue(foundMessage!!.id == testModel.id)
            Assert.assertTrue(foundMessage.coolType == testModel.coolType)
            Assert.assertTrue(foundMessage.display == testModel.display)
            Assert.assertTrue(foundMessage.testInt32 == testModel.testInt32)
            Assert.assertTrue(foundMessage.testInt64 == testModel.testInt64)
            Assert.assertTrue(foundMessage.testUint32 == testModel.testUint32)
            Assert.assertTrue(foundMessage.testUint64 == testModel.testUint64)
            Assert.assertTrue(foundMessage.testSint32 == testModel.testSint32)
            Assert.assertTrue(foundMessage.testSint64 == testModel.testSint64)
            Assert.assertTrue(foundMessage.testFixed32 == testModel.testFixed32)
            Assert.assertTrue(foundMessage.testFixed64 == testModel.testFixed64)
            Assert.assertTrue(foundMessage.testSfixed32 == testModel.testSfixed32)
            Assert.assertTrue(foundMessage.testSfixed64 == testModel.testSfixed64)
            Assert.assertTrue(foundMessage.testBool == testModel.testBool)
            Assert.assertTrue(foundMessage.testBytes == testModel.testBytes)
            Assert.assertTrue(foundMessage.testDouble == testModel.testDouble)
            Assert.assertTrue(foundMessage.testFloat == testModel.testFloat)
            Assert.assertTrue(entityProtoMessageService.getReport().executionsCount > 0)
            println(entityProtoMessageService.getReport().executionsList.map { it.rawSql }.joinToString("\n\n"))
        }
        finally {
            cleanup?.build()
        }
    }
}