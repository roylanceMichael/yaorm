package org.roylance.yaorm.services

import java.sql.Connection
import java.sql.Statement

interface IConnectionSourceFactory: AutoCloseable {
    val readConnection: Connection
    val writeConnection: Connection

    fun generateReadStatement(): Statement
    fun generateUpdateStatement(): Statement
}
