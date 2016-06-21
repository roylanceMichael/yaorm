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
        val definitionBuilder = YaormModel.TableDefinition.newBuilder()
                .setName(BeaconBroadcastModel::class.java.simpleName)

        definitionBuilder.mutableColumnDefinitions[CommonUtils.IdName] = idProperty.build()
        definitionBuilder.mutableColumnDefinitions[BeaconBroadcastModel.BeaconIdName] = beaconIdProperty.build()
        definitionBuilder.mutableColumnDefinitions[BeaconBroadcastModel.MajorIdName] = majorProperty.build()
        definitionBuilder.mutableColumnDefinitions[BeaconBroadcastModel.MinorIdName] = minorProperty.build()
        definitionBuilder.mutableColumnDefinitions[BeaconBroadcastModel.CachedNameName] = cachedNameProperty.build()
        definitionBuilder.mutableColumnDefinitions[BeaconBroadcastModel.ActiveName] = isActiveProperty.build()
        definitionBuilder.mutableColumnDefinitions[BeaconBroadcastModel.LastSeenName] = lastSeenProperty.build()
        val definition = definitionBuilder.build()


        val newBeaconMap = YaormModel.Record.newBuilder()
        newBeaconMap.mutableColumns[CommonUtils.IdName] = (CommonUtils.buildColumn(newId, idProperty.build()))
        newBeaconMap.mutableColumns[BeaconBroadcastModel.BeaconIdName] = (CommonUtils.buildColumn(beaconId, beaconIdProperty.build()))
        newBeaconMap.mutableColumns[BeaconBroadcastModel.MajorIdName] = (CommonUtils.buildColumn(majorId, majorProperty.build()))
        newBeaconMap.mutableColumns[BeaconBroadcastModel.MinorIdName] = (CommonUtils.buildColumn(minorId, minorProperty.build()))
        newBeaconMap.mutableColumns[BeaconBroadcastModel.ActiveName] = (CommonUtils.buildColumn(isActive, isActiveProperty.build()))
        newBeaconMap.mutableColumns[BeaconBroadcastModel.CachedNameName] = (CommonUtils.buildColumn(cachedName, cachedNameProperty.build()))
        newBeaconMap.mutableColumns[BeaconBroadcastModel.LastSeenName] = (CommonUtils.buildColumn(0, lastSeenProperty.build()))

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
