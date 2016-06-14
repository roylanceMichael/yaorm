package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.EntityUtils
import java.io.File
import java.util.*

class SQLiteGeneratorServiceTest {
     @Test
    fun anotherSimpleCreateTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val simpleTestDefinition = EntityUtils.getDefinitionProto(org.roylance.yaorm.testmodels.before.SimpleTestModel::class.java)

            val dropTableSql = sqliteGeneratorService.buildDropTable(simpleTestDefinition)
            granularDatabaseService.executeUpdateQuery(dropTableSql)

            val createTableSql = sqliteGeneratorService.buildCreateTable(simpleTestDefinition)!!
            granularDatabaseService.executeUpdateQuery(createTableSql)

            val newModel = org.roylance.yaorm.testmodels.before.SimpleTestModel()
            newModel.id = "1"
            newModel.fName = "what"
            newModel.lName = "is"
            newModel.mName = "this"

            val newModelProperties = EntityUtils.getProperties(newModel)
            val newModelMap = EntityUtils.getRecordFromObject(newModelProperties, newModel)

            val insertSql = sqliteGeneratorService.buildInsertIntoTable(
                    simpleTestDefinition,
                    newModelMap)!!

            granularDatabaseService.executeUpdateQuery(insertSql)

            val secondDefinition = EntityUtils.getDefinitionProto(org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java)
            val propertyDefinition = YaormModel.ColumnDefinition.newBuilder().setName(org.roylance.yaorm.testmodels.before.SimpleTestModel.MNameName).setType(YaormModel.ProtobufType.STRING).build()

            // act
            val dropColumnSql = sqliteGeneratorService.buildDropColumn(
                    secondDefinition,
                    propertyDefinition)

            granularDatabaseService.executeUpdateQuery(dropColumnSql!!)

            // assert
            val selectAllQuery = sqliteGeneratorService.buildSelectAll(simpleTestDefinition)

            val allItems = granularDatabaseService.executeSelectQuery(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java,
                    selectAllQuery)

            val allItemsList:List<org.roylance.yaorm.testmodels.after.SimpleTestModel> = allItems.getRecords()
            val foundItem = allItemsList[0]
            Assert.assertEquals(newModel.fName, foundItem.fName)
            Assert.assertEquals(newModel.lName, foundItem.lName)
        }
        finally {
            database.deleteOnExit()
        }
    }
}
