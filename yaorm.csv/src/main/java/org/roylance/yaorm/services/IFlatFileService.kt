package org.roylance.yaorm.services

import org.roylance.yaorm.YaormModel
import java.io.File

interface IFlatFileService {
    fun convertRecords(tableDefinition: YaormModel.TableDefinition, records: YaormModel.Records): File
    fun convertFile(tableDefinition : YaormModel.TableDefinition, file: File): YaormModel.Records
}