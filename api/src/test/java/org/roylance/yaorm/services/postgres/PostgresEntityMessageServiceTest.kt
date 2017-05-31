package org.roylance.yaorm.services.postgres

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.EntityMessageService
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.utilities.*
import org.roylance.yaorm.utilities.common.EntityMessageServiceTestUtilities
import org.roylance.yaorm.utilities.common.IEntityMessageServiceTest
import java.util.*

class PostgresEntityMessageServiceTest: PostgresBase(), IEntityMessageServiceTest {
    @Test
    override fun simpleCreateTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleCreateTest(buildEntityService())
    }

    @Test
    override fun simpleLoadAndCreateTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleLoadAndCreateTest(buildEntityService())
    }

    @Test
    override fun complexLoadAndCreateTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateTest(buildEntityService())
    }

    @Test
    override fun complexLoadAndCreate2Test() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreate2Test(buildEntityService())
    }

    @Test
    override fun complexLoadAndCreateProtectedTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.complexLoadAndCreateProtectedTest(buildEntityService())
    }

    @Test
    override fun simpleGetTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleGetTest(buildEntityService())
    }

    @Test
    override fun bulkInsert1Test() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsert1Test(buildEntityService())
    }

    @Test
    override fun simplePassThroughWithReportTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simplePassThroughWithReportTest(buildEntityService())
    }

    @Test
    fun simplePassThroughTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false, true)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
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
        }
        finally {
        }
    }

    @Test
    fun childAddThenDeleteTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
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

            entityProtoMessageService.merge(testModel.build())
            testModel.clearChild()

            // act
            val result = entityProtoMessageService.merge(testModel.build())

            // assert
            Assert.assertTrue(result)
            val foundMessage = entityProtoMessageService.get(testModel.build(), testModel.id)

            Assert.assertTrue(foundMessage != null)
            Assert.assertTrue(foundMessage!!.id == testModel.id)
            Assert.assertTrue(foundMessage.child.id == "")
        }
        finally {
        }
    }

    @Test
    fun verifyChildSerializedProperly() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
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
            Assert.assertTrue(foundMessage.child.id == testModel.child.id)
            Assert.assertTrue(foundMessage.child.testDisplay == testModel.child.testDisplay)
        }
        finally {
        }
    }

    @Test
    fun verifyChildChangedAfterMergeProperly() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
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

            entityProtoMessageService.merge(testModel.build())
            val newFirstDisplay = "new first display"

            testModel.childBuilder.testDisplay = newFirstDisplay

            // act
            val result = entityProtoMessageService.merge(testModel.build())

            // assert
            Assert.assertTrue(result)
            val foundMessage = entityProtoMessageService.get(testModel.build(), testModel.id)

            Assert.assertTrue(foundMessage != null)
            Assert.assertTrue(foundMessage!!.id == testModel.id)
            Assert.assertTrue(foundMessage.child.id == testModel.child.id)
            Assert.assertTrue(foundMessage.child.testDisplay == newFirstDisplay)
        }
        finally {
        }
    }

    @Test
    fun additionalAddRemoveTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
            val entityProtoMessageService = EntityMessageService(TestModelGMBuilder(), entityService, HashMap())

            val testModel = TestingModelUtilities.buildSampleRootObject()
            entityProtoMessageService.dropAndCreateEntireSchema(testModel.build())
            entityProtoMessageService.merge(testModel.build())
            val newFirstDisplay = "new first display"

            testModel.childBuilder.testDisplay = newFirstDisplay

            // act
            val result = entityProtoMessageService.merge(testModel.build())

            // assert
            Assert.assertTrue(result)
            val foundMessage = entityProtoMessageService.get(testModel.build(), testModel.id)

            Assert.assertTrue(foundMessage != null)
            Assert.assertTrue(foundMessage!!.id == testModel.id)
            Assert.assertTrue(foundMessage.child.id == testModel.child.id)
            Assert.assertTrue(foundMessage.child.testDisplay == newFirstDisplay)
        }
        finally {
        }
    }

    @Test
    fun simplePersonTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
            val entityProtoMessageService = EntityMessageService(TestModelGMBuilder(), entityService, HashMap())

            val person = TestingModel.Person.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setFirstName("Mike")
                    .setLastName("Roylance")
                    .setFather(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Paul").setLastName("Roylance"))
                    .setMother(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Terri").setLastName("Roylance"))
                    .addPhoneNumbers(TestingModel.Phone.newBuilder().setId(UUID.randomUUID().toString()).setNumber("555-555-55555"))
                    .addAddresses(TestingModel.Address.newBuilder().setId(UUID.randomUUID().toString()).setAddress("555").setCity("SLC").setState("UT").setZip("84101"))

            entityProtoMessageService.dropAndCreateEntireSchema(person.build())
            entityProtoMessageService.merge(person.build())

            // act
            person.firstName = "Michael"
            entityProtoMessageService.merge(person.build())

            // assert
            val foundPerson = entityProtoMessageService.get(person.build(), person.id)
            Assert.assertTrue(foundPerson!!.firstName == "Michael")
            Assert.assertTrue(foundPerson.lastName == "Roylance")
            Assert.assertTrue(foundPerson.mother.id == person.mother.id)
            Assert.assertTrue(foundPerson.mother.firstName == person.mother.firstName)
            Assert.assertTrue(foundPerson.mother.lastName == person.mother.lastName)
            Assert.assertTrue(foundPerson.father.id == person.father.id)
            Assert.assertTrue(foundPerson.father.firstName == person.father.firstName)
            Assert.assertTrue(foundPerson.father.lastName == person.father.lastName)

            val foundMother = entityProtoMessageService.get(person.build(), person.mother.id)
            Assert.assertTrue(foundMother!!.firstName == "Terri")
            Assert.assertTrue(foundMother.lastName == "Roylance")

            val foundFather = entityProtoMessageService.get(person.build(), person.father.id)
            Assert.assertTrue(foundFather!!.firstName == "Paul")
            Assert.assertTrue(foundFather.lastName == "Roylance")
        }
        finally {
        }
    }

    @Test
    fun simplePersonFriendsTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
            val entityProtoMessageService = EntityMessageService(TestModelGMBuilder(), entityService, HashMap())

            val person = TestingModel.Person.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setFirstName("Mike")
                    .setLastName("Roylance")
                    .setFather(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Paul").setLastName("Roylance"))
                    .setMother(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Terri").setLastName("Roylance"))
                    .addPhoneNumbers(TestingModel.Phone.newBuilder().setId(UUID.randomUUID().toString()).setNumber("555-555-55555"))
                    .addAddresses(TestingModel.Address.newBuilder().setId(UUID.randomUUID().toString()).setAddress("555").setCity("SLC").setState("UT").setZip("84101"))
                    .addFriends(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("James").setLastName("Hu"))

            entityProtoMessageService.dropAndCreateEntireSchema(person.build())
            entityProtoMessageService.merge(person.build())

            // act
            person.firstName = "Michael"
            entityProtoMessageService.merge(person.build())

            // assert
            val foundPerson = entityProtoMessageService.get(person.build(), person.friendsList[0].id)
            Assert.assertTrue(foundPerson!!.id == person.friendsList[0].id)
            Assert.assertTrue(foundPerson.firstName == "James")
            Assert.assertTrue(foundPerson.lastName == "Hu")
        }
        finally {
        }
    }

    @Test
    fun simpleDagTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
            val entityProtoMessageService = EntityMessageService(TestModelGMBuilder(), entityService, HashMap())

            entityProtoMessageService.dropAndCreateEntireSchema(TestingModel.Dag.getDefaultInstance())
            val newDag = DagBuilder().build()
            entityProtoMessageService.merge(newDag)

            // act
            val manyDags = entityProtoMessageService.getMany(TestingModel.Dag.getDefaultInstance())
            entityProtoMessageService.merge(newDag)
            val moreDags = entityProtoMessageService.getMany(TestingModel.Dag.getDefaultInstance())

            // assert
            Assert.assertTrue(manyDags.size == 1)
            Assert.assertTrue(moreDags.size == 1)
        }
        finally {
        }
    }

    @Test
    fun moreComplexDagTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
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

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val generatorService = PostgresGeneratorService()
            val entityService = EntityService(granularDatabaseService, generatorService)
            val entityProtoMessageService = EntityMessageService(TestModelGMBuilder(), entityService, HashMap())

            entityProtoMessageService.dropAndCreateEntireSchema(TestingModel.Dag.getDefaultInstance())
            val newDag = DagBuilder().build()
            entityProtoMessageService.merge(newDag)

            // act
            val builder = DagBuilder().build().toBuilder()
            val firstUncompleted = builder.uncompletedTasksBuilderList[0]
            builder.removeUncompletedTasks(0)
            builder.addProcessingTasks(firstUncompleted.setExecutionDate(Date().time).setStartDate(Date().time))
            entityProtoMessageService.merge(builder.build())

            // assert
            val moreDags = entityProtoMessageService.getMany(TestingModel.Dag.getDefaultInstance())
            Assert.assertTrue(moreDags.size == 2)

            val firstDag = moreDags.first { it.id == newDag.id }
            Assert.assertTrue(firstDag.uncompletedTasksCount == 10)
            Assert.assertTrue(firstDag.processingTasksCount == 0)
            val secondDag = moreDags.first { it.id == builder.id }
            Assert.assertTrue(secondDag.uncompletedTasksCount == 9)
            Assert.assertTrue(secondDag.processingTasksCount == 1)
        }
        finally {
        }
    }

    @Test
    override fun simpleUserAndUserDeviceTestTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleUserAndUserDeviceTestTest(buildEntityService())
    }

    @Test
    override fun simpleIndexTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.simpleIndexTest(buildEntityService())
    }

    @Test
    override fun bulkInsertTest() {
        if (!ConnectionUtilities.runPostgresTests()) {
            return
        }
        EntityMessageServiceTestUtilities.bulkInsertTest(buildEntityService())
    }
}
