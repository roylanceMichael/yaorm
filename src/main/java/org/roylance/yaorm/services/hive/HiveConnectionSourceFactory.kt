package org.roylance.yaorm.services.hive

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

public class HiveConnectionSourceFactory(
        private val host:String,
        private val port:String,
        private val database:String) : IConnectionSourceFactory {

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

    override fun getConnectionSource(): Connection {
        if (this.connection.isClosed) {
            throw SQLException("already closed...")
        }
        return this.connection
    }
}
