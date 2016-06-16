package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.utilities.CommonUtils
import java.util.*

class MySQLEntityAccessMapServiceTests {
    @Test
    fun simpleCreateMySQLTest() {
        // arrange
        getConnectionInfo()

        val newId = UUID.randomUUID().toString()
        val beaconId = "test"
        val majorId = 1L
        val minorId = 2L
        val isActive = true
        val cachedName = "mike"

        val mysqlGeneratorService = MySQLGeneratorService(schema!!)
        val sourceConnection = MySQLConnectionSourceFactory(
                host!!,
                schema!!,
                userName!!,
                password!!)
        val granularDatabaseService = JDBCGranularDatabaseProtoService(
                sourceConnection.connectionSource,
                false)

        val entityService = EntityProtoService(
                indexDefinition = null,
                granularDatabaseService = granularDatabaseService,
                sqlGeneratorService = mysqlGeneratorService)

        val idProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.IdName).setType(YaormModel.ProtobufType.STRING).setIsKey(true)
        val beaconIdProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.BeaconIdName).setType(YaormModel.ProtobufType.STRING).setIsKey(false)
        val majorProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.MajorIdName).setType(YaormModel.ProtobufType.INT64).setIsKey(false)
        val minorProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.MinorIdName).setType(YaormModel.ProtobufType.INT64).setIsKey(false)
        val cachedNameProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.CachedNameName).setType(YaormModel.ProtobufType.STRING).setIsKey(false)
        val isActiveProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.ActiveName).setType(YaormModel.ProtobufType.BOOL).setIsKey(false)
        val lastSeenProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.LastSeenName).setType(YaormModel.ProtobufType.INT64).setIsKey(false)
        val definition = YaormModel.TableDefinition.newBuilder()
                .setName(BeaconBroadcastModel::class.java.simpleName)
                .addColumnDefinitions(idProperty)
                .addColumnDefinitions(beaconIdProperty)
                .addColumnDefinitions(majorProperty)
                .addColumnDefinitions(minorProperty)
                .addColumnDefinitions(isActiveProperty)
                .addColumnDefinitions(cachedNameProperty)
                .addColumnDefinitions(lastSeenProperty)
                .build()

        val newBeaconMap = YaormModel.Record.newBuilder()
        newBeaconMap.addColumns(CommonUtils.buildColumn(newId, idProperty.build()))
        newBeaconMap.addColumns(CommonUtils.buildColumn(beaconId, beaconIdProperty.build()))
        newBeaconMap.addColumns(CommonUtils.buildColumn(majorId, majorProperty.build()))
        newBeaconMap.addColumns(CommonUtils.buildColumn(minorId, minorProperty.build()))
        newBeaconMap.addColumns(CommonUtils.buildColumn(isActive, isActiveProperty.build()))
        newBeaconMap.addColumns(CommonUtils.buildColumn(cachedName, cachedNameProperty.build()))
        newBeaconMap.addColumns(CommonUtils.buildColumn(0, lastSeenProperty.build()))

        // act
        entityService.dropTable(definition)
        entityService.createTable(definition)
        entityService.createOrUpdate(newBeaconMap.build(), definition)

        // assert
        val allBeacons = entityService.getMany(10, definition)

        Assert.assertEquals(1, allBeacons.recordsCount)

        val foundRecord = allBeacons.recordsList.first()
        val foundBeacon = CommonUtils.buildMapFromRecord(foundRecord)
        Assert.assertEquals(newId, foundBeacon[BeaconBroadcastModel.IdName])
        Assert.assertEquals(beaconId, foundBeacon[BeaconBroadcastModel.BeaconIdName])
        Assert.assertEquals(majorId, foundBeacon[BeaconBroadcastModel.MajorIdName])
        Assert.assertEquals(minorId, foundBeacon[BeaconBroadcastModel.MinorIdName])
        Assert.assertEquals(cachedName, foundBeacon[BeaconBroadcastModel.CachedNameName])
        Assert.assertEquals(isActive, foundBeacon[BeaconBroadcastModel.ActiveName])
    }

    companion object {
        private var host:String? = null
        private var userName:String? = null
        private var password:String? = null
        private var schema:String? = null

        fun getConnectionInfo() {
            if (host == null) {
                val properties = Properties()
                val mysqlStream = MySQLEntityAccessServiceTest::class.java.getResourceAsStream("/mysql.properties")
                properties.load(mysqlStream)
                host = properties.getProperty("host")
                password = properties.getProperty("password")
                userName = properties.getProperty("userName")
                schema = properties.getProperty("schema")
            }
        }
    }
}
