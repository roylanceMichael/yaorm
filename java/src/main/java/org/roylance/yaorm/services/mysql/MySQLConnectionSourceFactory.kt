package org.roylance.yaorm.services.mysql

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class MySQLConnectionSourceFactory(
        val host:String,
        val schema:String,
        val userName:String,
        val password:String,
        createDatabaseIfNotExists: Boolean = true
) : IConnectionSourceFactory {
    private val MySQLDriverClass = "com.mysql.jdbc.Driver"
    private val commonConnection: Connection
    private var isClosed: Boolean = false

    init {
        Class.forName(MySQLDriverClass)
        if (createDatabaseIfNotExists) {
            this.createSchemaIfNotExists()
        }
        this.commonConnection = DriverManager.getConnection(
                "jdbc:mysql://$host/$schema?user=$userName&password=$password&autoReconnect=true")
    }

    override fun getConnectionSource(): Connection {
        if (this.isClosed) {
            throw SQLException("already closed")
        }
        return this.commonConnection
    }

    override fun close() {
        if (!this.isClosed) {
            this.commonConnection.close()
        }
    }

    private fun createSchemaIfNotExists() {
        val tempConnection = DriverManager.getConnection(
                "jdbc:mysql://$host?user=$userName&password=$password")
        val statement = tempConnection.createStatement()
        try {
            statement.executeUpdate("create database if not exists ${this.schema}")
        }
        finally {
            statement?.close()
            tempConnection?.close()
        }
    }
}
