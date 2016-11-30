package org.roylance.yaorm.services

import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.proto.IEntityMessageService
import org.roylance.yaorm.services.proto.IEntityProtoService

interface ISyncService {
    fun <T: Message> syncTable(fromMessageService: IEntityMessageService,
                               toMessageService: IEntityMessageService,
                               messageType: T,
                               dropTable: Boolean = false,
                               bulkInsertSize: Int = 500): Boolean

    fun syncTable(fromProtoService: IEntityProtoService,
                  toProtoService: IEntityProtoService,
                  tableDefinition: YaormModel.TableDefinition,
                  dropTable: Boolean = false,
                  bulkInsertSize: Int = 500): Boolean
}