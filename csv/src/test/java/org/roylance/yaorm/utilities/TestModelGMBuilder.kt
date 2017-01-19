package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessageV3
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class TestModelGMBuilder : BaseProtoGeneratedMessageBuilder() {
    override val name: String
        get() = "TestingModel"

    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {
        if (TestingModel.Child.getDescriptor().name == name) {
            return TestingModel.Child.getDefaultInstance()
        }
        else if (TestingModel.SimpleInsertTest.getDescriptor().name == name) {
            return TestingModel.SimpleInsertTest.getDefaultInstance()
        }
        else if (TestingModel.SubChild.getDescriptor().name == name) {
            return TestingModel.SubChild.getDefaultInstance()
        }
        else if (TestingModel.SubSubChild.getDescriptor().name == name) {
            return TestingModel.SubSubChild.getDefaultInstance()
        }
        else if (TestingModel.SubSubSubChild.getDescriptor().name == name) {
            return TestingModel.SubSubSubChild.getDefaultInstance()
        }
        else if (TestingModel.Person.getDescriptor().name == name) {
            return TestingModel.Person.getDefaultInstance()
        }
        else if (TestingModel.Address.getDescriptor().name == name) {
            return TestingModel.Address.getDefaultInstance()
        }
        else if (TestingModel.Phone.getDescriptor().name == name) {
            return TestingModel.Phone.getDefaultInstance()
        }
        else if (TestingModel.WorkerConfiguration.getDescriptor().name == name) {
            return TestingModel.WorkerConfiguration.getDefaultInstance()
        }
        else if (TestingModel.Log.getDescriptor().name == name) {
            return TestingModel.Log.getDefaultInstance()
        }
        else if (TestingModel.Dag.getDescriptor().name == name) {
            return TestingModel.Dag.getDefaultInstance()
        }
        else if (TestingModel.Task.getDescriptor().name == name) {
            return TestingModel.Task.getDefaultInstance()
        }
        else if (TestingModel.AddTaskToDag.getDescriptor().name == name) {
            return TestingModel.AddTaskToDag.getDefaultInstance()
        }
        else if (TestingModel.CompleteTask.getDescriptor().name == name) {
            return TestingModel.CompleteTask.getDefaultInstance()
        }
        else if (TestingModel.TaskDependency.getDescriptor().name == name) {
            return TestingModel.TaskDependency.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}