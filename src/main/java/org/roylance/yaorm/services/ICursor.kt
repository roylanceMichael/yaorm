package org.roylance.yaorm.services

import org.roylance.yaorm.models.IEntity

interface ICursor<T> {
    fun <T: IEntity> getRecords(): List<T>
}
