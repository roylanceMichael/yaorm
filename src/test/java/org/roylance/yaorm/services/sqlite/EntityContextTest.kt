package org.roylance.yaorm.services.sqlite

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
import java.io.File
import java.util.*

public class EntityContextTest {
//    @Test
    public fun anotherSimpleDropColumnTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false, sourceConnection.generatedKeysColumnName!!)
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
                                                CommonSqlDataTypeUtilities.JavaAlt1IntegerName.equals(it.type)) ||
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
            database.deleteOnExit()
        }
    }

//     @Test
    public fun simpleMigrationColumnTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false, sourceConnection.generatedKeysColumnName!!)
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
            testEntityContext.handleMigrations(0)

            //assert
            var savedModels = testEntityContext.anotherTestModelService.getAll()
            Assert.assertEquals(0, savedModels.size)

            val testModel = AnotherTestModel(description = "cool description", gram = "cool gram")
            testEntityContext.anotherTestModelService.create(testModel)

            savedModels = testEntityContext.anotherTestModelService.getAll()

            Assert.assertEquals(1, savedModels.size)
            val foundTestModel = savedModels[0]

            Assert.assertEquals(testModel.description, foundTestModel.description)
            Assert.assertEquals(testModel.gram, foundTestModel.gram)
        }
        finally {
            database.deleteOnExit()
        }
    }

//     @Test
    public fun complexMigrationColumnTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false, sourceConnection.generatedKeysColumnName!!)
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
    public fun foreignObjectResolveTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false, sourceConnection.generatedKeysColumnName!!)
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

            val rootModel = RootTestModel(0, "test")
            val testModel = ChildTestModel(0, "childTest", rootModel)

            // act
            foreignContext.childTestService.createOrUpdate(testModel)

            // assert
            val foundRootModels = foreignContext.rootTestService.getAll()
            val foundTestModels = foreignContext.childTestService.getAll()

            Assert.assertEquals(1, foundRootModels.size)
            Assert.assertEquals(1, foundTestModels.size)
            Assert.assertEquals(foundRootModels[0].id, foundTestModels[0].rootModel?.id)
            Assert.assertEquals(foundRootModels[0].name, foundTestModels[0].rootModel?.name)
        }
        finally {
            database.deleteOnExit()
        }
    }
}
