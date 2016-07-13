package org.roylance.yaorm.services.postgres

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class PostgresConnectionSourceFactory(
        host:String,
        port:String,
        database:String,
        userName:String,
        password:String,
        useSSL:Boolean = true): IConnectionSourceFactory {
    private val commonConnection: Connection
    private var isClosed: Boolean = false

    init {
        val url = "jdbc:postgresql://$host:$port/$database"
        val props = Properties()
        props.setProperty("user", userName)
        props.setProperty("password", password)
        if (useSSL) {
            props.setProperty("ssl", "true")
            props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory")
        }

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
