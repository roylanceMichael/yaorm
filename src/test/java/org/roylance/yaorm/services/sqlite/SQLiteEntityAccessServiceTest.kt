package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.roylance.yaorm.services.EntityAccessService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.io.File
import java.util.*

public class SQLiteEntityAccessServiceTest {
//     @Test
    public fun readmeTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection.connectionSource,
                false)
        val sqliteGeneratorService = SQLiteGeneratorService()
        val entityService = EntityAccessService(granularDatabaseService, sqliteGeneratorService)

        try {
            val newBeacon = BeaconBroadcastModel(
                    1,
                    beaconId = beaconId,
                    majorId = majorId,
                    minorId = minorId,
                    active = isActive,
                    cachedName = cachedName)

            // act
            entityService.instantiate(BeaconBroadcastModel::class.java)
            entityService.create(BeaconBroadcastModel::class.java, newBeacon)

            // assert
            val allBeacons = entityService.getAll(BeaconBroadcastModel::class.java)

            Assert.assertEquals(1, allBeacons.size)

            val foundBeacon = allBeacons.first()
            Assert.assertEquals(1, foundBeacon.id)
            Assert.assertEquals(beaconId, foundBeacon.beaconId)
            Assert.assertEquals(majorId, foundBeacon.majorId)
            Assert.assertEquals(minorId, foundBeacon.minorId)
            Assert.assertEquals(isActive, foundBeacon.active)
            Assert.assertEquals(cachedName, foundBeacon.cachedName)
        }
        finally {
            database.deleteOnExit()
        }
    }

//     @Test
    public fun simpleCreateTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val sqliteGeneratorService = SQLiteGeneratorService()
        val entityService = EntityAccessService(granularDatabaseService, sqliteGeneratorService)

        try {
            val newBeacon = BeaconBroadcastModel(
                    1,
                    beaconId = beaconId,
                    majorId = majorId,
                    minorId = minorId,
                    active = isActive,
                    cachedName = cachedName)

            // act
            entityService.instantiate(BeaconBroadcastModel::class.java)
            entityService.createOrUpdate(BeaconBroadcastModel::class.java, newBeacon)

            // assert
            val allBeacons = entityService.getAll(BeaconBroadcastModel::class.java)

            Assert.assertEquals(1, allBeacons.size)

            val foundBeacon = allBeacons.first()
            Assert.assertEquals(1, foundBeacon.id)
            Assert.assertEquals(beaconId, foundBeacon.beaconId)
            Assert.assertEquals(majorId, foundBeacon.majorId)
            Assert.assertEquals(minorId, foundBeacon.minorId)
            Assert.assertEquals(isActive, foundBeacon.active)
            Assert.assertEquals(cachedName, foundBeacon.cachedName)
        }
        finally {
            database.deleteOnExit()
        }
    }
}
