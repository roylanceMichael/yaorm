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
        else if (TestModel.SubChild.getDescriptor().name.equals(name)) {
            return TestModel.SubChild.getDefaultInstance()
        }
        else if (TestModel.SubSubChild.getDescriptor().name.equals(name)) {
            return TestModel.SubSubChild.getDefaultInstance()
        }
        else if (TestModel.SubSubSubChild.getDescriptor().name.equals(name)) {
            return TestModel.SubSubSubChild.getDefaultInstance()
        }

        throw InvalidClassException("bad class name given")
    }
}
