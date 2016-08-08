package org.roylance.yaorm.services.hive

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class HiveConnectionSourceFactory(
        host:String,
        port:String,
        database:String) : IConnectionSourceFactory {

    private val connection:Connection
    private var isClosed:Boolean=false

    init {
        Class.forName("org.apache.hive.jdbc.HiveDriver")
        this.connection = DriverManager.getConnection("jdbc:hive2://$host:$port/$database", "", "")
    }

    override fun close() {
        if (!this.isClosed) {
            this.connection.close()
        }
        this.isClosed = true
    }

    override val connectionSource: Connection
        @Throws(SQLException::class)
        get() {
            if (this.isClosed) {
                throw SQLException("already closed...")
            }
            return this.connectionSource
        }
}
