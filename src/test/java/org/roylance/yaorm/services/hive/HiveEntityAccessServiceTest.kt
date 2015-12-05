package org.roylance.yaorm.services.hive

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.util.*

public class HiveEntityAccessServiceTest {
//     @Test
    public fun simpleCreateHiveTest() {
        // arrange
        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        getProperties()
        val sourceConnection = HiveConnectionSourceFactory(url!!, port!!, db!!)
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val entityService = EntityService(
                BeaconBroadcastModel::class.java,
                granularDatabaseService,
                hiveGeneratorService)

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
        val allBeacons = entityService.getAll()

        Assert.assertEquals(1, allBeacons.size)

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(0, foundBeacon.id)
        Assert.assertEquals(beaconId, foundBeacon.beaconId)
        Assert.assertEquals(majorId, foundBeacon.majorId)
        Assert.assertEquals(minorId, foundBeacon.minorId)
        Assert.assertEquals(isActive, foundBeacon.active)
        Assert.assertEquals(cachedName, foundBeacon.cachedName)
    }

//     @Test
    public fun simpleCreateUpdateHiveTest() {
        // arrange
        val beaconId = "test"
        val majorId = 1
        val minorId = 2
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        getProperties()
        val sourceConnection = HiveConnectionSourceFactory(url!!, port!!, db!!)
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val entityService = EntityService(
                BeaconBroadcastModel::class.java,
                granularDatabaseService,
                hiveGeneratorService)

        val newBeacon = BeaconBroadcastModel(
                beaconId = beaconId,
                majorId = majorId,
                minorId = minorId,
                active = isActive,
                cachedName = cachedName,
                lastSeen = 0)

        entityService.dropTable()
        entityService.createTable()
        entityService.createOrUpdate(newBeacon)

        val newBeaconId = "test1"
        val newMajorId = 2
        val newMinorId = 3

        val newValues = HashMap<String, Any>()
        newValues.put(BeaconBroadcastModel.BeaconIdName, newBeaconId)
        newValues.put(BeaconBroadcastModel.MajorIdName, newMajorId)
        newValues.put(BeaconBroadcastModel.MinorIdName, newMinorId)

        val criteria = WhereClauseItem(BeaconBroadcastModel.CachedNameName, WhereClauseItem.Equals, cachedName)

        // act
        entityService.updateWithCriteria(newValues, criteria)

        // assert
        val allBeacons = entityService.getAll()

        Assert.assertEquals(1, allBeacons.size)

        val foundBeacon = allBeacons.first()
        Assert.assertEquals(0, foundBeacon.id)
        Assert.assertEquals(foundBeacon.beaconId, newBeaconId)
        Assert.assertEquals(foundBeacon.majorId, newMajorId)
        Assert.assertEquals(foundBeacon.minorId, newMinorId)
        Assert.assertEquals(foundBeacon.active, foundBeacon.active)
        Assert.assertEquals(foundBeacon.cachedName, foundBeacon.cachedName)
    }

//     @Test
    public fun simpleBulkInsertHiveTest() {
        // arrange
        val isActive = true
        val cachedName = "mike"

        val hiveGeneratorService = HiveGeneratorService()
        getProperties()
        val sourceConnection = HiveConnectionSourceFactory(url!!, port!!, db!!)
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val entityService = EntityService(
                BeaconBroadcastModel::class.java,
                granularDatabaseService,
                hiveGeneratorService)

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
        entityService.dropTable()
        System.out.println("creating table")
        entityService.createTable()
        System.out.println("bulk inserting")
        entityService.bulkInsert(beaconsToInsert)

        // assert
        System.out.println("verifying size...")
        val allBeacons = entityService.getAll()
        Assert.assertEquals(totalInsertValue, allBeacons.size)
    }

    companion object {
        var url:String?=null
        var port:String?=null
        var db:String?=null
        fun getProperties() {
            val properties = Properties()
            val stream = HiveEntityAccessServiceTest::class.java.getResourceAsStream("/hive.properties")
            properties.load(stream)
            url = properties.getProperty("url")
            port = properties.getProperty("port")
            db = properties.getProperty("db")
        }
    }
}
