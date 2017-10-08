package org.roylance.yaorm.utilities.common

interface IWeakTypeTests {
    fun repeatedAddRemoveTest()
    fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated()
    fun saveDeleteSaveNotRepeated()
    fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists()
    fun directChildRemove()
    fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough()
}