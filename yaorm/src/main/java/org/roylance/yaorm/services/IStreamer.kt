package org.roylance.yaorm.services

import org.roylance.yaorm.YaormModel

interface IStreamer {
    fun stream(record:YaormModel.Record)
}
