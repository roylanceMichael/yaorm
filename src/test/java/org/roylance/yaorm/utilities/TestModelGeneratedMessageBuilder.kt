package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.TestModel
import org.roylance.yaorm.services.proto.IProtoGeneratedMessageBuilder
import java.io.InvalidClassException

class TestModelGeneratedMessageBuilder: IProtoGeneratedMessageBuilder {
    override fun buildGeneratedMessage(name: String): GeneratedMessage {
        if (TestModel.Child.getDescriptor().name.equals(name)) {
            return TestModel.Child.getDefaultInstance()
        }
        else if (TestModel.SimpleInsertTest.getDescriptor().name.equals(name)) {
            return TestModel.SimpleInsertTest.getDefaultInstance()
        }

        throw InvalidClassException("bad class name given")
    }
}
