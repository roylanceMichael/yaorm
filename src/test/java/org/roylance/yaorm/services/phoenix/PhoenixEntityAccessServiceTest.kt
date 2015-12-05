package org.roylance.yaorm.services.phoenix

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.models.migration.IndexModel
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.testmodels.AnotherTestModel
import org.roylance.yaorm.testmodels.TestModel
import java.util.*

class PhoenixEntityAccessServiceTest {
//    @Test
    fun simpleCreatePhoenixTest() {
        // arrange
        val id = 1
        val name = "mike"

        val phoenixGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory(getZookeeperInfo())
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityService(
                TestModel::class.java,
                granularDatabaseService,
                phoenixGeneratorService)

        try {
            val testModel = TestModel()
            testModel.id = id
            testModel.setName(name)

            entityService.dropTable()
            entityService.createTable()
            entityService.create(testModel)

            val newName = "test1"
            testModel.setName(newName)

            // act
            entityService.create(testModel)

            // assert
            val testModels = entityService.getAll()

            Assert.assertEquals(1, testModels.size)

            val foundTestModel = testModels.first()
            Assert.assertEquals(id, foundTestModel.id)
            Assert.assertEquals(foundTestModel.name, newName)
        }
        finally {
            granularDatabaseService.close()
        }
    }

//    @Test
    fun anotherSimpleCreatePhoenixTest() {
        // arrange
        val id = "waefawef"
        val gram = "a"
        val description = "what is this"

        val phoenixGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory(getZookeeperInfo())
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityService(
                AnotherTestModel::class.java,
                granularDatabaseService,
                phoenixGeneratorService)

        try {
            val testModel = AnotherTestModel(id, description, gram)

            entityService.dropTable()
            entityService.createTable()
            entityService.create(testModel)

            // sanity check
            Assert.assertEquals(1, entityService.getAll().size)

            // act
            entityService.delete(id)

            // assert
            Assert.assertEquals(0, entityService.getAll().size)
        }
        finally {
            granularDatabaseService.close()
        }
    }

//     @Test
    public fun anotherSimpleIndexPhoenixTest() {
        // arrange
        val phoenixGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory(getZookeeperInfo())
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityService(
                AnotherTestModel::class.java,
                granularDatabaseService,
                phoenixGeneratorService)

        try {
            entityService.dropTable()
            entityService.createTable()

            val columnNames = ArrayList<String>()
            columnNames.add(AnotherTestModel.DescriptionName)
            columnNames.add(AnotherTestModel.GramName)

            val indexModel = IndexModel(columnNames, ArrayList())

            // act
            entityService.createIndex(indexModel)

            // assert
            Assert.assertEquals(0, entityService.getAll().size)
        }
        finally {
            granularDatabaseService.close()
        }
    }

//     @Test
    public fun simpleDeletePhoenixTest() {
        // arrange
        val id = 1
        val name = "mike"

        val phoenixGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory(getZookeeperInfo())
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityService(
                TestModel::class.java,
                granularDatabaseService,
                phoenixGeneratorService)

        try {
            val testModel = TestModel()
            testModel.id = id
            testModel.setName(name)

            entityService.dropTable()
            entityService.createTable()
            entityService.create(testModel)

            // act
            entityService.delete(id)

            // assert
            val testModels = entityService.getAll()
            Assert.assertEquals(0, testModels.size)
        }
        finally {
            granularDatabaseService.close()
        }
    }

//     @Test
    public fun simpleCustomPhoenixTest() {
        // arrange
        val description = "mike"

        val customSql = "select distinct description from AnotherTestModel"

        val phoenixGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory("dev-sherlock-hadoop1")
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, true)
        val entityService = EntityService(
                AnotherTestModel::class.java,
                granularDatabaseService,
                phoenixGeneratorService)

        try {
            entityService.dropTable()
            entityService.createTable()

            val firstTestModel = AnotherTestModel()
            firstTestModel.id = "first"
            firstTestModel.description = description
            firstTestModel.gram = "first"

            val secondTestModel = AnotherTestModel()
            secondTestModel.id = "second"
            secondTestModel.description = description
            secondTestModel.gram = "second"

            entityService.create(firstTestModel)
            entityService.create(secondTestModel)

            // act
            val distinctDescriptions = entityService.getCustom(customSql)

            // assert
            Assert.assertEquals(1, distinctDescriptions.size)
            Assert.assertEquals(description, distinctDescriptions[0].description)
        }
        finally {
            granularDatabaseService.close()
        }
    }

//     @Test
    public fun simpleBulkInsertPhoenixTest() {
        // arrange
        val id = 1
        val name = "mike"

        val phoenixGeneratorService = PhoenixGeneratorService()
        val sourceConnection = PhoenixConnectionSourceFactory(getZookeeperInfo())
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection.connectionSource, false)
        val entityService = EntityService(
                TestModel::class.java,
                granularDatabaseService,
                phoenixGeneratorService)

        try {
            val testModel = TestModel()
            testModel.id = id
            testModel.setName(name)

            entityService.dropTable()
            entityService.createTable()
            entityService.create(testModel)

            granularDatabaseService.commit()

            // act
            var iter = 0
            while (iter < 1000) {
                testModel.id = iter
                testModel.setName("test${iter++}")
                entityService.create(testModel)
            }
            granularDatabaseService.commit()

            // assert
            val testModels = entityService.getAll()

            Assert.assertEquals(iter, testModels.size)
        }
        finally {
            granularDatabaseService.close()
        }
    }

    companion object {
        var zookeeperQuorum:String?=null

        fun getZookeeperInfo():String {
            if (zookeeperQuorum == null) {
                val properties = Properties()
                val phoenixStream = PhoenixEntityAccessServiceTest::class.java.getResourceAsStream("/phoenix.properties")
                properties.load(phoenixStream)
                zookeeperQuorum = properties.getProperty("zookeeperQuorum")
            }
            return zookeeperQuorum!!
        }
    }
}
