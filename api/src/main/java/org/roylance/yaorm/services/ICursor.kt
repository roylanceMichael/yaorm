package org.roylance.yaorm.services

import org.roylance.yaorm.YaormModel

interface ICursor {
    fun getRecords(): YaormModel.Records
    fun getRecordsStream(streamer: IStreamer)
}
