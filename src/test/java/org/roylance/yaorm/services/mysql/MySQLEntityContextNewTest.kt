package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.entity.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.*
import org.roylance.yaorm.testmodels.after.AfterSimpleTestContext
import org.roylance.yaorm.testmodels.before.BeforeSimpleTestContext
import org.roylance.yaorm.testmodels.before.SimpleTestModel
import java.sql.DriverManager
import java.util.*

class MySQLEntityContextNewTest {
//        @Test
        fun simpleDefinitionTest() {
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
                Assert.assertTrue(definitions.definitionsList.size == 2)

                val anotherTestModelDefinition = definitions.definitionsList[0]
                Assert.assertEquals(AnotherTestModel::class.java.simpleName, anotherTestModelDefinition.name)

                anotherTestModelDefinition
                        .propertyDefinitionsList
                        .forEach {
                            Assert.assertTrue(
                                    (AnotherTestModel.DescriptionName.equals(it.name) ||
                                            AnotherTestModel.GramName.equals(it.name) ||
                                            AnotherTestModel.IdName.equals(it.name)) &&
                                                YaormModel.ProtobufType.STRING.equals(it.type)
                            )
                        }

                Assert.assertFalse(anotherTestModelDefinition.hasIndex())

                val beaconBroadcastDefinition = definitions.definitionsList[1]
                Assert.assertEquals(BeaconBroadcastModel::class.java.simpleName, beaconBroadcastDefinition.name)

                beaconBroadcastDefinition
                        .propertyDefinitionsList
                        .forEach {
                            Assert.assertTrue(
                                    (BeaconBroadcastModel.ActiveName.equals(it.name) &&
                                            YaormModel.ProtobufType.STRING.equals(it.type)) ||
                                            (BeaconBroadcastModel.IdName.equals(it.name) &&
                                                    YaormModel.ProtobufType.STRING.equals(it.type)) ||
                                            (BeaconBroadcastModel.LastSeenName.equals(it.name) &&
                                                    YaormModel.ProtobufType.INT64.equals(it.type)) ||
                                            (BeaconBroadcastModel.BeaconIdName.equals(it.name) &&
                                                    YaormModel.ProtobufType.STRING.equals(it.type)) ||
                                            (BeaconBroadcastModel.ActiveName.equals(it.name) &&
                                                    YaormModel.ProtobufType.BOOL.equals(it.type)) ||
                                            (BeaconBroadcastModel.CachedNameName.equals(it.name) &&
                                                    YaormModel.ProtobufType.STRING.equals(it.type)) ||
                                            (BeaconBroadcastModel.MajorIdName.equals(it.name) &&
                                                    YaormModel.ProtobufType.INT64.equals(it.type)) ||
                                            (BeaconBroadcastModel.MinorIdName.equals(it.name) &&
                                                    YaormModel.ProtobufType.INT64.equals(it.type)))
                        }

                Assert.assertFalse(beaconBroadcastDefinition.hasIndex())
            }
            finally {
                dropSchema()
            }
        }

//        @Test
        fun simpleForeignKeyTest() {
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
                val rootService = EntityService(
                        RootTestModel::class.java,
                        granularDatabaseService,
                        mySqlGeneratorService)

                val childService = EntityService(
                        ChildTestModel::class.java,
                        granularDatabaseService,
                        mySqlGeneratorService)

                val migrationService = EntityService(
                        MigrationModel::class.java,
                        granularDatabaseService,
                        mySqlGeneratorService)

                val testEntityContext = ForeignEntityContext(
                        rootService,
                        childService,
                        migrationService)

                testEntityContext.handleMigrations()

                val root1Model = RootTestModel(name = "what")
                val child1Model = ChildTestModel(name = "who", commonRootModel = root1Model, commonRootModelId = root1Model.id)
                val root2Model = RootTestModel(name = "where")
                val child2Model = ChildTestModel(name = "why", commonRootModel = root2Model, commonRootModelId = root2Model.id)

                // act
                testEntityContext.rootTestService.create(root1Model)
                testEntityContext.rootTestService.create(root2Model)
                testEntityContext.childTestService.create(child1Model)
                testEntityContext.childTestService.create(child2Model)

                //assert
                val rootItems = testEntityContext.rootTestService.getMany()
                val children = testEntityContext.childTestService.getMany()

                Assert.assertTrue(rootItems.size == 2)
                Assert.assertTrue(children.size == 2)
                Assert.assertTrue(children[0].commonRootModelId.equals(root1Model.id) ||
                        children[0].commonRootModelId.equals(root2Model.id ))
                Assert.assertTrue(children[1].commonRootModelId.equals(root1Model.id) ||
                        children[1].commonRootModelId.equals(root2Model.id ))
            }
            finally {
                dropSchema()
            }
        }

//        @Test
        fun simpleMigrationColumnTest() {
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

//        @Test
        fun complexMigrationColumnTest() {
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

//        @Test
        fun foreignObjectResolveTest() {
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
                val testModel = ChildTestModel("0", "childTest", rootModel.id, rootModel)

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

//        @Test
        fun foreignObjectListResolveTest() {
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
                val testModel = ChildTestModel("0", "childTest", rootModel.id, rootModel)
                val test1Model = ChildTestModel("1", "child1Test", rootModel.id, rootModel)
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
                val mysqlStream = MySQLEntityContextNewTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                host = properties.getProperty("host")
                password = properties.getProperty("password")
                userName = properties.getProperty("userName")
                schema = "test_$${UUID.randomUUID().toString().replace("-", "")}"
            }
        }
    }
}
