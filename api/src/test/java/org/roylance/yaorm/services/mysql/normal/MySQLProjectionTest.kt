package org.roylance.yaorm.services.mysql.normal

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.mysql.MySQLConnectionSourceFactory
import org.roylance.yaorm.services.mysql.MySQLGeneratorService
import org.roylance.yaorm.utilities.ConnectionUtilities

class MySQLProjectionTest {
    @Test
    fun simpleRunThroughTest() {
        // arrange
        ConnectionUtilities.getMySQLConnectionInfo()
        try {
            val sourceConnection = MySQLConnectionSourceFactory(
                    ConnectionUtilities.mysqlHost!!,
                    ConnectionUtilities.mysqlSchema!!,
                    ConnectionUtilities.mysqlUserName!!,
                    ConnectionUtilities.mysqlPassword!!)

            val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)

            val projection = YaormModel.Projection.newBuilder()
                    .setName("simple_test")
                    .addLabels(YaormModel.ColumnDefinition.newBuilder()
                            .setName("something")
                            .setTableAlias("a")
                            .setAlias("something"))
                    .addLabels(YaormModel.ColumnDefinition.newBuilder()
                            .setName("what")
                            .setTableAlias("a")
                            .setAlias("what"))
                    .addLabels(YaormModel.ColumnDefinition.newBuilder()
                            .setName("who")
                            .setTableAlias("a")
                            .setAlias("who"))
                    .setMainTable(YaormModel.TableDefinition.newBuilder()
                            .setAlias("a")
                            .setName("test_table"))
                    .build()

            // act
            println(mySqlGeneratorService.buildProjectionSQL(projection))

            // assert
            Assert.assertTrue(true)
        }
        finally {
            ConnectionUtilities.dropMySQLSchema()
        }
    }

}