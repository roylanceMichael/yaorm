package org.roylance.yaorm.services

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.hive.HiveConnectionSourceFactory
import org.roylance.yaorm.services.hive.HiveGeneratorService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.io.File
import java.util.*

/**
 * Created by mikeroylance on 9/30/15.
 */
public class EntityAccessServiceTest {
    @Test
    public fun readmeTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val beaconId = "test"
            val majorId = 1
            val minorId = 2
            val isActive = true
            val cachedName = "mike"

            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityAccessService(granularDatabaseService, sqliteGeneratorService)

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
            database.deleteOnExit()
        }
        finally {
            if (database.exists()) {
                database.delete()
            }
        }
    }

    @Test
    public fun simpleCreateTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val beaconId = "test"
            val majorId = 1
            val minorId = 2
            val isActive = true
            val cachedName = "mike"

            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityAccessService(granularDatabaseService, sqliteGeneratorService)

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
            database.delete()
        }
        finally {
            if (database.exists()) {
                database.delete()
            }
        }
    }

    // @Test
    public fun simpleCreateHiveTest() {
        // arrange
        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        val sourceConnection = HiveConnectionSourceFactory("dev-sherlock-hadoop1.local", "10000", "default")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        val newBeacon = BeaconBroadcastModel(
                beaconId = beaconId,
                majorId = majorId,
                minorId = minorId,
                isActive = isActive,
                cachedName = cachedName,
                lastSeen = 0)

        // act
        entityService.drop(BeaconBroadcastModel::class.java)
        entityService.instantiate(BeaconBroadcastModel::class.java)
        entityService.createOrUpdate(BeaconBroadcastModel::class.java, newBeacon)

        // assert
        val allBeacons = entityService.getAll(BeaconBroadcastModel::class.java)

        Assert.assertEquals(1, allBeacons.size())

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(0, foundBeacon.id)
        Assert.assertEquals(beaconId, foundBeacon.beaconId)
        Assert.assertEquals(majorId, foundBeacon.majorId)
        Assert.assertEquals(minorId, foundBeacon.minorId)
        Assert.assertEquals(isActive, foundBeacon.isActive)
        Assert.assertEquals(cachedName, foundBeacon.cachedName)
    }

    // @Test
    public fun simpleCreateUpdateHiveTest() {
        // arrange
        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        val sourceConnection = HiveConnectionSourceFactory("dev-sherlock-hadoop1.local", "10000", "default")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        val newBeacon = BeaconBroadcastModel(
                beaconId = beaconId,
                majorId = majorId,
                minorId = minorId,
                isActive = isActive,
                cachedName = cachedName,
                lastSeen = 0)

        entityService.drop(BeaconBroadcastModel::class.java)
        entityService.instantiate(BeaconBroadcastModel::class.java)
        entityService.createOrUpdate(BeaconBroadcastModel::class.java, newBeacon)

        val newBeaconId = "test1"
        val newMajorId = 2
        val newMinorId = 3

        val newValues = HashMap<String, Any>()
        newValues.put(BeaconBroadcastModel.BeaconIdName, newBeaconId)
        newValues.put(BeaconBroadcastModel.MajorIdName, newMajorId)
        newValues.put(BeaconBroadcastModel.MinorIdName, newMinorId)

        val criteria = WhereClauseItem(BeaconBroadcastModel.CachedNameName, WhereClauseItem.Equals, cachedName)

        // act
        entityService.updateWithCriteria(BeaconBroadcastModel::class.java, newValues, criteria)

        // assert
        val allBeacons = entityService.getAll(BeaconBroadcastModel::class.java)

        Assert.assertEquals(1, allBeacons.size())

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(0, foundBeacon.id)
        Assert.assertEquals(foundBeacon.beaconId, newBeaconId)
        Assert.assertEquals(foundBeacon.majorId, newMajorId)
        Assert.assertEquals(foundBeacon.minorId, newMinorId)
        Assert.assertEquals(foundBeacon.isActive, foundBeacon.isActive)
        Assert.assertEquals(foundBeacon.cachedName, foundBeacon.cachedName)
    }

    // @Test
    public fun simpleBulkInsertHiveTest() {
        // arrange
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        val sourceConnection = HiveConnectionSourceFactory("dev-sherlock-hadoop1.local", "10000", "default")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        val beaconsToInsert = ArrayList<BeaconBroadcastModel>()

        var i = 0
        val totalInsertValue = 2000

        System.out.println("creating objects in memory")
        while (i < totalInsertValue) {
            val randomString = UUID.randomUUID().toString()
            beaconsToInsert.add(BeaconBroadcastModel(
                    id = i,
                    beaconId = randomString,
                    majorId = i,
                    minorId = i,
                    isActive = isActive,
                    cachedName = cachedName,
                    lastSeen = 0))

            i++
        }

        // act
        System.out.println("dropping table")
        entityService.drop(BeaconBroadcastModel::class.java)
        System.out.println("creating table")
        entityService.instantiate(BeaconBroadcastModel::class.java)
        System.out.println("bulk inserting")
        entityService.bulkInsert(BeaconBroadcastModel::class.java, beaconsToInsert)

        // assert
        System.out.println("verifying size...")
        val allBeacons = entityService.getAll(BeaconBroadcastModel::class.java)
        Assert.assertEquals(totalInsertValue, allBeacons.size())
    }
}
