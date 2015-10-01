package org.roylance.yaorm.services

public interface ICursor<T> {
    public fun moveNext(): Boolean
    public fun getRecord(): T
}
