package org.roylance.yaorm.services.proto

import com.google.protobuf.GeneratedMessage

interface IProtoGeneratedMessageBuilder {
    val name: String
    fun buildGeneratedMessage(name:String): GeneratedMessage
}
