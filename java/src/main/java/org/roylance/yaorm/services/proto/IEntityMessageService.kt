package org.roylance.yaorm.services.proto

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel

interface IEntityMessageService {
    fun createEntireSchema(fileDescriptor: Descriptors.FileDescriptor):Boolean
    fun dropAndCreateEntireSchema(fileDescriptor: Descriptors.FileDescriptor):Boolean

    fun <T: Message> createEntireSchema(messageType:T): Boolean
    fun <T: Message> dropAndCreateEntireSchema(messageType:T): Boolean

    fun <T: Message> merge(message:T): Boolean
    fun <T: Message> delete(message:T): Boolean

    fun <T: Message> get(messageType:T, id:String):T?
    fun <T: Message> getKeys(messageType:T):List<String>
    fun <T: Message> getKeysStream(messageType:T, streamer: IMessageStreamer)
    fun <T: Message> getMany(messageType:T, limit:Int=10000, offset:Int = 0):List<T>
    fun <T: Message> getManyStream(messageType: T, streamer: IMessageStreamer, limit:Int=10000, offset: Int  = 0)

    fun <T: Message> where(messageType: T,
                           whereClause:YaormModel.WhereClause):List<T>
    fun <T: Message> whereStream(messageType: T,
                           whereClause:YaormModel.WhereClause,
                           streamer: IMessageStreamer)

    fun <T: Message> getCount(messageType: T):Long
}
