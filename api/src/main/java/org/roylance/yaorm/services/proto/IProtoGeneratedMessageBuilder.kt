package org.roylance.yaorm.services.proto

import com.google.protobuf.GeneratedMessageV3

interface IProtoGeneratedMessageBuilder {
    val name: String
    fun buildGeneratedMessage(name:String): GeneratedMessageV3
}
