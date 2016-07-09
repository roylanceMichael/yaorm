package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.entity.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.utilities.ConnectionUtilities

class MySQLEntityAccessServiceTest {
    @Test
    fun simpleCreateMySQLTest() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        val sourceConnection = MySQLConnectionSourceFactory(
                ConnectionUtilities.mysqlHost!!,
                ConnectionUtilities.mysqlSchema!!,
                ConnectionUtilities.mysqlUserName!!,
                ConnectionUtilities.mysqlPassword!!)

        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val mysqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)

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
}
