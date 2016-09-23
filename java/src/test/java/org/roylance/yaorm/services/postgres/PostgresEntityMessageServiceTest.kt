package org.roylance.yaorm.services.postgres

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityMessageService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.utilities.*
import java.util.*

class PostgresEntityMessageServiceTest {
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
            Assert.assertTrue(foundMessage!!.id.equals(testModel.id))
            Assert.assertTrue(foundMessage.coolType.equals(testModel.coolType))
            Assert.assertTrue(foundMessage.display.equals(testModel.display))
            Assert.assertTrue(foundMessage.testInt32.equals(testModel.testInt32))
            Assert.assertTrue(foundMessage.testInt64.equals(testModel.testInt64))
            Assert.assertTrue(foundMessage.testUint32.equals(testModel.testUint32))
            Assert.assertTrue(foundMessage.testUint64.equals(testModel.testUint64))
            Assert.assertTrue(foundMessage.testSint32.equals(testModel.testSint32))
            Assert.assertTrue(foundMessage.testSint64.equals(testModel.testSint64))
            Assert.assertTrue(foundMessage.testFixed32.equals(testModel.testFixed32))
            Assert.assertTrue(foundMessage.testFixed64.equals(testModel.testFixed64))
            Assert.assertTrue(foundMessage.testSfixed32.equals(testModel.testSfixed32))
            Assert.assertTrue(foundMessage.testSfixed64.equals(testModel.testSfixed64))
            Assert.assertTrue(foundMessage.testBool.equals(testModel.testBool))
            Assert.assertTrue(foundMessage.testBytes.equals(testModel.testBytes))
            Assert.assertTrue(foundMessage.testDouble.equals(testModel.testDouble))
            Assert.assertTrue(foundMessage.testFloat.equals(testModel.testFloat))
        }
        finally {
        }
    }

    @Test
    fun childAddThenDeleteTest() {
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
            Assert.assertTrue(foundMessage!!.id.equals(testModel.id))
            Assert.assertTrue(foundMessage.child.id.equals(""))
        }
        finally {
        }
    }

    @Test
    fun verifyChildSerializedProperly() {
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
            Assert.assertTrue(foundMessage!!.id.equals(testModel.id))
            Assert.assertTrue(foundMessage.child.id.equals(testModel.child.id))
            Assert.assertTrue(foundMessage.child.testDisplay.equals(testModel.child.testDisplay))
        }
        finally {
        }
    }

    @Test
    fun verifyChildChangedAfterMergeProperly() {
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
            Assert.assertTrue(foundMessage!!.id.equals(testModel.id))
            Assert.assertTrue(foundMessage.child.id.equals(testModel.child.id))
            Assert.assertTrue(foundMessage.child.testDisplay.equals(newFirstDisplay))
        }
        finally {
        }
    }

    @Test
    fun additionalAddRemoveTest() {
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
            Assert.assertTrue(foundMessage!!.id.equals(testModel.id))
            Assert.assertTrue(foundMessage.child.id.equals(testModel.child.id))
            Assert.assertTrue(foundMessage.child.testDisplay.equals(newFirstDisplay))
        }
        finally {
        }
    }

    @Test
    fun simplePersonTest() {
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
            Assert.assertTrue(foundPerson!!.firstName.equals("Michael"))
            Assert.assertTrue(foundPerson.lastName.equals("Roylance"))
            Assert.assertTrue(foundPerson.mother.id.equals(person.mother.id))
            Assert.assertTrue(foundPerson.mother.firstName.equals(person.mother.firstName))
            Assert.assertTrue(foundPerson.mother.lastName.equals(person.mother.lastName))
            Assert.assertTrue(foundPerson.father.id.equals(person.father.id))
            Assert.assertTrue(foundPerson.father.firstName.equals(person.father.firstName))
            Assert.assertTrue(foundPerson.father.lastName.equals(person.father.lastName))

            val foundMother = entityProtoMessageService.get(person.build(), person.mother.id)
            Assert.assertTrue(foundMother!!.firstName.equals("Terri"))
            Assert.assertTrue(foundMother.lastName.equals("Roylance"))

            val foundFather = entityProtoMessageService.get(person.build(), person.father.id)
            Assert.assertTrue(foundFather!!.firstName.equals("Paul"))
            Assert.assertTrue(foundFather.lastName.equals("Roylance"))
        }
        finally {
        }
    }

    @Test
    fun simplePersonFriendsTest() {
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
            Assert.assertTrue(foundPerson!!.id.equals(person.friendsList[0].id))
            Assert.assertTrue(foundPerson.firstName.equals("James"))
            Assert.assertTrue(foundPerson.lastName.equals("Hu"))
        }
        finally {
        }
    }

    @Test
    fun simpleDagTest() {
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

            val firstDag = moreDags.first { it.id.equals(newDag.id) }
            Assert.assertTrue(firstDag.uncompletedTasksCount == 10)
            Assert.assertTrue(firstDag.processingTasksCount == 0)
            val secondDag = moreDags.first { it.id.equals(builder.id) }
            Assert.assertTrue(secondDag.uncompletedTasksCount == 9)
            Assert.assertTrue(secondDag.processingTasksCount == 1)
        }
        finally {
        }
    }

    @Test
    fun simpleUserAndUserDeviceTestTest() {
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
            val entityMessageService = EntityMessageService(protoService, entityService, HashMap())
            entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())

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
        }
    }

    @Test
    fun simpleIndexTest() {
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

            val customIndexes = HashMap<String, YaormModel.Index>()
            val index = YaormModel.Index
                    .newBuilder()
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(YaormUtils.IdName))
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(TestingModel.Dag.getDescriptor().findFieldByNumber(TestingModel.Dag.DISPLAY_FIELD_NUMBER).name))
                    .build()
            customIndexes[TestingModel.Dag.getDescriptor().name] = index

            val entityMessageService = EntityMessageService(protoService, entityService, customIndexes)
            entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())

            // act
            val foundDag = entityMessageService.get(TestingModel.Dag.getDefaultInstance(), UUID.randomUUID().toString())

            // assert
            Assert.assertTrue(foundDag == null)
        }
        finally {
        }
    }

    @Test
    fun bulkInsertTest() {
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

            val customIndexes = HashMap<String, YaormModel.Index>()
            val index = YaormModel.Index
                    .newBuilder()
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(YaormUtils.IdName))
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(TestingModel.Dag.getDescriptor().findFieldByNumber(TestingModel.Dag.DISPLAY_FIELD_NUMBER).name))
                    .build()
            customIndexes[TestingModel.Dag.getDescriptor().name] = index

            val entityMessageService = EntityMessageService(protoService, entityService, customIndexes)
            entityMessageService.dropAndCreateEntireSchema(TestingModel.getDescriptor())

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
        }
    }
}
