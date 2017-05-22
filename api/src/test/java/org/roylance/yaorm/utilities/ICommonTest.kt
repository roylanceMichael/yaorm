package org.roylance.yaorm.utilities

import org.roylance.common.service.IBuilder
import org.roylance.yaorm.services.IEntityService

interface ICommonTest {
    fun buildEntityService(schema: String? = null): IEntityService
    fun cleanup(schema: String? = null): IBuilder<Boolean>
}