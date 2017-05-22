package org.roylance.yaorm.services.sqlserver

import org.roylance.common.service.IBuilder
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.ICommonTest

open class SQLServerBase: ICommonTest {
    override fun buildEntityService(schema: String?): IEntityService {
        ConnectionUtilities.getSQLServerConnectionInfo()
        val sourceConnection = SQLServerConnectionSourceFactory(
                ConnectionUtilities.sqlServerSqlHost!!,
                ConnectionUtilities.sqlServerSqlSchema!!,
                ConnectionUtilities.sqlServerSqlUserName!!,
                ConnectionUtilities.sqlServerSqlPassword!!)

        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false)

        return EntityService(granularDatabaseService, SQLServerGeneratorService())
    }

    override fun cleanup(schema: String?): IBuilder<Boolean> {
        return object: IBuilder<Boolean> {
            override fun build(): Boolean {
                return true
            }

        }
    }
}