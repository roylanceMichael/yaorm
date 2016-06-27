package org.roylance.yaorm.services.proto

import org.roylance.yaorm.models.YaormModel

interface IProtoStreamer {
    fun stream(record:YaormModel.Record)
}
