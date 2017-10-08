package org.roylance.yaorm.utilities

import org.apache.commons.codec.binary.Base64
import org.roylance.common.service.IBase64Service

class TestBase64Service :IBase64Service {
    private val base64 = Base64()
    override fun deserialize(string64: String): ByteArray {
        return this.base64.decode(string64)
    }

    override fun serialize(bytes: ByteArray): String {
        return this.base64.encodeAsString(bytes)
    }
}