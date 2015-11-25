package org.roylance.yaorm.utilities

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.testmodels.ChildTestModel
import java.util.*

public class SqlUtilitiesTest {
    @Test
    public fun createTableTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "create table if not exists BeaconBroadcastModel (id integer primary key autoincrement, active integer, beaconId text, cachedName text, lastSeen integer, majorId integer, minorId integer);"

        // act
        val createTableSql = sqliteGeneratorService.buildCreateTable(BeaconBroadcastModel::class.java)

        // assert
        Assert.assertEquals(expectedSql, createTableSql)
    }

    @Test
    public fun insertTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val newInsertModel = BeaconBroadcastModel(0, "cool test", 1, 1, true, "what is this",0)
        val expectedSql = "insert into BeaconBroadcastModel (active,beaconId,cachedName,lastSeen,majorId,minorId) values (1,'${newInsertModel.beaconId}','${newInsertModel.cachedName}',${newInsertModel.lastSeen},${newInsertModel.majorId},${newInsertModel.minorId});"

        // act
        val insertSql = sqliteGeneratorService.buildInsertIntoTable(BeaconBroadcastModel::class.java, newInsertModel)

        // assert
        assert(insertSql != null)
        System.out.println(expectedSql)
        System.out.println(insertSql)
        assert(expectedSql.equals(insertSql))
    }

    @Test
    public fun updateTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "update BeaconBroadcastModel set active=1, beaconId='cool test', cachedName='what is this', lastSeen=0, majorId=1, minorId=1 where id=1;"
        val newUpdateModel = BeaconBroadcastModel(1, "cool test", 1, 1, true, "what is this")

        // act
        val updateSql = sqliteGeneratorService.buildUpdateTable(BeaconBroadcastModel::class.java, newUpdateModel)

        // assert
        assert(updateSql != null)
        assert(expectedSql.equals(updateSql))
    }

    @Test
    public fun deleteTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "delete from BeaconBroadcastModel where id=1;"

        // act
        val deleteSql = sqliteGeneratorService.buildDeleteTable(BeaconBroadcastModel::class.java, 1)

        // assert
        assert(deleteSql != null)
        assert(expectedSql.equals(deleteSql))
    }

    @Test
    public fun whereTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "select * from BeaconBroadcastModel where cachedName='mike';"

        val whereClause = WhereClauseItem(
                BeaconBroadcastModel.CachedNameName,
                WhereClauseItem.Equals,
                "mike")

        // act
        val whereSql = sqliteGeneratorService.buildWhereClause(BeaconBroadcastModel::class.java, whereClause)

        // assert
        assert(whereSql != null)
        assert(expectedSql.equals(whereSql))
    }

    @Test
    public fun selectAllTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "select * from BeaconBroadcastModel;"

        // act
        val selectAllSql = sqliteGeneratorService.buildSelectAll(BeaconBroadcastModel::class.java)

        // assert
        assert(expectedSql.equals(selectAllSql))
    }

    @Test
    public fun bulkInsertTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = """insert into BeaconBroadcastModel (active,beaconId,cachedName,lastSeen,majorId,minorId)  select 0 as active,'test1' as beaconId,'test1' as cachedName,0 as lastSeen,0 as majorId,0 as minorId
union select 0 as active,'test2' as beaconId,'test2' as cachedName,0 as lastSeen,0 as majorId,1 as minorId
union select 0 as active,'test3' as beaconId,'test3' as cachedName,0 as lastSeen,0 as majorId,2 as minorId;""".trim()

        val broadcastModels = ArrayList<BeaconBroadcastModel>()
        val firstModel = BeaconBroadcastModel(beaconId = "test1", majorId = 0, minorId = 0, cachedName = "test1")
        val secondModel = BeaconBroadcastModel(beaconId = "test2", majorId = 0, minorId = 1, cachedName = "test2")
        val thirdModel = BeaconBroadcastModel(beaconId = "test3", majorId = 0, minorId = 2, cachedName = "test3")
        broadcastModels.add(firstModel)
        broadcastModels.add(secondModel)
        broadcastModels.add(thirdModel)

        // act
        val actualSql = sqliteGeneratorService.buildBulkInsert(BeaconBroadcastModel::class.java, broadcastModels)

        // assert
        System.out.println(expectedSql)
        System.out.println(actualSql)
        assert(expectedSql.equals(actualSql))
    }

    @Test
    public fun createTableRootChildTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "create table if not exists ChildTestModel (id integer primary key autoincrement, name text, rootModel integer);"

        // act
        val createTableSql = sqliteGeneratorService.buildCreateTable(ChildTestModel::class.java)

        // assert
        Assert.assertEquals(expectedSql, createTableSql)
    }
}
