package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.TestingModelV2
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class TestModelGMv2Builder: BaseProtoGeneratedMessageBuilder() {
    override fun buildGeneratedMessage(name: String): GeneratedMessage {
        if (TestingModelV2.Child.getDescriptor().name.equals(name)) {
            return TestingModelV2.Child.getDefaultInstance()
        }
        else if (TestingModelV2.SimpleInsertTest.getDescriptor().name.equals(name)) {
            return TestingModelV2.SimpleInsertTest.getDefaultInstance()
        }
        else if (TestingModelV2.SubChild.getDescriptor().name.equals(name)) {
            return TestingModelV2.SubChild.getDefaultInstance()
        }
        else if (TestingModelV2.SubSubChild.getDescriptor().name.equals(name)) {
            return TestingModelV2.SubSubChild.getDefaultInstance()
        }
        else if (TestingModelV2.SubSubSubChild.getDescriptor().name.equals(name)) {
            return TestingModelV2.SubSubSubChild.getDefaultInstance()
        }
        else if (TestingModelV2.Person.getDescriptor().name.equals(name)) {
            return TestingModelV2.Person.getDefaultInstance()
        }
        else if (TestingModelV2.Address.getDescriptor().name.equals(name)) {
            return TestingModelV2.Address.getDefaultInstance()
        }
        else if (TestingModelV2.Phone.getDescriptor().name.equals(name)) {
            return TestingModelV2.Phone.getDefaultInstance()
        }
        else if (TestingModelV2.WorkerConfiguration.getDescriptor().name.equals(name)) {
            return TestingModelV2.WorkerConfiguration.getDefaultInstance()
        }
        else if (TestingModelV2.Log.getDescriptor().name.equals(name)) {
            return TestingModelV2.Log.getDefaultInstance()
        }
        else if (TestingModelV2.Dag.getDescriptor().name.equals(name)) {
            return TestingModelV2.Dag.getDefaultInstance()
        }
        else if (TestingModelV2.Task.getDescriptor().name.equals(name)) {
            return TestingModelV2.Task.getDefaultInstance()
        }
        else if (TestingModelV2.AddTaskToDag.getDescriptor().name.equals(name)) {
            return TestingModelV2.AddTaskToDag.getDefaultInstance()
        }
        else if (TestingModelV2.CompleteTask.getDescriptor().name.equals(name)) {
            return TestingModelV2.CompleteTask.getDefaultInstance()
        }
        else if (TestingModelV2.TaskDependency.getDescriptor().name.equals(name)) {
            return TestingModelV2.TaskDependency.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
