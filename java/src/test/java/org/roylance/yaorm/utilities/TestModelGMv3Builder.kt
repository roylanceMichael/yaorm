package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessageV3
import org.roylance.yaorm.TestingModelV3
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class TestModelGMv3Builder : BaseProtoGeneratedMessageBuilder() {
    override val name: String
        get() = "TestingModel"

    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {
        if (TestingModelV3.WorkerConfiguration.getDescriptor().name.equals(name)) {
            return TestingModelV3.WorkerConfiguration.getDefaultInstance()
        }
        else if (TestingModelV3.Log.getDescriptor().name.equals(name)) {
            return TestingModelV3.Log.getDefaultInstance()
        }
        else if (TestingModelV3.Dag.getDescriptor().name.equals(name)) {
            return TestingModelV3.Dag.getDefaultInstance()
        }
        else if (TestingModelV3.Task.getDescriptor().name.equals(name)) {
            return TestingModelV3.Task.getDefaultInstance()
        }
        else if (TestingModelV3.AddTaskToDag.getDescriptor().name.equals(name)) {
            return TestingModelV3.AddTaskToDag.getDefaultInstance()
        }
        else if (TestingModelV3.CompleteTask.getDescriptor().name.equals(name)) {
            return TestingModelV3.CompleteTask.getDefaultInstance()
        }
        else if (TestingModelV3.TaskDependency.getDescriptor().name.equals(name)) {
            return TestingModelV3.TaskDependency.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
