package org.roylance.yaorm.services

import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel

object SyncService: ISyncService {
    override fun <T : Message> syncTable(fromMessageService: IEntityMessageService,
                                         toMessageService: IEntityMessageService,
                                         messageType: T,
                                         dropTable: Boolean,
                                         bulkInsertSize: Int): Boolean {
        if (dropTable) {
            toMessageService.dropAndCreateEntireSchema(messageType)
        }
        else {
            toMessageService.createEntireSchema(messageType)
        }

        val totalCount = fromMessageService.getCount(messageType)
        var runningOffset = 0
        while (runningOffset < totalCount) {
            val records = fromMessageService.getMany(messageType, bulkInsertSize, runningOffset)
            toMessageService.bulkInsert(records)
            runningOffset += bulkInsertSize
        }

        val records = fromMessageService.getMany(messageType, bulkInsertSize, runningOffset)
        toMessageService.bulkInsert(records)

        return true
    }

    override fun syncTable(fromService: IEntityService,
                           toService: IEntityService,
                           tableDefinition: YaormModel.TableDefinition,
                           dropTable: Boolean,
                           bulkInsertSize: Int): Boolean {
        if (dropTable) {
            toService.dropTable(tableDefinition)
        }

        toService.createTable(tableDefinition)

        val totalCount = fromService.getCount(tableDefinition)
        var runningOffset = 0
        while (runningOffset < totalCount) {
            val records = fromService.getMany(tableDefinition, bulkInsertSize, runningOffset)
            toService.bulkInsert(records, tableDefinition)

            runningOffset += bulkInsertSize
        }

        val records = fromService.getMany(tableDefinition, bulkInsertSize, runningOffset)
        toService.bulkInsert(records, tableDefinition)

        return true
    }

}