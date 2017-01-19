package org.roylance.yaorm.services.csv

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.IFlatFileService
import org.roylance.yaorm.utilities.YaormUtils
import java.io.File
import java.io.FileWriter
import java.util.*

class CSVFlatFileService: IFlatFileService {
    override fun convertRecords(tableDefinition: YaormModel.TableDefinition, records: YaormModel.Records): File {
        val headers = tableDefinition.columnDefinitionsList.sortedBy { it.order }.map { it.name }.toTypedArray()
        val format = CSVFormat.DEFAULT.withHeader(*headers)
        val newFile = UUID.randomUUID().toString()
        val fileWriter = FileWriter(newFile)

        val csvFilePrinter = CSVPrinter(fileWriter, format)

        val columnsInOrder = tableDefinition.columnDefinitionsList.sortedBy { it.order }.toTypedArray()

        val columns = ArrayList<String>()
        records.recordsList.forEach { record ->
            columns.clear()

            columnsInOrder.forEach { columnDefinition ->
                val foundColumn = record.columnsList.firstOrNull { it.definition.name == columnDefinition.name }
                if (foundColumn != null) {
                    val actualObject = YaormUtils.getAnyObject(foundColumn)
                    if (actualObject == null) {
                        columns.add("")
                    }
                    else {
                        columns.add(actualObject.toString())
                    }
                }
                else {
                    columns.add("")
                }
            }
            csvFilePrinter.printRecord(*columns.toTypedArray())
        }
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun convertFile(tableDefinition: YaormModel.TableDefinition, file: File): YaormModel.Records {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}