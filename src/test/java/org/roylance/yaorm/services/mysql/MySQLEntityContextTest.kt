package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.*
import org.roylance.yaorm.testmodels.after.AfterSimpleTestContext
import org.roylance.yaorm.testmodels.before.BeforeSimpleTestContext
import org.roylance.yaorm.testmodels.before.SimpleTestModel
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.sql.DriverManager
import java.util.*

class MySQLEntityContextTest {
//    @Test
    public fun simpleDefinitionTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val anotherTestModelService = EntityService(
                    AnotherTestModel::class.java,
                    granularDatabaseService,
                    mySqlGeneratorService)

            val beaconBroadcastService = EntityService(
                    BeaconBroadcastModel::class.java,
                    granularDatabaseService,
                    mySqlGeneratorService)

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    mySqlGeneratorService)

            val testEntityContext = TestEntityContext(
                    anotherTestModelService,
                    beaconBroadcastService,
                    migrationService)

            // act
            val definitions = testEntityContext.getDefinitions()

            //assert
            Assert.assertTrue(definitions.definitionModels.size == 2)

            val anotherTestModelDefinition = definitions.definitionModels[0]
            Assert.assertEquals(AnotherTestModel::class.java.simpleName, anotherTestModelDefinition.name)

            anotherTestModelDefinition
                    .properties
                    .forEach {
                        Assert.assertTrue(
                                (AnotherTestModel.DescriptionName.equals(it.name) ||
                                        AnotherTestModel.GramName.equals(it.name) ||
                                        AnotherTestModel.IdName.equals(it.name)) &&
                                        CommonSqlDataTypeUtilities.JavaStringName.equals(it.type)
                        )
                    }

            Assert.assertEquals(null, anotherTestModelDefinition.indexModel)

            val beaconBroadcastDefinition = definitions.definitionModels[1]
            Assert.assertEquals(BeaconBroadcastModel::class.java.simpleName, beaconBroadcastDefinition.name)

            beaconBroadcastDefinition
                    .properties
                    .forEach {
                        Assert.assertTrue(
                                (BeaconBroadcastModel.ActiveName.equals(it.name) &&
                                        CommonSqlDataTypeUtilities.JavaStringName.equals(it.type)) ||
                                        (BeaconBroadcastModel.IdName.equals(it.name) &&
                                                CommonSqlDataTypeUtilities.JavaStringName.equals(it.type)) ||
                                        (BeaconBroadcastModel.LastSeenName.equals(it.name) &&
                                                CommonSqlDataTypeUtilities.JavaLongName.equals(it.type)) ||
                                        (BeaconBroadcastModel.BeaconIdName.equals(it.name) &&
                                                CommonSqlDataTypeUtilities.JavaStringName.equals(it.type)) ||
                                        (BeaconBroadcastModel.ActiveName.equals(it.name) &&
                                                CommonSqlDataTypeUtilities.JavaBooleanName.equals(it.type)) ||
                                        (BeaconBroadcastModel.CachedNameName.equals(it.name) &&
                                                CommonSqlDataTypeUtilities.JavaStringName.equals(it.type)) ||
                                        (BeaconBroadcastModel.MajorIdName.equals(it.name) &&
                                                CommonSqlDataTypeUtilities.JavaIntegerName.equals(it.type)) ||
                                        (BeaconBroadcastModel.MinorIdName.equals(it.name) &&
                                                CommonSqlDataTypeUtilities.JavaIntegerName.equals(it.type)))
                    }

            Assert.assertEquals(null, beaconBroadcastDefinition.indexModel)
        }
        finally {
            dropSchema()
        }
    }

//    @Test
    public fun simpleMigrationColumnTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
            val anotherTestModelService = EntityService(
                    AnotherTestModel::class.java,
                    granularDatabaseService,
                    mySqlGeneratorService)

            val beaconBroadcastService = EntityService(
                    BeaconBroadcastModel::class.java,
                    granularDatabaseService,
                    mySqlGeneratorService)

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    mySqlGeneratorService)

            val testEntityContext = TestEntityContext(
                    anotherTestModelService,
                    beaconBroadcastService,
                    migrationService)

            // act
            testEntityContext.handleMigrations()

            //assert
            var savedModels = testEntityContext.anotherTestModelService.getMany()
            Assert.assertEquals(0, savedModels.size)

            val testModel = AnotherTestModel(
                    id = "what",
                    description = "cool description",
                    gram = "cool gram")
            testEntityContext.anotherTestModelService.create(testModel)

            savedModels = testEntityContext.anotherTestModelService.getMany()

            Assert.assertEquals(1, savedModels.size)
            val foundTestModel = savedModels[0]

            Assert.assertEquals(testModel.description, foundTestModel.description)
            Assert.assertEquals(testModel.gram, foundTestModel.gram)
        }
        finally {
            dropSchema()
        }
    }

