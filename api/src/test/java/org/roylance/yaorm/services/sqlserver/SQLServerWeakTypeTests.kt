package org.roylance.yaorm.services.sqlserver

import org.junit.Test
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.common.IWeakTypeTests
import org.roylance.yaorm.utilities.common.WeakTypeTestUtilities

class SQLServerWeakTypeTests: SQLServerBase(), IWeakTypeTests {
    @Test
    override fun repeatedAddRemoveTest() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        WeakTypeTestUtilities.repeatedAddRemoveTest(buildEntityService(), cleanup())
    }
    @Test
    override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated(buildEntityService(), cleanup())
    }
    @Test
    override fun saveDeleteSaveNotRepeated() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        WeakTypeTestUtilities.saveDeleteSaveNotRepeated(buildEntityService(), cleanup())
    }
    @Test
    override fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakEvenIfNotExists(buildEntityService(), cleanup())
    }
    @Test
    override fun directChildRemove() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        WeakTypeTestUtilities.directChildRemove(buildEntityService(), cleanup())
    }
    @Test
    override fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough() {
        if (!ConnectionUtilities.runSQLServerTests()) {
            return
        }
        WeakTypeTestUtilities.shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough(buildEntityService(), cleanup())
    }
}