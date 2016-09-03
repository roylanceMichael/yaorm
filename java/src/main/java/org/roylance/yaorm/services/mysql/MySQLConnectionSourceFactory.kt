package org.roylance.yaorm.services.mysql

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.*

class MySQLConnectionSourceFactory(
        val host:String,
        val schema:String,
        val userName:String,
        val password:String,
        createDatabaseIfNotExists: Boolean = true
) : IConnectionSourceFactory {
    private val MySQLDriverClass = "com.mysql.jdbc.Driver"
    private val actualReadConnection: Connection
    private val actualWriteConnection: Connection
    private var isClosed: Boolean = false

    init {
        Class.forName(MySQLDriverClass)
        if (createDatabaseIfNotExists) {
            this.createSchemaIfNotExists()
        }
        this.actualReadConnection = DriverManager.getConnection(
                "jdbc:mysql://$host/$schema?user=$userName&password=$password&autoReconnect=true")
        this.actualWriteConnection = DriverManager.getConnection(
                "jdbc:mysql://$host/$schema?user=$userName&password=$password&autoReconnect=true")
    }

    override val readConnection: Connection
        get() {
            if (this.isClosed) {
                throw SQLException("already closed...")
            }
            return this.actualReadConnection
        }

    override val writeConnection: Connection
        get() {
            if (this.isClosed) {
                throw SQLException("already closed...")
            }
            return this.actualWriteConnection
        }

    override fun close() {
        if (!this.isClosed) {
            this.readConnection.close()
            this.writeConnection.close()
        }
    }

    override fun generateReadStatement(): Statement {
        val statement = this.readConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        statement.fetchSize = Integer.MIN_VALUE
        return statement
    }

    override fun generateUpdateStatement(): Statement {
        return this.writeConnection.createStatement()
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