//    @Test
    public fun complexMigrationColumnTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val mysqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val beforeService = EntityService(
                    SimpleTestModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val beforeContext = BeforeSimpleTestContext(beforeService, migrationService)

            val afterService = EntityService(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val afterContext = AfterSimpleTestContext(afterService, migrationService)

            // act
            // sanity check
            Assert.assertEquals(0L, migrationService.getCount())

            // first, let's apply migrations to first context
            beforeContext.handleMigrations()

            // after uses the same migration table as before
            Assert.assertEquals(1L, migrationService.getCount())

            // now, apply migrations from after context
            afterContext.handleMigrations()

            //assert
            Assert.assertEquals(2L, migrationService.getCount())
        }
        finally {
            dropSchema()
        }
    }

//    @Test
    public fun foreignObjectResolveTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val mysqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val rootTestService = EntityService(
                    RootTestModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val childTestService = EntityService(
                    ChildTestModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val foreignContext = ForeignEntityContext(
                    rootTestService,
                    childTestService,
                    migrationService)

            foreignContext.handleMigrations()

            val rootModel = RootTestModel("0", "test")
            val testModel = ChildTestModel("0", "childTest", rootModel)

            // act
            foreignContext.rootTestService.create(rootModel)
            foreignContext.childTestService.createOrUpdate(testModel)

            // assert
            val foundRootModels = foreignContext.rootTestService.getMany()
            val foundTestModels = foreignContext.childTestService.getMany()

            Assert.assertEquals(1, foundRootModels.size)
            Assert.assertEquals(1, foundTestModels.size)
            Assert.assertEquals(foundRootModels[0].id, foundTestModels[0].commonRootModel?.id)
            Assert.assertEquals(foundRootModels[0].name, foundTestModels[0].commonRootModel?.name)
        }
        finally {
            dropSchema()
        }
    }

//    @Test
    public fun foreignObjectListResolveTest() {
        // arrange
        getConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    host!!,
                    schema!!,
                    userName!!,
                    password!!)

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val mysqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val rootTestService = EntityService(
                    RootTestModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val childTestService = EntityService(
                    ChildTestModel::class.java,
                    granularDatabaseService,
                    mysqlGeneratorService)

            val foreignContext = ForeignEntityContext(
                    rootTestService,
                    childTestService,
                    migrationService)

            foreignContext.handleMigrations()

            val rootModel = RootTestModel("0", "test")
            val testModel = ChildTestModel("0", "childTest", rootModel)
            val test1Model = ChildTestModel("1", "child1Test", rootModel)
            rootModel.commonChildTests.add(testModel)
            rootModel.commonChildTests.add(test1Model)

            // act
            foreignContext.rootTestService.createOrUpdate(rootModel)
            foreignContext.childTestService.create(testModel)
            foreignContext.childTestService.create(test1Model)

            // assert
            val foundRootModels = foreignContext.rootTestService.getMany()
            val foundTestModels = foreignContext.childTestService.getMany()

            Assert.assertEquals(1, foundRootModels.size)
            Assert.assertEquals(2, foundTestModels.size)
            Assert.assertEquals(foundRootModels[0].id, foundTestModels[0].commonRootModel?.id)
            Assert.assertEquals(foundRootModels[0].name, foundTestModels[0].commonRootModel?.name)
            Assert.assertEquals(2, foundRootModels[0].commonChildTests.size)
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
                val mysqlStream = MySQLEntityContextTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                host = properties.getProperty("host")
                password = properties.getProperty("password")
                userName = properties.getProperty("userName")
                schema = UUID.randomUUID().toString().replace("-", "")
            }
        }
    }
}
