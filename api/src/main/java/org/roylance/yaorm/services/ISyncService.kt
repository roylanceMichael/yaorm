package org.roylance.yaorm.services

import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel

interface ISyncService {
    fun <T: Message> syncTable(fromMessageService: IEntityMessageService,
                               toMessageService: IEntityMessageService,
                               messageType: T,
                               dropTable: Boolean = false,
                               bulkInsertSize: Int = 500): Boolean

    fun syncTable(fromService: IEntityService,
                  toService: IEntityService,
                  tableDefinition: YaormModel.TableDefinition,
                  dropTable: Boolean = false,
                  bulkInsertSize: Int = 500): Boolean
}