package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity

interface ICursor<T> {
    fun <K, T: IEntity<K>> getRecords(): List<T>
}
