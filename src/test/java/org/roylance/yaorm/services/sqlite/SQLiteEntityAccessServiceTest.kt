package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.EntityAccessService
import org.roylance.yaorm.services.hive.HiveConnectionSourceFactory
import org.roylance.yaorm.services.hive.HiveGeneratorService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.phoenix.PhoenixConnectionSourceFactory
import org.roylance.yaorm.services.phoenix.PhoenixGeneratorService
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.testmodels.TestModel
import java.io.File
import java.util.*

public class SQLiteEntityAccessServiceTest {
    // @Test
    public fun readmeTest() {
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
            database.deleteOnExit()
        }
        finally {
            granularDatabaseService.close()

            if (database.exists()) {
                database.delete()
            }
        }
    }

    // @Test
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
            database.delete()
        }
        finally {
            granularDatabaseService.close()
            if (database.exists()) {
                database.delete()
            }
        }
    }
}
