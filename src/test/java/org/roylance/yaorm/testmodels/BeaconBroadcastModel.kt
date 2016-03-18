package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity
import java.util.*

class BeaconBroadcastModel(
        override var id:String = UUID.randomUUID().toString(),
        var beaconId: String = "",
        var majorId: Int = 0,
        var minorId: Int = 0,
        var active: Boolean = false,
        var cachedName: String = "",
        var lastSeen: Long = 0) : IEntity {

    companion object {
        val LastSeenName: String = "lastSeen"
        val IdName: String = "id"
        val BeaconIdName: String = "beaconId"
        val MajorIdName: String = "majorId"
        val MinorIdName: String = "minorId"
        val ActiveName: String = "active"
        val CachedNameName: String = "cachedName"
    }
}