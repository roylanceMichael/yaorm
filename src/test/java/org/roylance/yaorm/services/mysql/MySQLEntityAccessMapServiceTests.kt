package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.PropertyDefinitionModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseMapService
import org.roylance.yaorm.services.map.EntityMapService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

class MySQLEntityAccessMapServiceTests {
//    @Test
    fun simpleCreateMySQLTest() {
        // arrange
        getConnectionInfo()

        val beaconId = "test"
        val majorId = 1L
        val minorId = 2L
        val isActive = true
        val cachedName = "mike"

        val mysqlGeneratorService = MySQLGeneratorService(schema!!)
        val sourceConnection = MySQLConnectionSourceFactory(
                host!!,
                schema!!,
                userName!!,
                password!!)
        val granularDatabaseService = JDBCGranularDatabaseMapService(
                sourceConnection.connectionSource,
                false)

        val entityService = EntityMapService(
                indexDefinition = null,
                granularDatabaseService = granularDatabaseService,
                sqlGeneratorService = mysqlGeneratorService)

        val newBeaconMap = HashMap<String, Any>()
        newBeaconMap[BeaconBroadcastModel.IdName] = UUID.randomUUID().toString()
        newBeaconMap[BeaconBroadcastModel.BeaconIdName] = beaconId
        newBeaconMap[BeaconBroadcastModel.MajorIdName] = majorId
        newBeaconMap[BeaconBroadcastModel.MinorIdName] = minorId
        newBeaconMap[BeaconBroadcastModel.ActiveName] = isActive
        newBeaconMap[BeaconBroadcastModel.CachedNameName] = cachedName
        newBeaconMap[BeaconBroadcastModel.LastSeenName] = 0

        val definitionModel = DefinitionModel(name = BeaconBroadcastModel::class.java.simpleName,
                properties = listOf(
                        PropertyDefinitionModel(BeaconBroadcastModel.IdName, CommonSqlDataTypeUtilities.JavaStringName),
                        PropertyDefinitionModel(BeaconBroadcastModel.BeaconIdName, CommonSqlDataTypeUtilities.JavaStringName),
                        PropertyDefinitionModel(BeaconBroadcastModel.MajorIdName, CommonSqlDataTypeUtilities.JavaLongName),
                        PropertyDefinitionModel(BeaconBroadcastModel.MinorIdName, CommonSqlDataTypeUtilities.JavaLongName),
                        PropertyDefinitionModel(BeaconBroadcastModel.ActiveName, CommonSqlDataTypeUtilities.JavaBooleanName),
                        PropertyDefinitionModel(BeaconBroadcastModel.CachedNameName, CommonSqlDataTypeUtilities.JavaStringName),
                        PropertyDefinitionModel(BeaconBroadcastModel.LastSeenName, CommonSqlDataTypeUtilities.JavaIntegerName)
                ),
                indexModel = null)

        // act
        entityService.dropTable(definitionModel)
        entityService.createTable(definitionModel)
        entityService.createOrUpdate(newBeaconMap, definitionModel)

        // assert
        val allBeacons = entityService.getMany(10, definitionModel)

        Assert.assertEquals(1, allBeacons.size)

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(newBeaconMap[CommonSqlDataTypeUtilities.IdName], foundBeacon[BeaconBroadcastModel.IdName])
        Assert.assertEquals(beaconId, foundBeacon[BeaconBroadcastModel.BeaconIdName])
        Assert.assertEquals(majorId, foundBeacon[BeaconBroadcastModel.MajorIdName])
        Assert.assertEquals(minorId, foundBeacon[BeaconBroadcastModel.MinorIdName])
        Assert.assertEquals(isActive, foundBeacon[BeaconBroadcastModel.ActiveName])
        Assert.assertEquals(cachedName, foundBeacon[BeaconBroadcastModel.CachedNameName])
    }

    companion object {
        private var host:String? = null
        private var userName:String? = null
        private var password:String? = null
        private var schema:String? = null

        fun getConnectionInfo() {
            if (host == null) {
                val properties = Properties()
                val mysqlStream = MySQLEntityAccessServiceTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                host = properties.getProperty("host")
                password = properties.getProperty("password")
                userName = properties.getProperty("userName")
                schema = properties.getProperty("schema")
            }
        }
    }
}
