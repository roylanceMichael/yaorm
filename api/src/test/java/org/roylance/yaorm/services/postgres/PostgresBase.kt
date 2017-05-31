package org.roylance.yaorm.services.postgres

import org.roylance.common.service.IBuilder
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.ICommonTest

open class PostgresBase: ICommonTest {
    override fun buildEntityService(schema: String?): IEntityService {
        ConnectionUtilities.getPostgresConnectionInfo()
        val sourceConnection = PostgresConnectionSourceFactory(
                ConnectionUtilities.postgresHost!!,
                ConnectionUtilities.postgresPort!!,
                ConnectionUtilities.postgresDatabase!!,
                ConnectionUtilities.postgresUserName!!,
                ConnectionUtilities.postgresPassword!!,
                false)

        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false,
                true)
        val generatorService = PostgresGeneratorService()
        val entityService = EntityService(granularDatabaseService, generatorService)
        return entityService
    }

    override fun cleanup(schema: String?): IBuilder<Boolean> {
        return object : IBuilder<Boolean> {
            override fun build(): Boolean {
                return true
            }

        }
    }
}