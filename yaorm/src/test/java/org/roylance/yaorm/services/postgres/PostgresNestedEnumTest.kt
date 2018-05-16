package org.roylance.yaorm.services.postgres

import org.junit.Test
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.INestedEnumTest
import org.roylance.yaorm.utilities.common.NestedEnumTestUtilities

class PostgresNestedEnumTest : PostgresBase(), INestedEnumTest {
  @Test
  override fun simpleMultipleStringsTest() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    ConnectionUtilities.getPostgresConnectionInfo()
    val sourceConnection = PostgresConnectionSourceFactory(
        ConnectionUtilities.postgresHost!!,
        ConnectionUtilities.postgresPort!!,
        ConnectionUtilities.postgresDatabase!!,
        ConnectionUtilities.postgresUserName!!,
        ConnectionUtilities.postgresPassword!!,
        false)

    val granularDatabaseService = JDBCGranularDatabaseService(
        sourceConnection,
        false,
        true)
    val generatorService = PostgresGeneratorService()
    val entityService = EntityService(granularDatabaseService, generatorService)
    NestedEnumTestUtilities.simpleMultipleStringsTest(entityService, cleanup())
  }

  @Test
  override fun simplePassThroughExecutionsTest() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    ConnectionUtilities.getPostgresConnectionInfo()
    val sourceConnection = PostgresConnectionSourceFactory(
        ConnectionUtilities.postgresHost!!,
        ConnectionUtilities.postgresPort!!,
        ConnectionUtilities.postgresDatabase!!,
        ConnectionUtilities.postgresUserName!!,
        ConnectionUtilities.postgresPassword!!,
        false)

    val granularDatabaseService = JDBCGranularDatabaseService(
        sourceConnection,
        false,
        true)
    val generatorService = PostgresGeneratorService()
    val entityService = EntityService(granularDatabaseService, generatorService)
    NestedEnumTestUtilities.simplePassThroughExecutionsTest(entityService, cleanup())
  }

  @Test
  override fun simplePassThroughTest() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    NestedEnumTestUtilities.simplePassThroughTest(buildEntityService(), cleanup())
  }

  @Test
  override fun simplePassThroughTest2() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    NestedEnumTestUtilities.simplePassThroughTest2(buildEntityService(), cleanup())
  }

  @Test
  override fun simpleTablesTest() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    NestedEnumTestUtilities.simpleTablesTest(buildEntityService(), cleanup(), ConnectionUtilities.postgresDatabase!!)
  }

  @Test
  override fun simpleTableDefinitionTest() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    NestedEnumTestUtilities.simpleTableDefinitionTest(buildEntityService(), cleanup(), ConnectionUtilities.postgresDatabase!!)
  }

  @Test
  override fun simpleTableDefinitionNullableTest() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    NestedEnumTestUtilities.simpleTableDefinitionNullableTest(buildEntityService(), cleanup(), ConnectionUtilities.postgresDatabase!!)
  }
}