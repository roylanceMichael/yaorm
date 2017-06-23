package utilities

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.io.IOUtils
import java.io.*
import java.util.*

object FileProcessUtilities {
    private const val Space = " "
    private const val TempDirectory = "java.io.tmpdir"

    private val knownApplicationLocations = HashMap<String, String>()
    private val whitespaceRegex = Regex("\\s+")

    fun buildCommand(application: String, allArguments: String):List<String> {
        val returnList = ArrayList<String>()
        val actualApplicationLocation = getActualLocation(application)
        print(actualApplicationLocation)

        if (actualApplicationLocation.isEmpty()) {
            returnList.add(application)
        }
        else {
            returnList.add(actualApplicationLocation)
        }

        val splitArguments = allArguments.split(whitespaceRegex)
        returnList.addAll(splitArguments)

        return returnList
    }

    fun readFile(path: String): String {
        val foundFile = File(path)
        return foundFile.readText()
    }

    fun writeFile(file: String, path: String) {
        val newFile = File(path)
        newFile.writeText(file)
    }

    fun handleProcess(process: ProcessBuilder, name: String) {
        try {
            process.redirectError(ProcessBuilder.Redirect.INHERIT)
            process.redirectOutput(ProcessBuilder.Redirect.INHERIT)
            process.start().waitFor()
            println("finished $name")
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun buildReport(process: Process): MusubiModel.ProcessReport {
        val returnReport = MusubiModel.ProcessReport.newBuilder()

        val inputWriter = StringWriter()
        val errorWriter = StringWriter()

        IOUtils.copy(process.inputStream, inputWriter)
        IOUtils.copy(process.errorStream, errorWriter)

        return returnReport.setErrorOutput(errorWriter.toString()).setNormalOutput(inputWriter.toString()).build()
    }

    fun executeProcess(location: String,
                       application: String,
                       allArguments: String): MusubiModel.ProcessReport {
        val tempFile = File(getTempDirectory(), UUID.randomUUID().toString())
        val actualCommand = buildCommand(application, allArguments).joinToString(Space)
        val tempScript = buildTempScript(location, actualCommand)
        tempFile.writeText(tempScript)

        try {
            Runtime.getRuntime().exec("${InitUtilities.Chmod} ${InitUtilities.ChmodExecutable} ${tempFile.absolutePath}")
            val process = Runtime.getRuntime().exec(tempFile.absolutePath)
            process.waitFor()

            val report = buildReport(process).toBuilder()
            report.normalOutput = report.normalOutput + "\n" + tempScript
            return report.build()
        }
        finally {
            tempFile.delete()
        }
    }

    fun executeScript(location: String, application: String, script: String): MusubiModel.ProcessReport {
        val tempScript = File(getTempDirectory(), UUID.randomUUID().toString())
        tempScript.writeText(script)

        val tempFile = File(getTempDirectory(), UUID.randomUUID().toString())
        val actualCommand = buildCommand(application, tempScript.absolutePath).joinToString(Space)

        val tempExecuteScript = buildTempScript(location, actualCommand)
        tempFile.writeText(tempExecuteScript)

        try {
            Runtime.getRuntime().exec("${InitUtilities.Chmod} ${InitUtilities.ChmodExecutable} ${tempFile.absolutePath}")
            Runtime.getRuntime().exec("${InitUtilities.Chmod} ${InitUtilities.ChmodExecutable} ${tempScript.absolutePath}")
            val process = Runtime.getRuntime().exec(tempFile.absolutePath)
            process.waitFor()

            val report = buildReport(process).toBuilder()
            report.normalOutput = report.normalOutput + "\n" + tempScript
            return report.build()
        }
        finally {
            tempScript.delete()
            tempFile.delete()
        }
    }

    fun createTarFromDirectory(inputDirectory: String, outputFile: String, directoriesToExclude: HashSet<String>): Boolean {
        val directory = File(inputDirectory)

        if (!directory.exists()) {
            return false
        }

        val outputStream = FileOutputStream(File(outputFile))
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        val gzipOutputStream = GzipCompressorOutputStream(bufferedOutputStream)
        val tarOutputStream = TarArchiveOutputStream(gzipOutputStream)
        try {
            addFileToTarGz(tarOutputStream, inputDirectory, "", directoriesToExclude)
        }
        finally {
            tarOutputStream.finish()
            tarOutputStream.close()
            gzipOutputStream.close()
            bufferedOutputStream.close()
            outputStream.close()
        }

        return true
    }

    fun getActualLocation(application: String): String {
        if (knownApplicationLocations.containsKey(application)) {
            return knownApplicationLocations[application]!!
        }

        val tempFile = File(getTempDirectory(), UUID.randomUUID().toString())
        tempFile.writeText("""#!/usr/bin/env bash
. ~/.bash_profile
. ~/.bashrc
which $application""")

        print(tempFile.readText())

        val inputWriter = StringWriter()
        try {
            Runtime.getRuntime().exec("${InitUtilities.Chmod} ${InitUtilities.ChmodExecutable} ${tempFile.absolutePath}")
            val process = Runtime.getRuntime().exec(tempFile.absolutePath)
            process.waitFor()

            IOUtils.copy(process.inputStream, inputWriter)
            return inputWriter.toString().trim()
        }
        finally {
            inputWriter.close()
            tempFile.delete()
        }
    }

    private fun addFileToTarGz(tarOutputStream: TarArchiveOutputStream, path: String, base: String, directoriesToExclude: HashSet<String>) {
        val fileOrDirectory = File(path)
        if (directoriesToExclude.contains(fileOrDirectory.name)) {
            return
        }

        val entryName: String
        if (base.isEmpty()) {
            entryName = "."
        }
        else {
            entryName = base + fileOrDirectory.name
        }

        val tarEntry = TarArchiveEntry(fileOrDirectory, entryName)
        tarOutputStream.putArchiveEntry(tarEntry)

        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { file ->
                addFileToTarGz(tarOutputStream, file.absolutePath, "$entryName/", directoriesToExclude)
            }
        }
        else {
            val inputStream = FileInputStream(fileOrDirectory)
            try {
                IOUtils.copy(inputStream, tarOutputStream)
            }
            finally {
                inputStream.close()
                tarOutputStream.closeArchiveEntry()
            }
        }
    }

    private fun getTempDirectory(): String {
        return System.getProperty(TempDirectory)
    }

    private fun buildTempScript(location: String, actualCommand: String): String {
        return """#!/usr/bin/env bash
. ~/.bash_profile
. ~/.bashrc
pushd $location
$actualCommand
"""
    }
}