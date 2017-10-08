package org.roylance.yaorm.services.postgres

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class PostgresConnectionSourceFactory(
        host:String,
        port:String,
        database:String,
        userName:String,
        password:String,
        useSSL:Boolean = true): IConnectionSourceFactory {

    private val actualReadConnection: Connection
    private val actualWriteConnection: Connection
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

        this.actualReadConnection = DriverManager.getConnection(url, props)
        this.actualWriteConnection = DriverManager.getConnection(url, props)
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

    override fun generateReadStatement(): Statement {
        return this.readConnection.createStatement()
    }

    override fun generateUpdateStatement(): Statement {
        return this.writeConnection.createStatement()
    }

    override fun close() {
        if (!this.isClosed) {
            this.readConnection.close()
            this.writeConnection.close()
        }
    }
}
