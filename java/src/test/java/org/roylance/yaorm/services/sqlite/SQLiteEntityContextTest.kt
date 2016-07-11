package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.entity.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.*
import org.roylance.yaorm.testmodels.after.AfterSimpleTestContext
import org.roylance.yaorm.testmodels.before.BeforeSimpleTestContext
import org.roylance.yaorm.testmodels.before.SimpleTestModel
import org.roylance.yaorm.utilities.CommonUtils
import java.io.File
import java.util.*

class SQLiteEntityContextTest {
    @Test
    fun simpleColumnDefinitionsTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val anotherTestModelService = EntityService(
                    AnotherTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val beaconBroadcastService = EntityService(
                    BeaconBroadcastModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val testEntityContext = TestEntityContext(
                    anotherTestModelService,
                    beaconBroadcastService,
                    migrationService)

            // act
            val definitions = testEntityContext.getDefinitions()

            //assert
            Assert.assertTrue(definitions.tableDefinitions.values.size == 2)

            val anotherTestModelDefinition = definitions.tableDefinitions.values.elementAt(0)
            Assert.assertEquals(AnotherTestModel::class.java.simpleName, anotherTestModelDefinition.name)

            anotherTestModelDefinition
                    .columnDefinitions
                    .values
                    .forEach {
                        Assert.assertTrue(
                                (AnotherTestModel.DescriptionName.equals(it.name) ||
                                        AnotherTestModel.GramName.equals(it.name) ||
                                        AnotherTestModel.IdName.equals(it.name)) &&
                                        YaormModel.ProtobufType.STRING.equals(it.type)
                        )
                    }

            Assert.assertFalse(anotherTestModelDefinition.hasIndex())

            val beaconBroadcastDefinition = definitions.tableDefinitions.values.elementAt(1)
            Assert.assertEquals(BeaconBroadcastModel::class.java.simpleName, beaconBroadcastDefinition.name)

            beaconBroadcastDefinition
                    .columnDefinitions
                    .values
                    .forEach {
                        Assert.assertTrue(
                                (BeaconBroadcastModel.ActiveName.equals(it.name) &&
                                        CommonUtils.JavaStringName.equals(it.type)) ||
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
            database.deleteOnExit()
        }
    }

     @Test
    fun simpleMigrationColumnTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)

            val sqliteGeneratorService = SQLiteGeneratorService()
            val anotherTestModelService = EntityService(
                    AnotherTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val beaconBroadcastService = EntityService(
                    BeaconBroadcastModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

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
            database.deleteOnExit()
        }
    }

     @Test
    fun complexMigrationColumnTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val beforeService = EntityService(
                    SimpleTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val beforeContext = BeforeSimpleTestContext(beforeService, migrationService)

            val afterService = EntityService(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

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
            database.deleteOnExit()
        }
    }

    @Test
    fun foreignObjectResolveTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection.connectionSource,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val rootTestService = EntityService(
                    RootTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val childTestService = EntityService(
                    ChildTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

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
            database.deleteOnExit()
        }
    }

    @Test
    fun foreignObjectListResolveTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val migrationService = EntityService(
                    MigrationModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val rootTestService = EntityService(
                    RootTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

            val childTestService = EntityService(
                    ChildTestModel::class.java,
                    granularDatabaseService,
                    sqliteGeneratorService)

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
            database.deleteOnExit()
        }
    }
}