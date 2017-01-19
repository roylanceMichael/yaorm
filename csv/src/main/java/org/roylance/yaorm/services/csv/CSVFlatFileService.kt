package org.roylance.yaorm.services.csv

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.IFlatFileService
import org.roylance.yaorm.utilities.YaormUtils
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.util.*

object CSVFlatFileService: IFlatFileService {
    override fun convertRecords(tableDefinition: YaormModel.TableDefinition, records: YaormModel.Records): File {
        val newFile = UUID.randomUUID().toString()

        val headers = tableDefinition.columnDefinitionsList.sortedBy { it.order }.map { it.name }.toTypedArray()
        val format = CSVFormat.DEFAULT.withHeader(*headers)

        val fileWriter = FileWriter(newFile)
        val csvFilePrinter = CSVPrinter(fileWriter, format)
        try {
            val columnsInOrder = tableDefinition.columnDefinitionsList.sortedBy { it.order }.toTypedArray()

            val columns = ArrayList<String>()
            records.recordsList.forEach { record ->
                columns.clear()

                columnsInOrder.forEach { columnDefinition ->
                    val foundColumn = record.columnsList.firstOrNull { it.definition.name == columnDefinition.name }
                    var anyObject = ""
                    if (foundColumn != null) {
                        val actualObject = YaormUtils.getAnyObject(foundColumn)
                        if (actualObject != null) {
                            anyObject = actualObject.toString()
                        }
                    }

                    columns.add(anyObject)
                }
                csvFilePrinter.printRecord(*columns.toTypedArray())
            }

            return File(newFile)
        }
        finally {
            fileWriter.flush()
            fileWriter.close()
            csvFilePrinter.close()
        }
    }

    override fun convertFile(tableDefinition: YaormModel.TableDefinition, file: File): YaormModel.Records {
        val format = CSVFormat.DEFAULT
        val parser = CSVParser.parse(file, StandardCharsets.UTF_8, format)

        val records = YaormModel.Records.newBuilder()
        val newRecord = YaormModel.Record.newBuilder()

        val columnInOrder = tableDefinition.columnDefinitionsList.sortedBy { it.order }.toTypedArray()

        var skippedHeaderRecord = false
        for (csvRecord in parser) {
            if (!skippedHeaderRecord) {
                skippedHeaderRecord = true
                continue
            }
            newRecord.clear()
            var i = 0
            csvRecord.forEach { csvColumn ->
                val newColumn = YaormUtils.buildColumn(csvColumn, columnInOrder[i])
                newRecord.addColumns(newColumn)
                i++
            }
            records.addRecords(newRecord)
        }

        return records.build()
    }

}