package org.roylance.yaorm.services.sqlite

import org.roylance.yaorm.services.IConnectionSourceFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class SQLiteConnectionSourceFactory : IConnectionSourceFactory {

    private val actualReadConnection: Connection
    private val actualWriteConnection: Connection
    private var isClosed: Boolean = false

    constructor(
            dbPath: String) {
        this.actualReadConnection = DriverManager.getConnection(
                String.format(SqliteJdbcDbTemplate, dbPath))
        this.actualWriteConnection = DriverManager.getConnection(
                String.format(SqliteJdbcDbTemplate, dbPath))
    }

    constructor(
            dbPath: String,
            userName: String,
            password: String) {
        this.actualReadConnection = DriverManager.getConnection(String.format(SqliteJdbcDbTemplate, dbPath),
                userName,
                password)
        this.actualWriteConnection = DriverManager.getConnection(String.format(SqliteJdbcDbTemplate, dbPath),
                userName,
                password)
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

    @Throws(Exception::class)
    override fun close() {
        if (!this.isClosed) {
            this.actualReadConnection.close()
            this.actualWriteConnection.close()
        }
        this.isClosed = true
    }

    override fun generateReadStatement(): Statement {
        return this.readConnection.createStatement()
    }

    override fun generateUpdateStatement(): Statement {
        return this.writeConnection.createStatement()
    }

    companion object {

        private val SqliteJdbcDbTemplate = "jdbc:sqlite:%1\$s"
    }
}