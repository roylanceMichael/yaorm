package org.roylance.yaorm.services.proto

import com.google.protobuf.GeneratedMessageV3
import org.roylance.yaorm.YaormModel

abstract class BaseProtoGeneratedMessageBuilder: IProtoGeneratedMessageBuilder {
    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {
        if (YaormModel.Migration.getDescriptor().name.equals(name)) {
            return YaormModel.Migration.getDefaultInstance()
        }
        throw UnsupportedOperationException("could not find message ${name}")
    }
}
