// This file was auto-generated, but can be altered. It will not be overwritten.
package org.roylance.yaorm.utilities

object ServiceLocator: IServiceLocator {
    override val protobufSerializerService: org.roylance.common.service.IProtoSerializerService
        get() = org.roylance.common.service.ProtoSerializerService(org.roylance.yaorm.services.Base64Service())
    override val yaormMainService: org.roylance.yaorm.services.IYaormMainService
        get() = throw UnsupportedOperationException()

}
