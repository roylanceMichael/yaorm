package org.roylance.yaorm.services.proto

import org.roylance.yaorm.YaormModel

interface IProtoStreamer {
    fun stream(record:YaormModel.Record)
}
