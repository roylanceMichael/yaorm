package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import java.io.File
import java.util.*

class SQLiteGeneratorServiceTest {
//     @Test
    public fun anotherSimpleCreateTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val dropTableSql = sqliteGeneratorService.buildDropTable(
                    org.roylance.yaorm.testmodels.before.SimpleTestModel::class.java)
            granularDatabaseService.executeUpdateQuery<Int>(dropTableSql)

            val createTableSql = sqliteGeneratorService.buildCreateTable(
                    org.roylance.yaorm.testmodels.before.SimpleTestModel::class.java)!!
            granularDatabaseService.executeUpdateQuery<Int>(createTableSql)

            val newModel = org.roylance.yaorm.testmodels.before.SimpleTestModel()
            newModel.fName = "what"
            newModel.lName = "is"
            newModel.mName = "this"

            val insertSql = sqliteGeneratorService.buildInsertIntoTable(
                    org.roylance.yaorm.testmodels.before.SimpleTestModel::class.java,
                    newModel)!!

            granularDatabaseService.executeUpdateQuery<Int>(insertSql)

            // act
            val dropColumnSql = sqliteGeneratorService.buildDropColumn(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java,
                    org.roylance.yaorm.testmodels.before.SimpleTestModel.MNameName)

            granularDatabaseService.executeUpdateQuery<Int>(dropColumnSql!!)

            // assert
            val selectAllQuery = sqliteGeneratorService.buildSelectAll(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java)

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
