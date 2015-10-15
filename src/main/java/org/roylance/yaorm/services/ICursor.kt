package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity

public interface ICursor<T> {
    public fun moveNext(): Boolean
    public fun <K, T: IEntity<K>> getRecord(): T
}
