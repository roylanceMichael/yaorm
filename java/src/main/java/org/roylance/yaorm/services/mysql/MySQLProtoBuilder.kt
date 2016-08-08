package org.roylance.yaorm.services.mysql

import com.google.protobuf.Descriptors
import org.roylance.common.service.IBase64Service
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseProtoService
import org.roylance.yaorm.services.proto.*
import java.util.*

class MySQLProtoBuilder: IEntityProtoBuilder {
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
                base64Service
        )
    }

    override fun buildMessageService(connectionInfo: YaormModel.ConnectionInfo,
                                     messageBuilder: IProtoGeneratedMessageBuilder,
                                     customIndexes: HashMap<String, YaormModel.Index>): IEntityMessageService {
        return EntityMessageService(messageBuilder, this.buildProtoService(connectionInfo), customIndexes)
    }

    override fun buildProtoService(connectionInfo: YaormModel.ConnectionInfo): IEntityProtoService {
        val sourceConnection = MySQLConnectionSourceFactory(
                connectionInfo.host,
                connectionInfo.schema,
                connectionInfo.user,
                connectionInfo.password,
                connectionInfo.shouldCreateSchema)

        val granularDatabaseService = JDBCGranularDatabaseProtoService(
                sourceConnection,
                false)
        val mySqlGeneratorService = MySQLGeneratorService(sourceConnection.schema)

        return EntityProtoService(granularDatabaseService, mySqlGeneratorService)
    }
}