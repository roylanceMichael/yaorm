package org.roylance.yaorm.services.proto

import com.google.protobuf.GeneratedMessage

interface IProtoGeneratedMessageBuilder {
    fun buildGeneratedMessage(name:String): GeneratedMessage
}
