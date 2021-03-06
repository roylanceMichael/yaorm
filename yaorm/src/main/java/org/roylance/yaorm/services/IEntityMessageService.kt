package org.roylance.yaorm.services

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel

interface IEntityMessageService: AutoCloseable {
    val entityService: IEntityService

    fun createEntireSchema(fileDescriptor: Descriptors.FileDescriptor):Boolean
    fun dropAndCreateEntireSchema(fileDescriptor: Descriptors.FileDescriptor):Boolean

    fun <T: Message> createEntireSchema(messageType: T): Boolean
    fun <T: Message> dropAndCreateEntireSchema(messageType: T): Boolean

    fun <T: Message> bulkInsert(messages: List<T>): Boolean
    fun <T: Message> merge(message: T): Boolean
    fun <T: Message> mergeTable(messages: List<T>, message: T): Boolean
    fun <T: Message> delete(message: T): Boolean

    fun <T: Message> get(messageType: T, id:String):T?
    fun <T: Message> getKeys(messageType: T):List<String>
    fun <T: Message> getKeysStream(messageType: T, streamer: IMessageStreamer)
    fun <T: Message> getMany(messageType: T, ids: List<String>): List<T>
    fun <T: Message> getMany(messageType: T, limit:Int=10000, offset:Int = 0):List<T>
    fun <T: Message> getManyStream(messageType: T, streamer: IMessageStreamer, limit:Int=10000, offset: Int=0)

    fun <T: Message> getManySingleLevel(messageType: T, limit:Int=10000, offset:Int = 0): List<T>
    fun <T: Message> getManySingleLevel(messageType: T, ids: List<String>): List<T>

    fun <T: Message> where(messageType: T,
                           whereClause:YaormModel.WhereClause):List<T>
    fun <T: Message> whereStream(messageType: T,
                           whereClause:YaormModel.WhereClause,
                           streamer: IMessageStreamer)

    fun <T: Message> getCount(messageType: T):Long

    fun <T: Message> getCustomSingleLevel(messageType: T, customSql: String): List<T>
    fun <T: Message> getCustomSingleLevelStream(messageType: T, customSql: String, stream: IMessageStreamer)

    fun getReport(): YaormModel.DatabaseExecutionReport
}
