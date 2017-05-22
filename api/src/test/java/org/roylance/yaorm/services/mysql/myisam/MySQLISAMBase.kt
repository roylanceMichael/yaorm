package org.roylance.yaorm.services.mysql.myisam

import org.roylance.common.service.IBuilder
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.mysql.MySQLConnectionSourceFactory
import org.roylance.yaorm.services.mysql.MySQLGeneratorService
import org.roylance.yaorm.utilities.ConnectionUtilities
import org.roylance.yaorm.utilities.ICommonTest

open class MySQLISAMBase: ICommonTest {
    override fun cleanup(schema: String?): IBuilder<Boolean> {
        return MySQLCleanup()
    }

    override fun buildEntityService(schema: String?): IEntityService {
        ConnectionUtilities.getMySQLConnectionInfo()
        val sourceConnection = MySQLConnectionSourceFactory(
                ConnectionUtilities.mysqlHost!!,
                ConnectionUtilities.mysqlSchema!!,
                ConnectionUtilities.mysqlUserName!!,
                ConnectionUtilities.mysqlPassword!!)

        val granularDatabaseService = JDBCGranularDatabaseService(
                sourceConnection,
                false)
        val mySqlGeneratorService = MySQLGeneratorService(schemaName = sourceConnection.schema, useMyISAM = true)
        return EntityService(granularDatabaseService, mySqlGeneratorService)
    }

    private class MySQLCleanup: IBuilder<Boolean> {
        override fun build(): Boolean {
            ConnectionUtilities.dropMySQLSchema()
            return true
        }
    }
}