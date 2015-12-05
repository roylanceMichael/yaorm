package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.EntityAccessService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.util.*

class MySQLEntityAccessServiceTest {
    @Test
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

        val entityService = EntityAccessService(
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
        entityService.drop(BeaconBroadcastModel::class.java)
        entityService.instantiate(BeaconBroadcastModel::class.java)
        entityService.createOrUpdate(BeaconBroadcastModel::class.java, newBeacon)

        // assert
        val allBeacons = entityService.getAll(BeaconBroadcastModel::class.java)

        Assert.assertEquals(1, allBeacons.size)

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(0, foundBeacon.id)
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
            if (MySQLEntityAccessServiceTest.host == null) {
                val properties = Properties()
                val mysqlStream = MySQLEntityAccessServiceTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                MySQLEntityAccessServiceTest.host = properties.getProperty("host")
                MySQLEntityAccessServiceTest.password = properties.getProperty("password")
                MySQLEntityAccessServiceTest.userName = properties.getProperty("userName")
                MySQLEntityAccessServiceTest.schema = properties.getProperty("schema")
            }
        }
    }
}
