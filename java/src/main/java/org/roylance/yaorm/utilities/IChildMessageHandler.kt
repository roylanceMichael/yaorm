package org.roylance.yaorm.utilities

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel

interface IChildMessageHandler {
    fun handle(fieldKey: Descriptors.FieldDescriptor, idColumn: YaormModel.Column, builder: Message.Builder)
}