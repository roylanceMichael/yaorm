package org.roylance.yaorm.services

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.sqlite.SqliteConnectionSourceFactory
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.io.File

/**
 * Created by mikeroylance on 9/30/15.
 */
public class EntityAccessServiceTest {
    @Test
    public fun simpleCreateTest() {
        // arrange
        val database = File("testDatabase.db")
        if (database.exists()) {
            database.delete()
        }

        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val sourceConnection = SqliteConnectionSourceFactory(database.absolutePath)
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource)
        val entityService = EntityAccessService(granularDatabaseService)

        val newBeacon = BeaconBroadcastModel(
                beaconId = beaconId,
                majorId = majorId,
                minorId = minorId,
                isActive = isActive,
                cachedName = cachedName)

        // act
        entityService.instantiate(BeaconBroadcastModel::class.java)
        entityService.createOrUpdate(BeaconBroadcastModel::class.java, newBeacon)

        // assert
        val allBeacons = entityService.getAll(BeaconBroadcastModel::class.java)

        Assert.assertEquals(1, allBeacons.size())

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(1, foundBeacon.id)
        Assert.assertEquals(beaconId, foundBeacon.beaconId)
        Assert.assertEquals(majorId, foundBeacon.majorId)
        Assert.assertEquals(minorId, foundBeacon.minorId)
        Assert.assertEquals(isActive, foundBeacon.isActive)
        Assert.assertEquals(cachedName, foundBeacon.cachedName)
    }
}
