package org.roylance.yaorm.utilities

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.testmodels.BeaconBroadcastModel
import org.roylance.yaorm.testmodels.ChildTestModel
import java.util.*

class SqlUtilitiesTest {
    private val beaconBroadcastDefinition = EntityUtils.getDefinitionProto(BeaconBroadcastModel::class.java)

    @Test
    fun createTableTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "create table if not exists \"BeaconBroadcastModel\" (\"id\" text primary key, \"active\" integer, \"beaconId\" text, \"cachedName\" text, \"lastSeen\" integer, \"majorId\" integer, \"minorId\" integer);"

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
        val expectedSql = "insert into \"BeaconBroadcastModel\" (\"id\",\"beaconId\",\"majorId\",\"minorId\",\"active\",\"cachedName\",\"lastSeen\") values ('1','cool test',1,1,1,'what is this',0);"
        val definitions = EntityUtils.getProperties(newInsertModel)
        val newInsertModelMap = EntityUtils.getRecordFromObject(definitions, newInsertModel)

        // act
        val insertSql = sqliteGeneratorService
                .buildInsertIntoTable(this.beaconBroadcastDefinition, newInsertModelMap)!!

        // assert
        Assert.assertEquals(expectedSql, insertSql, insertSql)
    }

    @Test
    fun updateTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "update \"BeaconBroadcastModel\" set \"beaconId\"='cool test', \"majorId\"=1, \"minorId\"=1, \"active\"=1, \"cachedName\"='what is this', \"lastSeen\"=0 where \"id\"='1';"
        val newUpdateModel = BeaconBroadcastModel("1", "cool test", 1, 1, true, "what is this")

        val definitions = EntityUtils.getProperties(newUpdateModel)
        val updateMap = EntityUtils.getRecordFromObject(definitions, newUpdateModel)

        // act
        val updateSql = sqliteGeneratorService.buildUpdateTable(this.beaconBroadcastDefinition, updateMap)!!

        // assert
        Assert.assertEquals(expectedSql, updateSql, updateSql)
    }

    @Test
    fun deleteTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "delete from \"BeaconBroadcastModel\" where \"id\"='1';"

        val property = YaormModel.ColumnDefinition.newBuilder().setName(CommonUtils.IdName).setType(YaormModel.ProtobufType.STRING)
        val holder = YaormModel.Column.newBuilder().setStringHolder(1.toString()).setDefinition(property).build()

        // act
        val deleteSql = sqliteGeneratorService.buildDeleteTable(this.beaconBroadcastDefinition, holder)!!

        // assert
        System.out.println(deleteSql)
        assert(expectedSql.equals(deleteSql))
    }

    @Test
    fun whereTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "select * from \"BeaconBroadcastModel\" where \"cachedName\"='mike';"

        val property = YaormModel.ColumnDefinition.newBuilder().setName(BeaconBroadcastModel.CachedNameName).setType(YaormModel.ProtobufType.STRING).build()
        val holder = YaormModel.Column.newBuilder().setStringHolder("mike").setDefinition(property).build()
        val whereClause = YaormModel.WhereClause.newBuilder().setNameAndProperty(holder).setOperatorType(YaormModel.WhereClause.OperatorType.EQUALS).build()

        // act
        val whereSql = sqliteGeneratorService.buildWhereClause(this.beaconBroadcastDefinition, whereClause)!!

        // assert
        assert(expectedSql.equals(whereSql))
    }

    @Test
    fun selectAllTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "select * from \"BeaconBroadcastModel\" limit 1000 offset 0;"

        // act
        val selectAllSql = sqliteGeneratorService.buildSelectAll(this.beaconBroadcastDefinition)

        // assert
        assert(expectedSql.equals(selectAllSql))
    }

    @Test
    fun bulkInsertTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = """replace into "BeaconBroadcastModel" ("active","beaconId","cachedName","id","lastSeen","majorId","minorId")  select 0 as "active",'test1' as "beaconId",'test1' as "cachedName",'0' as "id",0 as "lastSeen",0 as "majorId",0 as "minorId"
union select 0 as "active",'test2' as "beaconId",'test2' as "cachedName",'1' as "id",0 as "lastSeen",0 as "majorId",1 as "minorId"
union select 0 as "active",'test3' as "beaconId",'test3' as "cachedName",'2' as "id",0 as "lastSeen",0 as "majorId",2 as "minorId";""".trim()

        val broadcastModels = ArrayList<BeaconBroadcastModel>()
        val firstModel = BeaconBroadcastModel(id = "0", beaconId = "test1", majorId = 0, minorId = 0, cachedName = "test1")
        val secondModel = BeaconBroadcastModel(id = "1", beaconId = "test2", majorId = 0, minorId = 1, cachedName = "test2")
        val thirdModel = BeaconBroadcastModel(id = "2", beaconId = "test3", majorId = 0, minorId = 2, cachedName = "test3")
        broadcastModels.add(firstModel)
        broadcastModels.add(secondModel)
        broadcastModels.add(thirdModel)

        val definitions = EntityUtils.getProperties(firstModel)
        val modelMaps = EntityUtils.getRecordsFromObjects(definitions, broadcastModels)

        // act
        val actualSql = sqliteGeneratorService.buildBulkInsert(this.beaconBroadcastDefinition, modelMaps)

        // assert
        System.out.println(expectedSql)
        System.out.println(actualSql)
        Assert.assertEquals(expectedSql, actualSql)
    }

    @Test
    fun createTableRootChildTest() {
        // arrange
        val sqliteGeneratorService = SQLiteGeneratorService()
        val expectedSql = "create table if not exists \"ChildTestModel\" (\"id\" text primary key, \"commonRootModel\" text, \"commonRootModelId\" text, \"name\" text);"

        // act
        val createTableSql = sqliteGeneratorService.buildCreateTable(EntityUtils.getDefinitionProto(ChildTestModel::class.java))

        // assert
        Assert.assertEquals(expectedSql, createTableSql)
    }
}
