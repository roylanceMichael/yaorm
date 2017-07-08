package org.roylance.yaorm.services.sqlite

import org.junit.Test
import org.roylance.yaorm.utilities.common.IWeakTypeTests
import org.roylance.yaorm.utilities.common.WeakTypeTestUtilities
import java.util.*

class SQLiteWeakTypeTests: SQLiteBase(), IWeakTypeTests {
    @Test
    override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough() {
        val uuid = UUID.randomUUID().toString()
        WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun directChildRemove() {
        val uuid = UUID.randomUUID().toString()
        WeakTypeTestUtilities.directChildRemove(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists() {
        val uuid = UUID.randomUUID().toString()
        WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakEvenIfNotExists(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun saveDeleteSaveNotRepeated() {
        val uuid = UUID.randomUUID().toString()
        WeakTypeTestUtilities.saveDeleteSaveNotRepeated(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated() {
        val uuid = UUID.randomUUID().toString()
        WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated(buildEntityService(uuid), cleanup(uuid))
    }

    @Test
    override fun repeatedAddRemoveTest() {
        val uuid = UUID.randomUUID().toString()
        WeakTypeTestUtilities.repeatedAddRemoveTest(buildEntityService(uuid), cleanup(uuid))
    }
}