package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.entity.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.testmodels.ChildTestModel
import java.io.File
import java.util.*

class SQLiteEntityAccessServiceTest {
     @Test
    fun readmeTest() {
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
        val entityService = EntityService(
                BeaconBroadcastModel::class.java,
                granularDatabaseService,
                sqliteGeneratorService)

        try {
            val newBeacon = BeaconBroadcastModel(
                    beaconId = beaconId,
                    majorId = majorId,
                    minorId = minorId,
                    active = isActive,
                    cachedName = cachedName)

            // act
            entityService.createTable()
            entityService.create(newBeacon)

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
        finally {
            database.deleteOnExit()
        }
    }

     @Test
    fun simpleCreateTest() {
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
        val entityService = EntityService(
                BeaconBroadcastModel::class.java,
                granularDatabaseService,
                sqliteGeneratorService)

        try {
            val newBeacon = BeaconBroadcastModel(
                    beaconId = beaconId,
                    majorId = majorId,
                    minorId = minorId,
                    active = isActive,
                    cachedName = cachedName)

            // act
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
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun simpleCreate2Test() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val sqliteGeneratorService = SQLiteGeneratorService()
        val entityService = EntityService(
                ChildTestModel::class.java,
                granularDatabaseService,
                sqliteGeneratorService)

        try {
            val newModel = ChildTestModel(name = "cool child")

            // act
            entityService.createTable()
            entityService.createOrUpdate(newModel)

            // assert
            val allChildren = entityService.getMany()
            Assert.assertEquals(1, allChildren.size)

            val foundChild = allChildren.first()
            Assert.assertEquals(newModel.id, foundChild.id)
            Assert.assertEquals(newModel.name, foundChild.name)
        }
        finally {
            database.deleteOnExit()
        }
    }
}
