package org.roylance.yaorm.services.proto

import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel

interface IEntityMessageService {
    fun <T: Message> createEntireSchema(message:T): Boolean
    fun <T: Message> dropAndRecreateEntireSchema(message:T): Boolean

    fun <T: Message> merge(sourceOfTruthMessage:T): Boolean
    fun <T: Message> delete(sourceOfTruthMessage:T): Boolean

    fun <T: Message> get(messageType:T, id:String):T
    fun <T: Message> getMany(messageType:T, maxAmount:Int=10000):List<T>
    fun <T: Message> getManyStream(messageType: T, streamer: IMessageStreamer)

    fun <T: Message> where(messageType: T,
                           whereClause:YaormModel.WhereClause):List<T>
    fun <T: Message> whereStream(messageType: T,
                           whereClause:YaormModel.WhereClause,
                           streamer: IMessageStreamer)

    fun <T: Message> getCount(messageType: T):Long
}
