package org.roylance.yaorm.services.entity

import org.roylance.yaorm.models.IEntity

interface ICursor<out T> {
    fun <T: IEntity> getRecords(): List<T>
}
