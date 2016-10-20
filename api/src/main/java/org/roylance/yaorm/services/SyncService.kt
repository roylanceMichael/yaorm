package org.roylance.yaorm.services

import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.proto.IEntityMessageService
import org.roylance.yaorm.services.proto.IEntityProtoService

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

    override fun syncTable(fromProtoService: IEntityProtoService,
                           toProtoService: IEntityProtoService,
                           tableDefinition: YaormModel.TableDefinition,
                           dropTable: Boolean,
                           bulkInsertSize: Int): Boolean {
        if (dropTable) {
            toProtoService.dropTable(tableDefinition)
        }

        toProtoService.createTable(tableDefinition)

        val totalCount = fromProtoService.getCount(tableDefinition)
        var runningOffset = 0
        while (runningOffset < totalCount) {
            val records = fromProtoService.getMany(tableDefinition, bulkInsertSize, runningOffset)
            toProtoService.bulkInsert(records, tableDefinition)

            runningOffset += bulkInsertSize
        }

        val records = fromProtoService.getMany(tableDefinition, bulkInsertSize, runningOffset)
        toProtoService.bulkInsert(records, tableDefinition)

        return true
    }

}