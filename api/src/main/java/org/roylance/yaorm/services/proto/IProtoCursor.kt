package org.roylance.yaorm.services.proto

import org.roylance.yaorm.YaormModel

interface IProtoCursor {
    fun getRecords(): YaormModel.Records
    fun getRecordsStream(streamer: IProtoStreamer)
}
