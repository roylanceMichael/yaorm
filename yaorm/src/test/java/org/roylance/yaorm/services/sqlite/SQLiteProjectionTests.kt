package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.ProtobufUtils
import java.io.File
import java.util.*

class SQLiteProjectionTests {
    // todo: finish tests
    //    @Test
    fun simplePassThroughTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)

            val testModel = TestingModel.SimpleInsertTest.newBuilder()

            testModel.id = UUID.randomUUID().toString()
            testModel.coolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            testModel.child = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("first display") .build()

            val subTestChild = TestingModel.Child.newBuilder().setId(UUID.randomUUID().toString()).setTestDisplay("second display")
            testModel.addChilds(subTestChild)

            val firstCoolType = TestingModel.SimpleInsertTest.CoolType.SURPRISED
            val secondCoolType = TestingModel.SimpleInsertTest.CoolType.TEST

            testModel.addCoolTypes(firstCoolType)
            testModel.addCoolTypes(secondCoolType)

            val records = ProtobufUtils.convertProtobufObjectToRecords(testModel.build(), HashMap())

            records.tableRecordsList.forEach {
                entityService.dropTable(it.tableDefinition)
                entityService.createTable(it.tableDefinition)
                entityService.bulkInsert(it.records, it.tableDefinition)
            }

            // act


            // assert
            Assert.assertTrue(true)
        }
        finally {
            database.deleteOnExit()
        }
    }
}