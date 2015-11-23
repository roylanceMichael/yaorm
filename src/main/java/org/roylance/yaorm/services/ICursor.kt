package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity

public interface ICursor<T> {
    public fun <K, T: IEntity<K>> getRecords(): List<T>
}
