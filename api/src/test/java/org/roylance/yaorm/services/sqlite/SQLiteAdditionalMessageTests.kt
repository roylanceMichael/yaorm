package org.roylance.yaorm.services.sqlite

import org.junit.Assert
import org.junit.Test
import org.naru.NaruModel
import org.roylance.yaorm.services.EntityMessageService
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import java.io.File
import java.nio.file.Files
import java.util.*

class SQLiteAdditionalMessageTests {
    @Test
    fun simplePassThroughTest() {
        // arrange
        val knownViewIdToTest = "adf4ef0b-2895-4af2-a8fd-c8644a7bd9f4"
        val database = File(UUID.randomUUID().toString())
        val stream = SQLiteAdditionalMessageTests::class.java.getResourceAsStream("/naru")
        try {
            Files.copy(stream, database.toPath())
        }
        finally {
            stream.close()
        }

        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val entityMessageService = EntityMessageService(
                    entityService,
                    HashMap())

            // act
            val views = entityMessageService.getMany(NaruModel.View.getDefaultInstance(), listOf(knownViewIdToTest))

            // assert
            Assert.assertTrue(views.size == 1)
            Assert.assertTrue(views[0].formsCount == 1)
            Assert.assertTrue(views[0].formsList[0].questionsCount == 7)
            Assert.assertTrue(true)
        }
        finally {
            database.deleteOnExit()
        }
    }
}