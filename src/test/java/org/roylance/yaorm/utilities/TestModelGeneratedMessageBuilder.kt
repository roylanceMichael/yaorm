package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.proto.IProtoGeneratedMessageBuilder
import java.io.InvalidClassException

class TestModelGeneratedMessageBuilder: IProtoGeneratedMessageBuilder {
    override fun buildGeneratedMessage(name: String): GeneratedMessage {
        if (TestingModel.Child.getDescriptor().name.equals(name)) {
            return TestingModel.Child.getDefaultInstance()
        }
        else if (TestingModel.SimpleInsertTest.getDescriptor().name.equals(name)) {
            return TestingModel.SimpleInsertTest.getDefaultInstance()
        }
        else if (TestingModel.SubChild.getDescriptor().name.equals(name)) {
            return TestingModel.SubChild.getDefaultInstance()
        }
        else if (TestingModel.SubSubChild.getDescriptor().name.equals(name)) {
            return TestingModel.SubSubChild.getDefaultInstance()
        }
        else if (TestingModel.SubSubSubChild.getDescriptor().name.equals(name)) {
            return TestingModel.SubSubSubChild.getDefaultInstance()
        }

        throw InvalidClassException("bad class name given")
    }
}
