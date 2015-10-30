package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import java.io.File
import java.util.*

class SQLiteGeneratorServiceTest {
    // @Test
    public fun anotherSimpleCreatePhoenixTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
            val sqliteGeneratorService = SQLiteGeneratorService()

            val dropTableSql = sqliteGeneratorService.buildDropTable(
                    org.roylance.yaorm.testmodels.before.SimpleTestModel::class.java)
            granularDatabaseService.executeUpdateQuery(dropTableSql)

            val createTableSql = sqliteGeneratorService.buildCreateTable(
                    org.roylance.yaorm.testmodels.before.SimpleTestModel::class.java)!!
            granularDatabaseService.executeUpdateQuery(createTableSql)

            val newModel = org.roylance.yaorm.testmodels.before.SimpleTestModel()
            newModel.fName = "what"
            newModel.lName = "is"
            newModel.mName = "this"

            val insertSql = sqliteGeneratorService.buildInsertIntoTable(
                    org.roylance.yaorm.testmodels.before.SimpleTestModel::class.java,
                    newModel)!!

            granularDatabaseService.executeUpdateQuery(insertSql)

            // act
            val dropColumnSql = sqliteGeneratorService.buildDropColumn(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java,
                    org.roylance.yaorm.testmodels.before.SimpleTestModel.MNameName)

            granularDatabaseService.executeUpdateQuery(dropColumnSql!!)

            // assert
            val selectAllQuery = sqliteGeneratorService.buildSelectAll(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java)

            val allItems = granularDatabaseService.executeSelectQuery(
                    org.roylance.yaorm.testmodels.after.SimpleTestModel::class.java,
                    selectAllQuery)

            allItems.moveNext()
            val foundItem:org.roylance.yaorm.testmodels.after.SimpleTestModel = allItems.getRecord()
            Assert.assertEquals(newModel.fName, foundItem.fName)
            Assert.assertEquals(newModel.lName, foundItem.lName)
        }
        finally {
            database.deleteOnExit()
        }
    }
}
