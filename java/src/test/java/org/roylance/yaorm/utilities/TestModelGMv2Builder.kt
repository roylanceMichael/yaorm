package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.TestingModelv2
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class TestModelGMv2Builder: BaseProtoGeneratedMessageBuilder() {
    override fun buildGeneratedMessage(name: String): GeneratedMessage {
        if (TestingModelv2.Child.getDescriptor().name.equals(name)) {
            return TestingModelv2.Child.getDefaultInstance()
        }
        else if (TestingModelv2.SimpleInsertTest.getDescriptor().name.equals(name)) {
            return TestingModelv2.SimpleInsertTest.getDefaultInstance()
        }
        else if (TestingModelv2.SubChild.getDescriptor().name.equals(name)) {
            return TestingModelv2.SubChild.getDefaultInstance()
        }
        else if (TestingModelv2.SubSubChild.getDescriptor().name.equals(name)) {
            return TestingModelv2.SubSubChild.getDefaultInstance()
        }
        else if (TestingModelv2.SubSubSubChild.getDescriptor().name.equals(name)) {
            return TestingModelv2.SubSubSubChild.getDefaultInstance()
        }
        else if (TestingModelv2.Person.getDescriptor().name.equals(name)) {
            return TestingModelv2.Person.getDefaultInstance()
        }
        else if (TestingModelv2.Address.getDescriptor().name.equals(name)) {
            return TestingModelv2.Address.getDefaultInstance()
        }
        else if (TestingModelv2.Phone.getDescriptor().name.equals(name)) {
            return TestingModelv2.Phone.getDefaultInstance()
        }
        else if (TestingModelv2.WorkerConfiguration.getDescriptor().name.equals(name)) {
            return TestingModelv2.WorkerConfiguration.getDefaultInstance()
        }
        else if (TestingModelv2.Log.getDescriptor().name.equals(name)) {
            return TestingModelv2.Log.getDefaultInstance()
        }
        else if (TestingModelv2.Dag.getDescriptor().name.equals(name)) {
            return TestingModelv2.Dag.getDefaultInstance()
        }
        else if (TestingModelv2.Task.getDescriptor().name.equals(name)) {
            return TestingModelv2.Task.getDefaultInstance()
        }
        else if (TestingModelv2.AddTaskToDag.getDescriptor().name.equals(name)) {
            return TestingModelv2.AddTaskToDag.getDefaultInstance()
        }
        else if (TestingModelv2.CompleteTask.getDescriptor().name.equals(name)) {
            return TestingModelv2.CompleteTask.getDefaultInstance()
        }
        else if (TestingModelv2.TaskDependency.getDescriptor().name.equals(name)) {
            return TestingModelv2.TaskDependency.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
