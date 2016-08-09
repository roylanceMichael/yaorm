package org.roylance.yaorm.services.proto

import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.YaormModel

abstract class BaseProtoGeneratedMessageBuilder: IProtoGeneratedMessageBuilder {
    override fun buildGeneratedMessage(name: String): GeneratedMessage {
        if (YaormModel.Migration.getDescriptor().name.equals(name)) {
            return YaormModel.Migration.getDefaultInstance()
        }
        throw UnsupportedOperationException("could not find message ${name}")
    }
}
