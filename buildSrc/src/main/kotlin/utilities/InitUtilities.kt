package utilities

import java.nio.file.Paths

object InitUtilities {
    const val OsNameProperty = "os.name"

    const val DpkgDeb = "dpkg-deb"

    const val Chmod = "chmod"
    const val ChmodExecutable = "755"
    const val RemoveDirectory = "rm"
    const val Find = "find"
    const val Move = "mv"
    const val Bash = "bash"
    const val Curl = "curl"

    const val Gradle = "gradle"
    const val Maven = "mvn"

    const val Nuget = "nuget"
    const val DotNet = "dotnet"

    const val Python = "python"
    const val Pip = "pip"

    const val XCodeBuild = "xcodebuild"
    const val Carthage = "carthage"

    const val TypeScriptCompiler = "tsc"
    const val NPM = "npm"

    const val Protoc = "protoc"

    const val Separator = """---------------------------------------------------------------------------------------------------------"""

    const val MinimumRequiredErrorMessage = """Minimum requirements to run Yaclib not met. Please ensure the following requirements are met:
OS is OSX/MacOS or Linux
protoc, mvn, gradle, python, tsc, npm can all be located with "which"
optional: dotnet and nuget can be located with "which"
MAKE SURE THAT ~/.bashrc or ~/.bash_profile PATH contains references to the folder these apps are located in!
"""

    fun hasMinimumRequired(): Boolean {
        return isNotWindows() &&
                hasNPM() &&
                hasTypeScriptCompiler() &&
                hasPython() &&
                hasGradle() &&
                hasProtoc() &&
                hasMaven()
    }

    fun hasCSharp(): Boolean {
        return hasDotNet() && hasNuget()
    }

    fun hasProtoc(): Boolean {
        return FileProcessUtilities.getActualLocation(Protoc).isNotEmpty()
    }

    fun hasGradle(): Boolean {
        return FileProcessUtilities.getActualLocation(Gradle).isNotEmpty()
    }

    fun hasMaven(): Boolean {
        return FileProcessUtilities.getActualLocation(Maven).isNotEmpty()
    }

    fun hasDotNet(): Boolean {
        return FileProcessUtilities.getActualLocation(DotNet).isNotEmpty()
    }

    fun hasNuget(): Boolean {
        return FileProcessUtilities.getActualLocation(Nuget).isNotEmpty()
    }

    fun hasPython(): Boolean {
        return FileProcessUtilities.getActualLocation(Python).isNotEmpty()
    }

    fun hasTypeScriptCompiler(): Boolean {
        return FileProcessUtilities.getActualLocation(TypeScriptCompiler).isNotEmpty()
    }

    fun hasNPM(): Boolean {
        return FileProcessUtilities.getActualLocation(NPM).isNotEmpty()
    }

    fun isNotWindows(): Boolean {
        return !System.getProperty(OsNameProperty).toLowerCase().startsWith("window")
    }

    fun buildPhaseMessage(message: String): String {
        return """$Separator
$message
$Separator"""
    }

    fun buildBuildSrcProjectLocation(): String {
        return Paths.get("buildSrc").toString()
    }
}