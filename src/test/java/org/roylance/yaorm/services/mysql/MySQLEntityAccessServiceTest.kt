package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.util.*

class MySQLEntityAccessServiceTest {
//    @Test
    public fun simpleCreateMySQLTest() {
        // arrange
        getConnectionInfo()

        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val mysqlGeneratorService = MySQLGeneratorService(schema!!)
        val sourceConnection = MySQLConnectionSourceFactory(
                host!!,
                schema!!,
                userName!!,
                password!!)
        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection.connectionSource,
                false)

        val entityService = EntityService(
                BeaconBroadcastModel::class.java,
                granularDatabaseService,
                mysqlGeneratorService)

        val newBeacon = BeaconBroadcastModel(
                beaconId = beaconId,
                majorId = majorId,
                minorId = minorId,
                active = isActive,
                cachedName = cachedName,
                lastSeen = 0)

        // act
        entityService.dropTable()
        entityService.createTable()
        entityService.createOrUpdate(newBeacon)

        // assert
        val allBeacons = entityService.getMany()

        Assert.assertEquals(1, allBeacons.size)

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(newBeacon.id, foundBeacon.id)
        Assert.assertEquals(beaconId, foundBeacon.beaconId)
        Assert.assertEquals(majorId, foundBeacon.majorId)
        Assert.assertEquals(minorId, foundBeacon.minorId)
        Assert.assertEquals(isActive, foundBeacon.active)
        Assert.assertEquals(cachedName, foundBeacon.cachedName)
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
