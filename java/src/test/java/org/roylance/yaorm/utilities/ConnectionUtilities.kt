package org.roylance.yaorm.utilities

import org.roylance.yaorm.services.postgres.PostgresProtoTest
import java.sql.DriverManager
import java.util.*

object ConnectionUtilities {
    var postgresHost:String? = null
    var postgresPort:String? = null
    var postgresUserName:String? = null
    var postgresPassword:String? = null
    var postgresDatabase:String? = null

    var mysqlHost:String? = null
    var mysqlUserName:String? = null
    var mysqlPassword:String? = null
    var mysqlSchema:String? = null

    fun dropMySQLSchema() {
        val connection = DriverManager.getConnection(
                "jdbc:mysql://$mysqlHost?user=$mysqlUserName&password=$mysqlPassword&autoReconnect=true")
        val statement = connection.prepareStatement("drop database if exists $mysqlSchema")
        statement.executeUpdate()
        statement.close()
        connection.close()
    }

    fun getMySQLConnectionInfo() {
        if (mysqlHost == null) {
            val properties = Properties()
            val mysqlStream = ConnectionUtilities::class.java.getResourceAsStream("/mysql.properties")
            try {
                properties.load(mysqlStream)
                mysqlHost = properties.getProperty("host")
                mysqlPassword = properties.getProperty("password")
                mysqlUserName = properties.getProperty("userName")
                mysqlSchema = "test_${UUID.randomUUID().toString().replace("-", "")}"
            }
            finally {
                mysqlStream.close()
            }
        }
    }

    fun getPostgresConnectionInfo() {
        if (postgresHost == null) {
            val properties = Properties()
            val stream = PostgresProtoTest::class.java.getResourceAsStream("/postgres.properties")
            try {
                properties.load(stream)
                postgresHost = properties.getProperty("host")
                postgresPort = properties.getProperty("port")
                postgresPassword = properties.getProperty("password")
                postgresUserName = properties.getProperty("userName")
                postgresDatabase = properties.getProperty("database")
            }
            finally {
                stream.close()
            }
        }
    }
}
