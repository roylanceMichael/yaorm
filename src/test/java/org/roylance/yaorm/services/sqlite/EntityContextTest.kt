package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.AnotherTestModel
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.testmodels.TestEntityContext
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.io.File
import java.util.*

public class EntityContextTest {
    @Test
    public fun anotherSimpleCreatePhoenixTest() {
        // arrange
        val contextName = "testContext"
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
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
                    migrationService,
                    contextName)

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
}
