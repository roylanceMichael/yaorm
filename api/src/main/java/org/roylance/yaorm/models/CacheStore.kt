package org.roylance.yaorm.models

import com.google.protobuf.Message
import org.roylance.yaorm.utilities.ProtobufUtils
import java.util.*

class CacheStore {
    private val types = HashMap<String, HashMap<String, Message.Builder>>()

    fun seenObject(type: Message, key: String): Boolean {
        if (types.containsKey(type.descriptorForType.name) &&
                types[type.descriptorForType.name]!!.containsKey(key)) {
            return true
        }
        return false
    }

    fun getObject(type: Message, key: String): Message.Builder {
        if (types.containsKey(type.descriptorForType.name) &&
            types[type.descriptorForType.name]!!.containsKey(key)) {
            return types[type.descriptorForType.name]!![key]!!
        }

        if (!types.containsKey(type.descriptorForType.name)) {
            types[type.descriptorForType.name] = HashMap()
        }

        val newBuilder =  type.toBuilder()
        ProtobufUtils.setIdForMessage(newBuilder, key)
        types[type.descriptorForType.name]!![key] = newBuilder

        return newBuilder
    }

    fun saveObject(actualObject: Message.Builder, key: String) {
        if (types.containsKey(actualObject.descriptorForType.name) &&
                types[actualObject.descriptorForType.name]!!.containsKey(key)) {
            val existingObject = types[actualObject.descriptorForType.name]!![key]!!
            existingObject.mergeFrom(actualObject.build())
            return
        }
        else if (!types.containsKey(actualObject.descriptorForType.name)) {
            types[actualObject.descriptorForType.name] = HashMap()
        }

        types[actualObject.descriptorForType.name]!![key] = actualObject
    }
}