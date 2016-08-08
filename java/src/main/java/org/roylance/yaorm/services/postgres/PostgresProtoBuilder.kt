package org.roylance.yaorm.services.postgres

import com.google.protobuf.Descriptors
import org.roylance.common.service.IBase64Service
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.*
import java.util.*

class PostgresProtoBuilder: IEntityProtoBuilder {
    override fun buildMessageService(connectionInfo: YaormModel.ConnectionInfo,
                                     messageBuilder: IProtoGeneratedMessageBuilder,
                                     customIndexes: HashMap<String, YaormModel.Index>): IEntityMessageService {
        return EntityMessageService(messageBuilder, this.buildProtoService(connectionInfo), customIndexes)
    }

    override fun buildProtoContext(connectionInfo: YaormModel.ConnectionInfo,
                                   fileDescriptor: Descriptors.FileDescriptor,
                                   messageBuilder: IProtoGeneratedMessageBuilder,
                                   entityMessageName: String,
                                   customIndexes: HashMap<String, YaormModel.Index>,
                                   base64Service: IBase64Service): EntityProtoContext {
        return EntityProtoContext(
                fileDescriptor,
                messageBuilder,
                this.buildProtoService(connectionInfo),
                entityMessageName,
                customIndexes,
                base64Service)
    }

    override fun buildProtoService(connectionInfo: YaormModel.ConnectionInfo): IEntityProtoService {
        val sourceConnection = PostgresConnectionSourceFactory(
                connectionInfo.host,
                connectionInfo.port.toString(),
                connectionInfo.schema,
                connectionInfo.user,
                connectionInfo.password,
                false)

        val granularDatabaseService = JDBCGranularDatabaseProtoService(
                sourceConnection,
                false)
        val generatorService = PostgresGeneratorService()
        return EntityProtoService(granularDatabaseService, generatorService)
    }
}