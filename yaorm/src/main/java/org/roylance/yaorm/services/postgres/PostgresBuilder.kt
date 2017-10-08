package org.roylance.yaorm.services.postgres

import com.google.protobuf.Descriptors
import org.roylance.common.service.IBase64Service
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.*
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import java.util.*

class PostgresBuilder : IEntityBuilder {
    override fun buildMessageService(connectionInfo: YaormModel.ConnectionInfo,
                                     customIndexes: HashMap<String, YaormModel.Index>,
                                     emptyAsNull: Boolean): IEntityMessageService {
        return EntityMessageService(this.buildProtoService(connectionInfo, emptyAsNull), customIndexes)
    }

    override fun buildProtoContext(connectionInfo: YaormModel.ConnectionInfo,
                                   fileDescriptor: Descriptors.FileDescriptor,
                                   customIndexes: HashMap<String, YaormModel.Index>,
                                   base64Service: IBase64Service,
                                   emptyAsNull: Boolean): EntityProtoContext {
        return EntityProtoContext(
                fileDescriptor,
                this.buildProtoService(connectionInfo, emptyAsNull),
                customIndexes,
                base64Service)
    }

    override fun buildProtoService(connectionInfo: YaormModel.ConnectionInfo, emptyAsNull: Boolean): IEntityService {
        val sourceConnection = PostgresConnectionSourceFactory(
                connectionInfo.host,
                connectionInfo.port.toString(),
                connectionInfo.schema,
                connectionInfo.user,
                connectionInfo.password,
                false)

        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false)
        val generatorService = PostgresGeneratorService(emptyAsNull = emptyAsNull)
        return EntityService(granularDatabaseService, generatorService)
    }
}