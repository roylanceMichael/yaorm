// This file was auto-generated, but can be altered. It will not be overwritten.
package org.roylance.utilities

object ServiceLocator: IServiceLocator {
    override val protobufSerializerService: org.roylance.common.service.IProtoSerializerService
        get() = org.roylance.common.service.ProtoSerializerService(org.roylance.services.Base64Service())
    override val yaormMainService: org.roylance.services.IYaormMainService
        get() = throw UnsupportedOperationException()

}
