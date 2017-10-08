package org.roylance.yaorm.services.csv

import org.junit.Test
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.EntityMessageService
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.utilities.DagBuilder
import org.roylance.yaorm.utilities.YaormUtils
import java.io.File
import java.util.*

class CSVFlatFileServiceTests {
    @Test
    fun bulkInsertTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(
                    database.absolutePath,
                    "mike",
                    "testing")

            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)

            val customIndexes = HashMap<String, YaormModel.Index>()
            val index = YaormModel.Index
                    .newBuilder()
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(YaormUtils.IdName))
                    .addColumnNames(YaormModel.ColumnDefinition.newBuilder().setName(TestingModel.Dag.getDescriptor().findFieldByNumber(TestingModel.Dag.DISPLAY_FIELD_NUMBER).name))
                    .build()
            customIndexes[TestingModel.Dag.getDescriptor().name] = index

            val entityMessageService = EntityMessageService(entityService, customIndexes)
            entityMessageService.createEntireSchema(TestingModel.Dag.getDefaultInstance())

            val manyDags = ArrayList<TestingModel.Dag>()
            var i = 0
            while (i < 100) {
                manyDags.add(DagBuilder().build())
                i++
            }

            entityMessageService.bulkInsert(manyDags)
            val tableDefinition = entityMessageService.entityService.getTableDefinition("", TestingModel.Dag.getDescriptor().name)
            val allRecords = entityMessageService.entityService.getMany(tableDefinition)

            // act
            val newFile = CSVFlatFileService.convertRecords(tableDefinition, allRecords)

            // assert
            val convertedRecords = CSVFlatFileService.convertFile(tableDefinition, newFile)

            assert(convertedRecords.recordsCount == allRecords.recordsCount)

            val allRecordsIds = allRecords.recordsList.map { YaormUtils.getIdColumn(it.columnsList)!!.stringHolder }.toHashSet()
            val convertedRecordsIds = convertedRecords.recordsList.map { YaormUtils.getIdColumn(it.columnsList)!!.stringHolder }.toHashSet()

            assert(allRecordsIds.size == convertedRecordsIds.size)

            allRecordsIds.forEach {
                assert(convertedRecordsIds.contains(it))
            }

            convertedRecordsIds.forEach {
                assert(allRecordsIds.contains(it))
            }

            newFile.deleteOnExit()
        }
        finally {
            database.deleteOnExit()
        }
    }
}