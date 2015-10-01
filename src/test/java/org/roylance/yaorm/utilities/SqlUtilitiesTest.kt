package org.roylance.yaorm.utilities

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import java.util.*

public class SqlUtilitiesTest {

    @Test
    public fun createTableTest() {
        // arrange
        val expectedSql = "create table if not exists BeaconBroadcastModel (id integer primary key autoincrement, beaconId text, majorId integer, minorId integer, isActive integer, cachedName text, lastSeen integer);"

        // act
        val createTableSql = SqlUtilities.buildInitialTableCreate(BeaconBroadcastModel::class.java)

        // assert
        Assert.assertEquals(expectedSql, createTableSql.get())
    }

    @Test
    public fun insertTest() {
        // arrange
        val expectedSql = "insert into BeaconBroadcastModel (beaconId,majorId,minorId,isActive,cachedName,lastSeen) values ('cool test',1,1,1,'what is this',0);"
        val newInsertModel = BeaconBroadcastModel(0, "cool test", 1, 1, true, "what is this",0)

        // act
        val insertSql = SqlUtilities.buildInsertIntoTable(BeaconBroadcastModel::class.java, newInsertModel)

        // assert
        assert(insertSql.isPresent)
        assert(expectedSql == insertSql.get())
    }

    @Test
    public fun updateTest() {
        // arrange
        val expectedSql = "update BeaconBroadcastModel set beaconId='cool test', majorId=1, minorId=1, isActive=1, cachedName='what is this', lastSeen=0 where id=1;"
        val newUpdateModel = BeaconBroadcastModel(1, "cool test", 1, 1, true, "what is this")

        // act
        val updateSql = SqlUtilities.buildUpdateTable(BeaconBroadcastModel::class.java, newUpdateModel)

        // assert
        assert(updateSql.isPresent)
        assert(expectedSql == updateSql.get())
    }

    @Test
    public fun deleteTest() {
        // arrange
        val expectedSql = "delete from BeaconBroadcastModel where id=1;"

        // act
        val deleteSql = SqlUtilities.buildDeleteTable(BeaconBroadcastModel::class.java, 1)

        // assert
        assert(deleteSql.isPresent)
        assert(expectedSql == deleteSql.get())
    }

    @Test
    public fun whereTest() {
        // arrange
        val expectedSql = "select * from BeaconBroadcastModel where cachedName='mike';"

        val whereClause = HashMap<String, Any>()
        whereClause.put(BeaconBroadcastModel.CachedNameName, "mike")

        // act
        val whereSql = SqlUtilities.buildWhereClauseAnd(BeaconBroadcastModel::class.java, whereClause)

        // assert
        assert(whereSql.isPresent)
        assert(expectedSql == whereSql.get())
    }

    @Test
    public fun selectAllTest() {
        // arrange
        val expectedSql = "select * from BeaconBroadcastModel;"

        // act
        val selectAllSql = SqlUtilities.buildSelectAll(BeaconBroadcastModel::class.java)

        // assert
        assert(expectedSql == selectAllSql)
    }

    @Test
    public fun bulkInsertTest() {
        // arrange
        val expectedSql = """insert into BeaconBroadcastModel (beaconId,majorId,minorId,isActive,cachedName,lastSeen)  select 'test1' as beaconId,0 as majorId,0 as minorId,0 as isActive,'test1' as cachedName,0 as lastSeen
union select 'test2' as beaconId,0 as majorId,1 as minorId,0 as isActive,'test2' as cachedName,0 as lastSeen
union select 'test3' as beaconId,0 as majorId,2 as minorId,0 as isActive,'test3' as cachedName,0 as lastSeen;""".trim()

        val broadcastModels = ArrayList<BeaconBroadcastModel>()
        val firstModel = BeaconBroadcastModel(beaconId = "test1", majorId = 0, minorId = 0, cachedName = "test1")
        val secondModel = BeaconBroadcastModel(beaconId = "test2", majorId = 0, minorId = 1, cachedName = "test2")
        val thirdModel = BeaconBroadcastModel(beaconId = "test3", majorId = 0, minorId = 2, cachedName = "test3")
        broadcastModels.add(firstModel)
        broadcastModels.add(secondModel)
        broadcastModels.add(thirdModel)

        // act
        val actualSql = SqlUtilities.buildBulkInsert(BeaconBroadcastModel::class.java, broadcastModels)

        // assert
        assert(expectedSql.equals(actualSql))
    }
}
