package org.roylance.yaorm.services.mysql.normal

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IWeakTypeTests
import org.roylance.yaorm.utilities.common.WeakTypeTestUtilities

class MySQLWeakTypeTests : MySQLBase(), IWeakTypeTests {
  @Test
  override fun shouldBeAbleToSaveDeepNestedTree() {
    if (!ConnectionUtilities.runMySQLTests()) {
      return
    }
    WeakTypeTestUtilities.shouldBeAbleToSaveDeepNestedTree(buildEntityService(), cleanup())
  }

  @Test
  override fun repeatedAddRemoveTest() {
    if (!ConnectionUtilities.runMySQLTests()) {
      return
    }
    WeakTypeTestUtilities.repeatedAddRemoveTest(buildEntityService(), cleanup())
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated() {
    if (!ConnectionUtilities.runMySQLTests()) {
      return
    }
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated(
        buildEntityService(), cleanup())
  }

  @Test
  override fun saveDeleteSaveNotRepeated() {
    if (!ConnectionUtilities.runMySQLTests()) {
      return
    }
    WeakTypeTestUtilities.saveDeleteSaveNotRepeated(buildEntityService(), cleanup())
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists() {
    if (!ConnectionUtilities.runMySQLTests()) {
      return
    }
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakEvenIfNotExists(buildEntityService(),
        cleanup())
  }

  @Test
  override fun directChildRemove() {
    if (!ConnectionUtilities.runMySQLTests()) {
      return
    }
    WeakTypeTestUtilities.directChildRemove(buildEntityService(), cleanup())
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough() {
    if (!ConnectionUtilities.runMySQLTests()) {
      return
    }
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough(
        buildEntityService(), cleanup())
  }
}