package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity

public class BeaconBroadcastModel(
        public override var id:Int = 0,
        public var beaconId: String = "",
        public var majorId: Int = 0,
        public var minorId: Int = 0,
        public var isActive: Boolean = false,
        public var cachedName: String = "",
        public var lastSeen: Long = 0) : IEntity<Int> {

    companion object {
        public val TableName: String = "beaconBroadcasts"
        public val IdName: String = "id"
        public val BeaconIdName: String = "beaconId"
        public val MajorIdName: String = "majorId"
        public val MinorIdName: String = "minorId"
        public val IsActiveName: String = "isActive"
        public val CachedNameName: String = "cachedName"
    }
}