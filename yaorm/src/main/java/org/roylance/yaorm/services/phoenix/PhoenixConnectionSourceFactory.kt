package org.roylance.yaorm.services.phoenix

import org.roylance.yaorm.services.IConnectionSourceFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class PhoenixConnectionSourceFactory @Throws(ClassNotFoundException::class, SQLException::class)
constructor(host: String) : IConnectionSourceFactory {

    private val actualReadConnection: Connection
    private val actualWriteConnection: Connection
    private var isClosed: Boolean = false

    init {
        Class.forName(JDBCDriverName)
        val jdbcUrl = String.format(JDBCUrl, host)
        this.actualReadConnection = DriverManager.getConnection(jdbcUrl)
        this.actualWriteConnection = DriverManager.getConnection(jdbcUrl)
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
        this.isClosed = true
    }

    override fun generateUpdateStatement(): Statement {
        return this.writeConnection.createStatement()
    }

    override fun generateReadStatement(): Statement {
        return this.readConnection.createStatement()
    }

    companion object {
        private val JDBCDriverName = "org.apache.phoenix.jdbc.PhoenixDriver"
        private val JDBCUrl = "jdbc:phoenix:%1\$s:/hbase-unsecure"
    }
}