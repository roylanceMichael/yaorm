package org.roylance.yaorm.services.proto

import com.google.protobuf.Descriptors
import org.roylance.common.service.IBase64Service
import org.roylance.yaorm.YaormModel
import java.util.*

interface IEntityProtoBuilder {
    fun buildProtoContext(connectionInfo: YaormModel.ConnectionInfo,
                          fileDescriptor: Descriptors.FileDescriptor,
                          messageBuilder: IProtoGeneratedMessageBuilder,
                          customIndexes: HashMap<String, YaormModel.Index>,
                          base64Service: IBase64Service,
                          emptyAsNull: Boolean = false): EntityProtoContext

    fun buildMessageService(connectionInfo: YaormModel.ConnectionInfo,
                            messageBuilder: IProtoGeneratedMessageBuilder,
                            customIndexes: HashMap<String, YaormModel.Index>,
                            emptyAsNull: Boolean = false): IEntityMessageService

    fun buildProtoService(connectionInfo: YaormModel.ConnectionInfo, emptyAsNull: Boolean = false): IEntityProtoService
}