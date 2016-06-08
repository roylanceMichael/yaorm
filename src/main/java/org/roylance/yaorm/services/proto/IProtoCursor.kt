package org.roylance.yaorm.services.proto

import org.roylance.yaorm.models.YaormModel

interface IProtoCursor {
    fun getRecords(): YaormModel.Records
    fun getRecordsStream(streamer: IProtoStreamer)
}
