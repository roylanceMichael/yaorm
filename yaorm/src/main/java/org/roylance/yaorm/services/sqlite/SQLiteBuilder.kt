package org.roylance.yaorm.services.sqlite

import com.google.protobuf.Descriptors
import org.roylance.common.service.IBase64Service
import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.*
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import java.util.*

class SQLiteBuilder : IEntityBuilder {
    override fun buildProtoContext(connectionInfo: YaormModel.ConnectionInfo,
                                   fileDescriptor: Descriptors.FileDescriptor,
                                   customIndexes: HashMap<String, YaormModel.Index>,
                                   base64Service: IBase64Service,
                                   emptyAsNull: Boolean): EntityProtoContext {
        return EntityProtoContext(fileDescriptor,
                buildProtoService(connectionInfo, emptyAsNull),
                customIndexes,
                base64Service)
    }

    override fun buildMessageService(connectionInfo: YaormModel.ConnectionInfo,
                                     customIndexes: HashMap<String, YaormModel.Index>,
                                     emptyAsNull: Boolean): IEntityMessageService {
        return EntityMessageService(buildProtoService(connectionInfo, emptyAsNull), customIndexes)
    }

    override fun buildProtoService(connectionInfo: YaormModel.ConnectionInfo, emptyAsNull: Boolean): IEntityService {
        val sourceConnection = SQLiteConnectionSourceFactory(connectionInfo.schema, connectionInfo.user, connectionInfo.password)
        val granularDatabaseService = JDBCGranularDatabaseService(sourceConnection, false)
        val sqliteGeneratorService = SQLiteGeneratorService(emptyAsNull = emptyAsNull)
        return EntityService(granularDatabaseService, sqliteGeneratorService)
    }
}