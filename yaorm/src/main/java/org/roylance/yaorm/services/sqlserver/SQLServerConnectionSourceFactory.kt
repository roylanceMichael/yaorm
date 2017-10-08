package org.roylance.yaorm.services.sqlserver

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.*

class SQLServerConnectionSourceFactory(val host:String,
                                       val schema:String,
                                       val userName:String,
                                       val password:String,
                                       val port:Int = 1433,
                                       createDatabaseIfNotExists: Boolean = true,
                                       private val isAzure: Boolean = false): IConnectionSourceFactory {
    private val SQLServerClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    private val SQLServerJDBCString: String
    private val actualReadConnection: Connection
    private val actualWriteConnection: Connection

    private var isClosed: Boolean = false

    init {
        Class.forName(SQLServerClassName)
        if (createDatabaseIfNotExists) {
            this.createSchemaIfNotExists()
        }
        this.SQLServerJDBCString = buildConnectionString(host, port, schema, userName, password, isAzure, true)
        this.actualReadConnection = DriverManager.getConnection(SQLServerJDBCString)
        this.actualWriteConnection = DriverManager.getConnection(SQLServerJDBCString)

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
        val statement = this.readConnection.createStatement()
        return statement
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

    private fun createSchemaIfNotExists() {
        val tempConnection = DriverManager.getConnection(buildConnectionString(host, port, schema, userName, password, isAzure, false))
        val statement = tempConnection.createStatement()
        try {
            statement.executeUpdate("""if not exists(select * from sys.databases where name = '$schema')
    create database $schema""")
        }
        finally {
            statement?.close()
            tempConnection?.close()
        }
    }

    companion object {
        fun buildConnectionString(host: String, port: Int, schema: String, userName: String, password: String, isAzure: Boolean, includeDatabase: Boolean): String {
            if (isAzure && includeDatabase) {
                return "jdbc:sqlserver://$host:$port;database=$schema;user=$userName;password=$password;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
            }
            else if (isAzure) {
                return "jdbc:sqlserver://$host:$port;user=$userName;password=$password;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
            }
            else if (includeDatabase) {
                return "jdbc:sqlserver://$host:$port;database=$schema;user=$userName;password=$password;loginTimeout=30;"
            }
            return "jdbc:sqlserver://$host:$port;user=$userName;password=$password;loginTimeout=30;"
        }
    }

}