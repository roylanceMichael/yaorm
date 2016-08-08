package org.roylance.yaorm.services

import java.sql.Connection

interface IConnectionSourceFactory : AutoCloseable {
    val connectionSource: Connection
}
