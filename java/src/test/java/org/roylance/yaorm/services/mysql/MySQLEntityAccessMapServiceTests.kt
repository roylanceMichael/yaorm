package org.roylance.yaorm.services.mysql

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.EntityProtoService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.utilities.YaormUtils
import org.roylance.yaorm.utilities.ConnectionUtilities
import java.util.*

class MySQLEntityAccessMapServiceTests {
    @Test
    fun simpleCreateMySQLTest() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        val sourceConnection = MySQLConnectionSourceFactory(
                ConnectionUtilities.mysqlHost!!,
                ConnectionUtilities.mysqlSchema!!,
                ConnectionUtilities.mysqlUserName!!,
                ConnectionUtilities.mysqlPassword!!)

        val newId = UUID.randomUUID().toString()
        val beaconId = "test"
        val majorId = 1L
        val minorId = 2L
        val isActive = true
        val cachedName = "mike"

        val mysqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)
        val granularDatabaseService = JDBCGranularDatabaseProtoService(
                sourceConnection.connectionSource,
                false)

        val entityService = EntityProtoService(
                granularDatabaseService = granularDatabaseService,
                sqlGeneratorService = mysqlGeneratorService)

        val idProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.IdName).setType(YaormModel.ProtobufType.STRING).setIsKey(true)
        val beaconIdProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.BeaconIdName).setType(YaormModel.ProtobufType.STRING).setIsKey(false)
        val majorProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.MajorIdName).setType(YaormModel.ProtobufType.INT64).setIsKey(false)
        val minorProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.MinorIdName).setType(YaormModel.ProtobufType.INT64).setIsKey(false)
        val cachedNameProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.CachedNameName).setType(YaormModel.ProtobufType.STRING).setIsKey(false)
        val isActiveProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.ActiveName).setType(YaormModel.ProtobufType.BOOL).setIsKey(false)
        val lastSeenProperty = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.LastSeenName).setType(YaormModel.ProtobufType.INT64).setIsKey(false)
        val definitionBuilder = YaormModel.TableDefinition.newBuilder()
                .setName(BeaconBroadcastModel::class.java.simpleName)

        definitionBuilder.addColumnDefinitions(idProperty)
        definitionBuilder.addColumnDefinitions(beaconIdProperty)
        definitionBuilder.addColumnDefinitions(majorProperty)
        definitionBuilder.addColumnDefinitions(minorProperty)
        definitionBuilder.addColumnDefinitions(cachedNameProperty)
        definitionBuilder.addColumnDefinitions(isActiveProperty)
        definitionBuilder.addColumnDefinitions(lastSeenProperty)
        val definition = definitionBuilder.build()


        val newBeaconMap = YaormModel.Record.newBuilder()
        newBeaconMap.addColumns(YaormUtils.buildColumn(newId, idProperty.build()))
        newBeaconMap.addColumns(YaormUtils.buildColumn(beaconId, beaconIdProperty.build()))
        newBeaconMap.addColumns(YaormUtils.buildColumn(majorId, majorProperty.build()))
        newBeaconMap.addColumns(YaormUtils.buildColumn(minorId, minorProperty.build()))
        newBeaconMap.addColumns(YaormUtils.buildColumn(isActive, isActiveProperty.build()))
        newBeaconMap.addColumns(YaormUtils.buildColumn(cachedName, cachedNameProperty.build()))
        newBeaconMap.addColumns(YaormUtils.buildColumn(0, lastSeenProperty.build()))

        // act
        entityService.dropTable(definition)
        entityService.createTable(definition)
        entityService.createOrUpdate(newBeaconMap.build(), definition)

        // assert
        val allBeacons = entityService.getMany(definition, 10)

        Assert.assertEquals(1, allBeacons.recordsCount)

        val foundRecord = allBeacons.recordsList.first()
        val foundBeacon = YaormUtils.buildMapFromRecord(foundRecord)
        Assert.assertEquals(newId, foundBeacon[BeaconBroadcastModel.IdName])
        Assert.assertEquals(beaconId, foundBeacon[BeaconBroadcastModel.BeaconIdName])
        Assert.assertEquals(majorId, foundBeacon[BeaconBroadcastModel.MajorIdName])
        Assert.assertEquals(minorId, foundBeacon[BeaconBroadcastModel.MinorIdName])
        Assert.assertEquals(cachedName, foundBeacon[BeaconBroadcastModel.CachedNameName])
        Assert.assertEquals(isActive, foundBeacon[BeaconBroadcastModel.ActiveName])
    }
}
