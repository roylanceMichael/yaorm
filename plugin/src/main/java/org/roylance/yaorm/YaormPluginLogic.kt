package org.roylance.yaorm

import java.io.File
import java.util.*

object YaormPluginLogic {
    private const val ResourceDirectoryLocation = "src/main/resources"

    fun execute(projectPackage: String, projectName: String) {
        val fullTexts = HashMap<String, String>()
        val resourceDirectory = File(ResourceDirectoryLocation)

        println("resource directory exists: ${resourceDirectory.exists()} and is directory: ${resourceDirectory.isDirectory}")
        if (resourceDirectory.exists() && resourceDirectory.isDirectory) {
            resourceDirectory.list()
                    .filter { it.endsWith(ModelClassBuilder.ProtoExtension) }
                    .forEach {
                    val foundFile = File(ResourceDirectoryLocation, it)
                    fullTexts[foundFile.name] = foundFile.readText()
                    println("processing :" + foundFile.name)
            }

            val allText = ModelClassBuilder.build(projectPackage, projectName, fullTexts)
            val pascalProjectName = ModelClassBuilder.makePascal(projectName)
            val generationModelName = "${pascalProjectName}GenerationModel.kt"

            val utilitiesFolder = File("src/main/java/" + projectPackage.replace(".", "/") + "/utilities")
            println("found utilities folder: " + utilitiesFolder.absoluteFile)

            if (!utilitiesFolder.exists()) {
                utilitiesFolder.mkdirs()
            }

            val actualFile = File(utilitiesFolder, generationModelName)
            actualFile.writeText(allText)
            println("writing actualFile: ${actualFile.absolutePath}")
        }
        else {
            println("not processing anything...")
        }
    }
}