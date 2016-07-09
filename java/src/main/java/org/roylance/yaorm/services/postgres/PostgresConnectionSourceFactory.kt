package org.roylance.yaorm.services.postgres

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class PostgresConnectionSourceFactory(
        val host:String,
        val port:String,
        val database:String,
        val userName:String,
        val password:String): IConnectionSourceFactory {
    private val commonConnection: Connection
    private var isClosed: Boolean = false

    init {
        val url = "jdbc:postgresql://$host:$port/$database"
        val props = Properties()
        props.setProperty("user", userName)
        props.setProperty("password", password)
        props.setProperty("ssl", "true")
        props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory")
        this.commonConnection = DriverManager.getConnection(url, props)
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
}
