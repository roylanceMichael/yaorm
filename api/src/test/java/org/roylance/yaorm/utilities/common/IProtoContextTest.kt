package org.roylance.yaorm.utilities.common

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.*
import org.roylance.yaorm.services.EntityProtoContext
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.mysql.MySQLConnectionSourceFactory
import org.roylance.yaorm.services.mysql.MySQLGeneratorService
import org.roylance.yaorm.utilities.*
import java.util.*

interface IProtoContextTest: ICommonTest {
    fun simplePassThroughTest()
    fun migrationAddColumnTest()
    fun migrationRemoveColumnTest()
    fun migrationAddTableTest()
    fun complexMergeTest()
    fun complexMerge2Test()
}