package org.roylance.yaorm.services.hive

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class HiveConnectionSourceFactory(
        host:String,
        port:String,
        database:String) : IConnectionSourceFactory {

    private val actualReadConnection: Connection
    private val actualWriteConnection: Connection
    private var isClosed:Boolean=false

    init {
        Class.forName("org.apache.hive.jdbc.HiveDriver")
        this.actualReadConnection = DriverManager.getConnection("jdbc:hive2://$host:$port/$database", "", "")
        this.actualWriteConnection = DriverManager.getConnection("jdbc:hive2://$host:$port/$database", "", "")
    }

    override fun close() {
        if (!this.isClosed) {
            this.actualReadConnection.close()
            this.actualWriteConnection.close()
        }
        this.isClosed = true
    }

    override val readConnection: Connection
        @Throws(SQLException::class)
        get() {
            if (this.isClosed) {
                throw SQLException("already closed...")
            }
            return this.actualReadConnection
        }

    override val writeConnection: Connection
        @Throws(SQLException::class)
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
}
