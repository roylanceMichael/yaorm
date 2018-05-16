package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities
import java.util.*

class SQLServerNestedEnumTest : SQLServerBase(), INestedEnumTest {
  @Test
  override fun simpleMultipleStringsTest() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    val sourceConnection = SQLServerConnectionSourceFactory(
        ConnectionUtilities.sqlServerSqlHost!!,
        uuid,
        ConnectionUtilities.sqlServerSqlUserName!!,
        ConnectionUtilities.sqlServerSqlPassword!!)
    val granularDatabaseService = JDBCGranularDatabaseService(
        sourceConnection,
        false,
        true)
    val sqlGeneratorService = SQLServerGeneratorService()
    val entityService = EntityService(granularDatabaseService, sqlGeneratorService)
    NestedEnumTestUtilities.simpleMultipleStringsTest(entityService, cleanup(uuid))
  }

  @Test
  override fun simplePassThroughExecutionsTest() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    val sourceConnection = SQLServerConnectionSourceFactory(
        ConnectionUtilities.sqlServerSqlHost!!,
        uuid,
        ConnectionUtilities.sqlServerSqlUserName!!,
        ConnectionUtilities.sqlServerSqlPassword!!)
    val granularDatabaseService = JDBCGranularDatabaseService(
        sourceConnection,
        false,
        true)
    val sqlGeneratorService = SQLServerGeneratorService()
    val entityService = EntityService(granularDatabaseService, sqlGeneratorService)
    NestedEnumTestUtilities.simplePassThroughExecutionsTest(entityService, cleanup(uuid))
  }

  @Test
  override fun simplePassThroughTest() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    NestedEnumTestUtilities.simplePassThroughTest(buildEntityService(uuid), cleanup(uuid))
  }

  @Test
  override fun simplePassThroughTest2() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    NestedEnumTestUtilities.simplePassThroughTest2(buildEntityService(uuid), cleanup(uuid))
  }

  @Test
  override fun simpleTablesTest() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    NestedEnumTestUtilities.simpleTablesTest(buildEntityService(uuid), cleanup(uuid), uuid)
  }

  @Test
  override fun simpleTableDefinitionTest() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    NestedEnumTestUtilities.simpleTableDefinitionTest(buildEntityService(uuid), cleanup(uuid), uuid)
  }

  @Test
  override fun simpleTableDefinitionNullableTest() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    NestedEnumTestUtilities.simpleTableDefinitionNullableTest(buildEntityService(uuid), cleanup(uuid), uuid)
  }
}