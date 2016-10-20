package org.roylance.yaorm.models.entity

import com.google.protobuf.Descriptors
import com.google.protobuf.Message

class CachingObject(
        val messageType: Message,
        val fieldKey: Descriptors.FieldDescriptor,
        val mainId: String,
        val id: MutableList<String>)