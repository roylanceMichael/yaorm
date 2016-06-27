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
        const val LastSeenName: String = "lastSeen"
        const val IdName: String = "id"
        const val BeaconIdName: String = "beaconId"
        const val MajorIdName: String = "majorId"
        const val MinorIdName: String = "minorId"
        const val ActiveName: String = "active"
        const val CachedNameName: String = "cachedName"
    }
}