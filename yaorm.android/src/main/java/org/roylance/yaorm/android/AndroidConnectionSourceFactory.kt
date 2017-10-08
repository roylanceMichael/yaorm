package org.roylance.yaorm.android

import org.roylance.yaorm.services.IConnectionSourceFactory
import java.sql.Connection
import java.sql.Statement

class AndroidConnectionSourceFactory : IConnectionSourceFactory {
    override val readConnection: Connection
        get() = NullConnection()
    override val writeConnection: Connection
        get() = NullConnection()

    override fun generateReadStatement(): Statement {
        return NullStatement()
    }

    override fun generateUpdateStatement(): Statement {
        return NullStatement()
    }

    override fun close() {
    }
}