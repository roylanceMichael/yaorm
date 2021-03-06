package org.roylance.yaorm.utilities

import org.roylance.yaorm.TestingModel
import java.util.*

class DagBuilder {
    fun build(): TestingModel.Dag {
        val dagDefinition = TestingModel.Dag
                .newBuilder()
                .setId(UUID.randomUUID().toString())
                .setDisplay("amazing dag")

        val firstTask = buildTaskDefinition(FirstTaskId, dagDefinition)
        val secondTask = buildTaskDefinition(SecondTaskId, dagDefinition)
        val thirdTask = buildTaskDefinition(ThirdTaskId, dagDefinition)
        val fourthTask = buildTaskDefinition(FourthTaskId, dagDefinition)
        val fifthTask = buildTaskDefinition(FifthTaskId, dagDefinition)
        val sixthTask = buildTaskDefinition(SixthTaskId, dagDefinition)
        val seventhTask = buildTaskDefinition(SeventhTaskId, dagDefinition)
        val eigthTask = buildTaskDefinition(EigthTaskId, dagDefinition)
        val ninthTask = buildTaskDefinition(NinthTaskId, dagDefinition)
        val tenthTask = buildTaskDefinition(TenthTaskId, dagDefinition)

        tenthTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(ninthTask.id))
        ninthTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(sixthTask.id))
        eigthTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(seventhTask.id))
        seventhTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(fifthTask.id))
        sixthTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(thirdTask.id))
        fifthTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(fourthTask.id))
        fourthTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(firstTask.id))
        thirdTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(firstTask.id))
        secondTask.addDependencies(TestingModel.TaskDependency.newBuilder().setId(UUID.randomUUID().toString()).setParentTaskId(firstTask.id))

        dagDefinition.addFlattenedTasks(firstTask)
        dagDefinition.addFlattenedTasks(secondTask)
        dagDefinition.addFlattenedTasks(thirdTask)
        dagDefinition.addFlattenedTasks(fourthTask)
        dagDefinition.addFlattenedTasks(fifthTask)
        dagDefinition.addFlattenedTasks(sixthTask)
        dagDefinition.addFlattenedTasks(seventhTask)
        dagDefinition.addFlattenedTasks(eigthTask)
        dagDefinition.addFlattenedTasks(ninthTask)
        dagDefinition.addFlattenedTasks(tenthTask)

        dagDefinition.addUncompletedTasks(firstTask)
        dagDefinition.addUncompletedTasks(secondTask)
        dagDefinition.addUncompletedTasks(thirdTask)
        dagDefinition.addUncompletedTasks(fourthTask)
        dagDefinition.addUncompletedTasks(fifthTask)
        dagDefinition.addUncompletedTasks(sixthTask)
        dagDefinition.addUncompletedTasks(seventhTask)
        dagDefinition.addUncompletedTasks(eigthTask)
        dagDefinition.addUncompletedTasks(ninthTask)
        dagDefinition.addUncompletedTasks(tenthTask)

        return dagDefinition.build()
    }

    private fun buildTaskDefinition(id:String,
                                    dagDefinition:TestingModel.Dag.Builder):TestingModel.Task.Builder {
        return TestingModel.Task.newBuilder()
                .setDagId(dagDefinition.id)
                .setId(UUID.randomUUID().toString())
                .setDisplay(id)
    }

    companion object {
        const val FirstTaskId = "0 to 100"
        const val SecondTaskId = "100 to 200"
        const val ThirdTaskId = "200 to 300"
        const val FourthTaskId = "300 to 400"
        const val FifthTaskId = "400 to 500"
        const val SixthTaskId = "500 to 600"
        const val SeventhTaskId = "600 to 700"
        const val EigthTaskId = "700 to 800"
        const val NinthTaskId = "800 to 900"
        const val TenthTaskId = "900 to 1000"
    }
}