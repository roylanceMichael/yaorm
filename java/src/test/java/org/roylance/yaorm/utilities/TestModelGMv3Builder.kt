package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessage
import org.roylance.yaorm.TestingModelv3
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class TestModelGMv3Builder : BaseProtoGeneratedMessageBuilder() {
    override fun buildGeneratedMessage(name: String): GeneratedMessage {
        if (TestingModelv3.WorkerConfiguration.getDescriptor().name.equals(name)) {
            return TestingModelv3.WorkerConfiguration.getDefaultInstance()
        }
        else if (TestingModelv3.Log.getDescriptor().name.equals(name)) {
            return TestingModelv3.Log.getDefaultInstance()
        }
        else if (TestingModelv3.Dag.getDescriptor().name.equals(name)) {
            return TestingModelv3.Dag.getDefaultInstance()
        }
        else if (TestingModelv3.Task.getDescriptor().name.equals(name)) {
            return TestingModelv3.Task.getDefaultInstance()
        }
        else if (TestingModelv3.AddTaskToDag.getDescriptor().name.equals(name)) {
            return TestingModelv3.AddTaskToDag.getDefaultInstance()
        }
        else if (TestingModelv3.CompleteTask.getDescriptor().name.equals(name)) {
            return TestingModelv3.CompleteTask.getDefaultInstance()
        }
        else if (TestingModelv3.TaskDependency.getDescriptor().name.equals(name)) {
            return TestingModelv3.TaskDependency.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
