package org.roylance.yaorm.services.hive

import org.junit.Assert
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.entity.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.util.*

class HiveEntityAccessServiceTest {
//     @Test
    fun simpleCreateHiveTest() {
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
        val allBeacons = entityService.getMany()

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
    fun simpleCreateUpdateHiveTest() {
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

        val record = YaormModel.Record.newBuilder()
        val beaconProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.BeaconIdName).setType(YaormModel.ProtobufType.STRING)
        val majorProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.MajorIdName).setType(YaormModel.ProtobufType.INT32)
        val minorProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.MinorIdName).setType(YaormModel.ProtobufType.INT32)

        val beaconHolder = YaormModel.Column.newBuilder().setDefinition(beaconProperty).setStringHolder(newBeaconId).build()
        val majorHolder = YaormModel.Column.newBuilder().setDefinition(majorProperty).setInt32Holder(newMajorId).build()
        val minorHolder = YaormModel.Column.newBuilder().setDefinition(minorProperty).setInt32Holder(newMinorId).build()

        record.addColumns(beaconHolder)
        record.addColumns(majorHolder)
        record.addColumns(minorHolder)

        val newValues = HashMap<String, Any>()
        newValues.put(BeaconBroadcastModel.BeaconIdName, newBeaconId)
        newValues.put(BeaconBroadcastModel.MajorIdName, newMajorId)
        newValues.put(BeaconBroadcastModel.MinorIdName, newMinorId)

        val cachedNameProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.CachedNameName).setType(YaormModel.ProtobufType.STRING).build()
        val cachedNameHolder = YaormModel.Column.newBuilder().setStringHolder(cachedName).setDefinition(cachedNameProperty).build()
        val whereClause = YaormModel.WhereClause.newBuilder().setNameAndProperty(cachedNameHolder).setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS).build()

        // act
        entityService.updateWithCriteria(record.build(), whereClause)

        // assert
        val allBeacons = entityService.getMany()

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
    fun simpleBulkInsertHiveTest() {
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
                    id = i.toString(),
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
        val allBeacons = entityService.getMany()
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
