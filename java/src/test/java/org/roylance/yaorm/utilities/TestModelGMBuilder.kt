package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.TestingModel
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class TestModelGMBuilder : BaseProtoGeneratedMessageBuilder() {
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
        else if (TestingModel.Person.getDescriptor().name.equals(name)) {
            return TestingModel.Person.getDefaultInstance()
        }
        else if (TestingModel.Address.getDescriptor().name.equals(name)) {
            return TestingModel.Address.getDefaultInstance()
        }
        else if (TestingModel.Phone.getDescriptor().name.equals(name)) {
            return TestingModel.Phone.getDefaultInstance()
        }
        else if (TestingModel.WorkerConfiguration.getDescriptor().name.equals(name)) {
            return TestingModel.WorkerConfiguration.getDefaultInstance()
        }
        else if (TestingModel.Log.getDescriptor().name.equals(name)) {
            return TestingModel.Log.getDefaultInstance()
        }
        else if (TestingModel.Dag.getDescriptor().name.equals(name)) {
            return TestingModel.Dag.getDefaultInstance()
        }
        else if (TestingModel.Task.getDescriptor().name.equals(name)) {
            return TestingModel.Task.getDefaultInstance()
        }
        else if (TestingModel.AddTaskToDag.getDescriptor().name.equals(name)) {
            return TestingModel.AddTaskToDag.getDefaultInstance()
        }
        else if (TestingModel.CompleteTask.getDescriptor().name.equals(name)) {
            return TestingModel.CompleteTask.getDefaultInstance()
        }
        else if (TestingModel.TaskDependency.getDescriptor().name.equals(name)) {
            return TestingModel.TaskDependency.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
