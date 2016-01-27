package org.roylance.yaorm.utilities

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.testmodels.ChildTestModel
import java.util.*

class SqlUtilitiesTest {
    private val beaconBroadcastDefinition = EntityUtils.getDefinition(BeaconBroadcastModel::class.java)

    @Test
    fun createTableTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "create table if not exists BeaconBroadcastModel (id text primary key, active integer, beaconId text, cachedName text, lastSeen integer, majorId integer, minorId integer);"

        // act
        val createTableSql = sqliteGeneratorService.buildCreateTable(this.beaconBroadcastDefinition)
        // assert
        Assert.assertEquals(expectedSql, createTableSql)
    }

    @Test
    fun insertTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val newInsertModel = BeaconBroadcastModel("1", "cool test", 1, 1, true, "what is this",0)
        val expectedSql = "insert into BeaconBroadcastModel (active,beaconId,cachedName,id,lastSeen,majorId,minorId) values (1,'${newInsertModel.beaconId}','${newInsertModel.cachedName}','${newInsertModel.id}',${newInsertModel.lastSeen},${newInsertModel.majorId},${newInsertModel.minorId});"
        val definitions = EntityUtils.getProperties(newInsertModel)
        val newInsertModelMap = EntityUtils.getMapFromObject(definitions, newInsertModel)

        // act
        val insertSql = sqliteGeneratorService.buildInsertIntoTable(this.beaconBroadcastDefinition, newInsertModelMap)

        // assert
        assert(insertSql != null)
        assert(expectedSql.equals(insertSql))
    }

    @Test
    fun updateTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "update BeaconBroadcastModel set active=1, beaconId='cool test', cachedName='what is this', lastSeen=0, majorId=1, minorId=1 where id='1';"
        val newUpdateModel = BeaconBroadcastModel("1", "cool test", 1, 1, true, "what is this")

        val definitions = EntityUtils.getProperties(newUpdateModel)
        val updateMap = EntityUtils.getMapFromObject(definitions, newUpdateModel)

        // act
        val updateSql = sqliteGeneratorService.buildUpdateTable(this.beaconBroadcastDefinition, updateMap)

        // assert
        assert(updateSql != null)
        assert(expectedSql.equals(updateSql))
    }

    @Test
    fun deleteTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "delete from BeaconBroadcastModel where id=1;"

        // act
        val deleteSql = sqliteGeneratorService.buildDeleteTable(this.beaconBroadcastDefinition, 1)

        // assert
        assert(deleteSql != null)
        assert(expectedSql.equals(deleteSql))
    }

    @Test
    fun whereTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "select * from BeaconBroadcastModel where cachedName='mike';"

        val whereClause = WhereClauseItem(
                BeaconBroadcastModel.CachedNameName,
                WhereClauseItem.Equals,
                "mike")

        // act
        val whereSql = sqliteGeneratorService.buildWhereClause(this.beaconBroadcastDefinition, whereClause)

        // assert
        assert(whereSql != null)
        assert(expectedSql.equals(whereSql))
    }

    @Test
    fun selectAllTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "select * from BeaconBroadcastModel limit 1000;"

        // act
        val selectAllSql = sqliteGeneratorService.buildSelectAll(this.beaconBroadcastDefinition)

        // assert
        assert(expectedSql.equals(selectAllSql))
    }

    @Test
    fun bulkInsertTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = """insert into BeaconBroadcastModel (active,beaconId,cachedName,id,lastSeen,majorId,minorId)  select 0 as active,'test1' as beaconId,'test1' as cachedName,'0' as id,0 as lastSeen,0 as majorId,0 as minorId
union select 0 as active,'test2' as beaconId,'test2' as cachedName,'1' as id,0 as lastSeen,0 as majorId,1 as minorId
union select 0 as active,'test3' as beaconId,'test3' as cachedName,'2' as id,0 as lastSeen,0 as majorId,2 as minorId;""".trim()

        val broadcastModels = ArrayList<BeaconBroadcastModel>()
        val firstModel = BeaconBroadcastModel(id = "0", beaconId = "test1", majorId = 0, minorId = 0, cachedName = "test1")
        val secondModel = BeaconBroadcastModel(id = "1", beaconId = "test2", majorId = 0, minorId = 1, cachedName = "test2")
        val thirdModel = BeaconBroadcastModel(id = "2", beaconId = "test3", majorId = 0, minorId = 2, cachedName = "test3")
        broadcastModels.add(firstModel)
        broadcastModels.add(secondModel)
        broadcastModels.add(thirdModel)

        val definitions = EntityUtils.getProperties(firstModel)
        val modelMaps = EntityUtils.getMapsFromObjects(definitions, broadcastModels)

        // act
        val actualSql = sqliteGeneratorService.buildBulkInsert(this.beaconBroadcastDefinition, modelMaps)

        // assert
        System.out.println(expectedSql)
        System.out.println(actualSql)
        assert(expectedSql.equals(actualSql))
    }

    @Test
    fun createTableRootChildTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "create table if not exists ChildTestModel (id text primary key, commonRootModel text, name text);"

        // act
        val createTableSql = sqliteGeneratorService.buildCreateTable(EntityUtils.getDefinition(ChildTestModel::class.java))

        // assert
        Assert.assertEquals(expectedSql, createTableSql)
    }
}
