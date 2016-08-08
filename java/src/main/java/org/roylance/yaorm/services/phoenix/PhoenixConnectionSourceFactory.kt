package org.roylance.yaorm.services.phoenix

import org.roylance.yaorm.services.IConnectionSourceFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class PhoenixConnectionSourceFactory @Throws(ClassNotFoundException::class, SQLException::class)
constructor(host: String) : IConnectionSourceFactory {

    private var isClosed: Boolean = false
    private val commonConnection: Connection

    init {
        Class.forName(JDBCDriverName)
        val jdbcUrl = String.format(JDBCUrl, host)
        this.commonConnection = DriverManager.getConnection(jdbcUrl)
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

        private val JDBCDriverName = "org.apache.phoenix.jdbc.PhoenixDriver"
        private val JDBCUrl = "jdbc:phoenix:%1\$s:/hbase-unsecure"
    }
}