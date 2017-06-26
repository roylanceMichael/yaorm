package tasks.common.protogen

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import utilities.Tokens
import java.nio.file.Paths

class JavaProtoGenTask : DefaultTask() {
    @TaskAction
    fun run() {
        // need to do a few things
        // location is
        // (root)/java/client

        val javaOutputLocation = Paths.get("java", "client", "src", "main", "java").toString()
        val protoInput = Paths.get("buildSrc")

        val arguments = ArrayList<String>()
        arguments.add("-I=$protoInput")
        arguments.add("--proto_path=$protoInput")
        arguments.add("--java_out=$javaOutputLocation")
        arguments.add("$protoInput/*.proto")

        project.exec {
            it.commandLine(Tokens.ProtocName)
            it.args(arguments)
        }
    }
}