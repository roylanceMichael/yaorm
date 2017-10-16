package org.roylance.yaorm.services.postgres

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IWeakTypeTests
import org.roylance.yaorm.utilities.common.WeakTypeTestUtilities

class PostgresWeakTypeTests : PostgresBase(), IWeakTypeTests {
  @Test
  override fun shouldBeAbleToSaveDeepNestedTree() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    WeakTypeTestUtilities.shouldBeAbleToSaveDeepNestedTree(buildEntityService(), cleanup())
  }

  @Test
  override fun repeatedAddRemoveTest() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    WeakTypeTestUtilities.repeatedAddRemoveTest(buildEntityService(), cleanup())
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated(
        buildEntityService(), cleanup())
  }

  @Test
  override fun saveDeleteSaveNotRepeated() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    WeakTypeTestUtilities.saveDeleteSaveNotRepeated(buildEntityService(), cleanup())
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakEvenIfNotExists(buildEntityService(),
        cleanup())
  }

  @Test
  override fun directChildRemove() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    WeakTypeTestUtilities.directChildRemove(buildEntityService(), cleanup())
  }

  @Test
  override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough() {
    if (!ConnectionUtilities.runPostgresTests()) {
      return
    }
    WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough(
        buildEntityService(), cleanup())
  }
}