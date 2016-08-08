package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.services.IConnectionSourceFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SQLiteConnectionSourceFactory : IConnectionSourceFactory {
    private val commonConnection: Connection
    private var isClosed: Boolean = false

    @Throws(SQLException::class)
    constructor(
            dbPath: String) {
        this.commonConnection = DriverManager.getConnection(
                String.format(SqliteJdbcDbTemplate, dbPath))
    }

    @Throws(SQLException::class)
    constructor(
            dbPath: String,
            userName: String,
            password: String) {
        this.commonConnection = DriverManager.getConnection(String.format(SqliteJdbcDbTemplate, dbPath),
                userName,
                password)
    }

    override val connectionSource: Connection
        @Throws(SQLException::class)
        get() {
            if (this.isClosed) {
                throw SQLException("already closed...")
            }
            return this.commonConnection
        }

    @Throws(Exception::class)
    override fun close() {
        if (!this.isClosed) {
            this.commonConnection.close()
        }
        this.isClosed = true
    }

    companion object {

        private val SqliteJdbcDbTemplate = "jdbc:sqlite:%1\$s"
    }
}