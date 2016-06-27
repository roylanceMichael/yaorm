package org.roylance.yaorm.services

interface IMergeOperations {
    fun update(key:String)
    fun insert(key:String)
    fun delete(key:String)
}
