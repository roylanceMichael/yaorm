package org.roylance.yaorm

import java.util.*

object ModelClassBuilder {
    const val ProtoExtension = ".proto"
    private const val Underscore = '_'

    private val MessageRegex = Regex("message\\s+([a-zA-Z0-9_]+)\\s*\\{")
    private val PackageRegex = Regex("package\\s+([a-zA-Z0-9\\.]+);")

    fun build(projectPackage: String,
              projectName: String,
              fullTexts: Map<String, String>): String {
        val pascalProjectName = makePascal(projectName)
        val generationModelName = "${pascalProjectName}GenerationModel"

        val workspace = StringBuilder("package  $projectPackage.utilities")
        workspace.appendln()
        workspace.appendln("import  com.google.protobuf.GeneratedMessageV3")
        workspace.appendln("import  org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder")
        workspace.appendln()
        workspace.appendln("object  $generationModelName: BaseProtoGeneratedMessageBuilder() {")
        workspace.appendln("    override val name: String")
        workspace.appendln("        get() = \"${pascalProjectName}Model\"")
        workspace.appendln()
        workspace.appendln("    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {")

        val allClasses = getFullClasses(fullTexts)
        allClasses.forEach {
            workspace.appendln("        if ($it.getDescriptor().name == name) {")
            workspace.appendln("            return $it.getDefaultInstance()")
            workspace.appendln("        }")
        }

        workspace.appendln("        return super.buildGeneratedMessage(name)")
        workspace.appendln("    }")
        workspace.append("}")

        return workspace.toString()
    }

    fun getFullClasses(fullTexts: Map<String, String>): List<String> {
        val returnList = ArrayList<String>()

        fullTexts.forEach {
            val actualJavaOuterClassName = makeFileNameJavaClass(it.key)
            val actualProtoFile = it.value

            val packageMatch = PackageRegex.find(actualProtoFile)
            val foundPackage: String
            if (packageMatch != null && packageMatch.groupValues.any()) {
                foundPackage = packageMatch.groupValues.last()
            }
            else {
                return@forEach
            }

            val classMatches = MessageRegex.findAll(actualProtoFile)
            classMatches.filter { it.groupValues.isNotEmpty() }.forEach { match ->
                val firstGroupValue = match.groupValues.last()
                returnList.add("$foundPackage.$actualJavaOuterClassName.$firstGroupValue")
            }

        }

        return returnList
    }

    fun makeFileNameJavaClass(fileName: String): String? {
        if (fileName.endsWith(ProtoExtension)) {
            val fileNameWithoutExtension = fileName.substring(0, fileName.length - ProtoExtension.length)
            return makePascal(fileNameWithoutExtension)
        }

        return null
    }

    fun makePascal(name: String): String {
        val workspace = StringBuilder()

        var previousWasUnderscore = false
        var i = 0

        while (i < name.length) {
            if (i == 0 || previousWasUnderscore) {
                workspace.append(name[i].toUpperCase())
                previousWasUnderscore = false
            }
            else if (name[i] == Underscore) {
                previousWasUnderscore = true
            }
            else {
                workspace.append(name[i])
            }

            i++
        }

        return workspace.toString()
    }
}