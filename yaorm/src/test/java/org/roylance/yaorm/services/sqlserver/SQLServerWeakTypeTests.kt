package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IWeakTypeTests
import org.roylance.yaorm.utilities.common.WeakTypeTestUtilities

class SQLServerWeakTypeTests : SQLServerBase(), IWeakTypeTests {
  @Test
  override fun shouldBeAbleToSaveDeepNestedTree() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    WeakTypeTestUtilities.shouldBeAbleToSaveDeepNestedTree(buildEntityService(uuid), cleanup(uuid))
  }

  @Test
  override fun repeatedAddRemoveTest() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    WeakTypeTestUtilities.repeatedAddRemoveTest(buildEntityService(uuid), cleanup(uuid))
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated(
        buildEntityService(uuid), cleanup(uuid))
  }

  @Test
  override fun saveDeleteSaveNotRepeated() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    WeakTypeTestUtilities.saveDeleteSaveNotRepeated(buildEntityService(uuid), cleanup(uuid))
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakEvenIfNotExists(buildEntityService(uuid),
        cleanup(uuid))
  }

  @Test
  override fun directChildRemove() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    WeakTypeTestUtilities.directChildRemove(buildEntityService(uuid), cleanup(uuid))
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough() {
    if (!ConnectionUtilities.runSQLServerTests()) {
      return
    }
    val uuid = ConnectionUtilities.buildSafeUUID()
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough(
        buildEntityService(uuid), cleanup(uuid))
  }
}