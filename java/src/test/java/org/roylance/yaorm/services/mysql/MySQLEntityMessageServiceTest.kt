package org.roylance.yaorm.services.mysql

import com.google.protobuf.ByteString
import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityMessageService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.testmodels.TestModel
import org.roylance.yaorm.utilities.DagBuilder
import org.roylance.yaorm.utilities.TestModelGeneratedMessageBuilder
import org.roylance.yaorm.utilities.TestingModelUtilities
import java.sql.DriverManager
import java.util.*

class MySQLEntityMessageServiceTest {
    @Test
    fun simplePassThroughTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()
            entityProtoMessageService.createEntireSchema(testModel.build())

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
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

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
            dropSchema()
        }
    }

    @Test
    fun childAddThenDeleteTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()
            entityProtoMessageService.createEntireSchema(testModel.build())

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
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

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
            dropSchema()
        }
    }

    @Test
    fun verifyChildSerializedProperly() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()
            entityProtoMessageService.createEntireSchema(testModel.build())

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
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

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
            dropSchema()
        }
    }

    @Test
    fun verifyChildChangedAfterMergeProperly() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()
            entityProtoMessageService.createEntireSchema(testModel.build())

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
            testModel.testDouble = 11.0
            testModel.testFloat = 12.0F

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
            dropSchema()
        }
    }

    @Test
    fun additionalAddRemoveTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val testModel = TestingModelUtilities.buildSampleRootObject()
            entityProtoMessageService.createEntireSchema(testModel.build())
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
            dropSchema()
        }
    }

    @Test
    fun simplePersonTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val person = TestingModel.Person.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setFirstName("Mike")
                .setLastName("Roylance")
                .setFather(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Paul").setLastName("Roylance"))
                .setMother(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Terri").setLastName("Roylance"))
                .addPhoneNumbers(TestingModel.Phone.newBuilder().setId(UUID.randomUUID().toString()).setNumber("555-555-55555"))
                .addAddresses(TestingModel.Address.newBuilder().setId(UUID.randomUUID().toString()).setAddress("555").setCity("SLC").setState("UT").setZip("84101"))

            entityProtoMessageService.createEntireSchema(person.build())
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
            dropSchema()
        }
    }

    @Test
    fun simplePersonFriendsTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            val person = TestingModel.Person.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setFirstName("Mike")
                    .setLastName("Roylance")
                    .setFather(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Paul").setLastName("Roylance"))
                    .setMother(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("Terri").setLastName("Roylance"))
                    .addPhoneNumbers(TestingModel.Phone.newBuilder().setId(UUID.randomUUID().toString()).setNumber("555-555-55555"))
                    .addAddresses(TestingModel.Address.newBuilder().setId(UUID.randomUUID().toString()).setAddress("555").setCity("SLC").setState("UT").setZip("84101"))
                    .addFriends(TestingModel.Person.newBuilder().setId(UUID.randomUUID().toString()).setFirstName("James").setLastName("Hu"))

            entityProtoMessageService.createEntireSchema(person.build())
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
            dropSchema()
        }
    }

    @Test
    fun simpleDagTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            entityProtoMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())
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
            dropSchema()
        }
    }

    @Test
    fun moreComplexDagTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseProtoService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val entityService = EntityProtoService(null, granularDatabaseService, mySqlGeneratorService)
            val entityProtoMessageService = EntityMessageService(TestModelGeneratedMessageBuilder(), entityService)

            entityProtoMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())
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
            dropSchema()
        }
    }

    companion object {
        private var host:String? = null
        private var userName:String? = null
        private var password:String? = null
        private var schema:String? = null

        fun dropSchema() {
            val connection = DriverManager.getConnection(
                    "jdbc:mysql://$host?user=$userName&password=$password&autoReconnect=true")
            val statement = connection.prepareStatement("drop database if exists $schema")
            statement.executeUpdate()
            statement.close()
            connection.close()
        }

        fun getConnectionInfo() {
            if (host == null) {
                val properties = Properties()
                val mysqlStream = MySQLEntityMessageServiceTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                host = properties.getProperty("host")
                password = properties.getProperty("password")
                userName = properties.getProperty("userName")
                schema = "test_${UUID.randomUUID().toString().replace("-", "")}"
            }
        }
    }
}
