package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessageV3
import org.roylance.yaorm.TestingModelV2
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class TestModelGMv2Builder: BaseProtoGeneratedMessageBuilder() {
    override val name: String
        get() = "TestingModel"

    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {
        if (TestingModelV2.Child.getDescriptor().name == name) {
            return TestingModelV2.Child.getDefaultInstance()
        }
        else if (TestingModelV2.SimpleInsertTest.getDescriptor().name == name) {
            return TestingModelV2.SimpleInsertTest.getDefaultInstance()
        }
        else if (TestingModelV2.SubChild.getDescriptor().name == name) {
            return TestingModelV2.SubChild.getDefaultInstance()
        }
        else if (TestingModelV2.SubSubChild.getDescriptor().name == name) {
            return TestingModelV2.SubSubChild.getDefaultInstance()
        }
        else if (TestingModelV2.SubSubSubChild.getDescriptor().name == name) {
            return TestingModelV2.SubSubSubChild.getDefaultInstance()
        }
        else if (TestingModelV2.Person.getDescriptor().name == name) {
            return TestingModelV2.Person.getDefaultInstance()
        }
        else if (TestingModelV2.Address.getDescriptor().name == name) {
            return TestingModelV2.Address.getDefaultInstance()
        }
        else if (TestingModelV2.Phone.getDescriptor().name == name) {
            return TestingModelV2.Phone.getDefaultInstance()
        }
        else if (TestingModelV2.WorkerConfiguration.getDescriptor().name == name) {
            return TestingModelV2.WorkerConfiguration.getDefaultInstance()
        }
        else if (TestingModelV2.Log.getDescriptor().name == name) {
            return TestingModelV2.Log.getDefaultInstance()
        }
        else if (TestingModelV2.Dag.getDescriptor().name == name) {
            return TestingModelV2.Dag.getDefaultInstance()
        }
        else if (TestingModelV2.Task.getDescriptor().name == name) {
            return TestingModelV2.Task.getDefaultInstance()
        }
        else if (TestingModelV2.AddTaskToDag.getDescriptor().name == name) {
            return TestingModelV2.AddTaskToDag.getDefaultInstance()
        }
        else if (TestingModelV2.CompleteTask.getDescriptor().name == name) {
            return TestingModelV2.CompleteTask.getDefaultInstance()
        }
        else if (TestingModelV2.TaskDependency.getDescriptor().name == name) {
            return TestingModelV2.TaskDependency.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
