package org.roylance.yaorm.services.hive

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.EntityAccessService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.util.*

/**
 * Created by mikeroylance on 10/27/15.
 */
public class HiveEntityAccessServiceTest {
    // @Test
    public fun simpleCreateHiveTest() {
        // arrange
        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        val sourceConnection = HiveConnectionSourceFactory("dev-sherlock-hadoop3", "10000", "default")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

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

    // @Test
    public fun simpleCreateUpdateHiveTest() {
        // arrange
        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        val sourceConnection = HiveConnectionSourceFactory("dev-sherlock-hadoop3", "10000", "default")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        val newBeacon = BeaconBroadcastModel(
                beaconId = beaconId,
                majorId = majorId,
                minorId = minorId,
                active = isActive,
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

        Assert.assertEquals(1, allBeacons.size)

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(0, foundBeacon.id)
        Assert.assertEquals(foundBeacon.beaconId, newBeaconId)
        Assert.assertEquals(foundBeacon.majorId, newMajorId)
        Assert.assertEquals(foundBeacon.minorId, newMinorId)
        Assert.assertEquals(foundBeacon.active, foundBeacon.active)
        Assert.assertEquals(foundBeacon.cachedName, foundBeacon.cachedName)
    }

    // @Test
    public fun simpleBulkInsertHiveTest() {
        // arrange
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        val sourceConnection = HiveConnectionSourceFactory("dev-sherlock-hadoop2.local", "10000", "default")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
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
                    active = isActive,
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
        Assert.assertEquals(totalInsertValue, allBeacons.size)
    }
}