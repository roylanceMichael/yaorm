package org.roylance.yaorm.services.phoenix

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.services.EntityAccessService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.AnotherTestModel
import org.roylance.yaorm.testmodels.TestModel
import java.util.*


public class PhoenixEntityAccessServiceTest {
    // @Test
    public fun simpleCreatePhoenixTest() {
        // arrange
        val id = 1
        val name = "mike"

        val hiveGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory("dev-sherlock-hadoop1")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        try {
            val testModel = TestModel()
            testModel.id = id
            testModel.setName(name)

            entityService.drop(TestModel::class.java)
            entityService.instantiate(TestModel::class.java)
            entityService.create(TestModel::class.java, testModel)

            val newName = "test1"
            testModel.setName(newName)

            // act
            entityService.create(TestModel::class.java, testModel)

            // assert
            val testModels = entityService.getAll(TestModel::class.java)

            Assert.assertEquals(1, testModels.size)

            val foundTestModel = testModels.first()
            Assert.assertEquals(id, foundTestModel.id)
            Assert.assertEquals(foundTestModel.name, newName)
        }
        finally {
            granularDatabaseService.close()
        }
    }

    // @Test
    public fun anotherSimpleCreatePhoenixTest() {
        // arrange
        val id = "waefawef"
        val gram = "a"
        val description = "what is this"

        val hiveGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory("dev-sherlock-hadoop1")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        try {
            val testModel = AnotherTestModel(id, description, gram)

            entityService.drop(AnotherTestModel::class.java)
            entityService.instantiate(AnotherTestModel::class.java)
            entityService.create(AnotherTestModel::class.java, testModel)

            // sanity check
            Assert.assertEquals(1, entityService.getAll(AnotherTestModel::class.java).size)

            // act
            entityService.delete(AnotherTestModel::class.java, id)

            // assert
            Assert.assertEquals(0, entityService.getAll(AnotherTestModel::class.java).size)
        }
        finally {
            granularDatabaseService.close()
        }
    }

    // @Test
    public fun anotherSimpleIndexPhoenixTest() {
        // arrange
        val hiveGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory("dev-sherlock-hadoop1")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        try {
            entityService.drop(AnotherTestModel::class.java)
            entityService.instantiate(AnotherTestModel::class.java)

            val columnNames = ArrayList<String>()
            columnNames.add(AnotherTestModel.DescriptionName)
            columnNames.add(AnotherTestModel.GramName)

            // act
            entityService.createIndex(AnotherTestModel::class.java, columnNames)

            // assert
            Assert.assertEquals(0, entityService.getAll(AnotherTestModel::class.java).size)
        }
        finally {
            granularDatabaseService.close()
        }
    }

    // @Test
    public fun simpleDeletePhoenixTest() {
        // arrange
        val id = 1
        val name = "mike"

        val hiveGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory("dev-sherlock-hadoop1")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        try {
            val testModel = TestModel()
            testModel.id = id
            testModel.setName(name)

            entityService.drop(TestModel::class.java)
            entityService.instantiate(TestModel::class.java)
            entityService.create(TestModel::class.java, testModel)

            // act
            entityService.delete(TestModel::class.java, id)

            // assert
            val testModels = entityService.getAll(TestModel::class.java)
            Assert.assertEquals(0, testModels.size)
        }
        finally {
            granularDatabaseService.close()
        }
    }

    // @Test
    public fun simpleCustomPhoenixTest() {
        // arrange
        val description = "mike"

        val customSql = "select distinct description from AnotherTestModel"

        val hiveGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory("dev-sherlock-hadoop1")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityAccessService(granularDatabaseService, hiveGeneratorService)

        try {
            entityService.drop(AnotherTestModel::class.java)
            entityService.instantiate(AnotherTestModel::class.java)

            val firstTestModel = AnotherTestModel()
            firstTestModel.id = "first"
            firstTestModel.description = description
            firstTestModel.gram = "first"

            val secondTestModel = AnotherTestModel()
            secondTestModel.id = "second"
            secondTestModel.description = description
            secondTestModel.gram = "second"

            entityService.create(AnotherTestModel::class.java, firstTestModel)
            entityService.create(AnotherTestModel::class.java, secondTestModel)

            // act
            val distinctDescriptions = entityService.getCustom(AnotherTestModel::class.java, customSql)

            // assert
            Assert.assertEquals(1, distinctDescriptions.size)
            Assert.assertEquals(description, distinctDescriptions[0].description)
        }
        finally {
            granularDatabaseService.close()
        }
    }

    // @Test
    public fun simpleBulkInsertPhoenixTest() {
        // arrange
        val id = 1
        val name = "mike"

        val phoenixGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory("dev-sherlock-hadoop1,dev-sherlock-hadoop3,dev-sherlock-hadoop4")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val entityService = EntityAccessService(granularDatabaseService, phoenixGeneratorService)

        try {
            val testModel = TestModel()
            testModel.id = id
            testModel.setName(name)

            entityService.drop(TestModel::class.java)
            entityService.instantiate(TestModel::class.java)
            entityService.create(TestModel::class.java, testModel)

            granularDatabaseService.commit()

            // act
            var iter = 0
            while (iter < 1000) {
                testModel.id = iter
                testModel.setName("test${iter++}")
                entityService.create(TestModel::class.java, testModel)
            }
            granularDatabaseService.commit()

            // assert
            val testModels = entityService.getAll(TestModel::class.java)

            Assert.assertEquals(iter, testModels.size)
        }
        finally {
            granularDatabaseService.close()
        }
    }
}
