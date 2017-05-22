package org.roylance.yaorm.services.sqlite

import org.roylance.common.service.IBuilder
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.ICommonTest
import java.io.File

open class SQLiteBase: ICommonTest {
    override fun buildEntityService(schema: String?): IEntityService {
        val sourceConnection = SQLiteConnectionSourceFactory(schema!!)
        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false)
        val sqliteGeneratorService = SQLiteGeneratorService()
        val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
        return entityService
    }

    override fun cleanup(schema: String?): IBuilder<Boolean> {
        return object : IBuilder<Boolean> {
            override fun build(): Boolean {
                File(schema).deleteOnExit()
                return true
            }
        }
    }
}