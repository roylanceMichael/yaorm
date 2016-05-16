package org.roylance.yaorm.services.map

interface IMapCursor {
    fun getRecords(): List<Map<String, Any>>
}
