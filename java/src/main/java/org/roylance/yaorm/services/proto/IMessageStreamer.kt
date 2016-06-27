package org.roylance.yaorm.services.proto

import com.google.protobuf.Message

interface IMessageStreamer {
    fun <T: Message> stream(message: T)
}
