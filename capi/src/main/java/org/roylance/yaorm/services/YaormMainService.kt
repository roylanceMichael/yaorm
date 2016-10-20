// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.services

import org.roylance.common.service.IProtoSerializerService

class YaormMainService(
        private val restYaormMain: IYaormMainRest,
        private val protoSerializer: IProtoSerializerService): IYaormMainService {

    override fun get_schemas(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
        val base64request = protoSerializer.serializeToBase64(request)
        val responseCall = restYaormMain.get_schemas(base64request)
        val response = responseCall.execute()
        return protoSerializer.deserializeFromBase64(response.body(),
                org.roylance.yaorm.YaormModel.UIYaormResponse.getDefaultInstance())
    }

    override fun get_tables(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
        val base64request = protoSerializer.serializeToBase64(request)
        val responseCall = restYaormMain.get_tables(base64request)
        val response = responseCall.execute()
        return protoSerializer.deserializeFromBase64(response.body(),
                org.roylance.yaorm.YaormModel.UIYaormResponse.getDefaultInstance())
    }

    override fun get_table_definition(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
        val base64request = protoSerializer.serializeToBase64(request)
        val responseCall = restYaormMain.get_table_definition(base64request)
        val response = responseCall.execute()
        return protoSerializer.deserializeFromBase64(response.body(),
                org.roylance.yaorm.YaormModel.UIYaormResponse.getDefaultInstance())
    }

    override fun get_table_definitions(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
        val base64request = protoSerializer.serializeToBase64(request)
        val responseCall = restYaormMain.get_table_definitions(base64request)
        val response = responseCall.execute()
        return protoSerializer.deserializeFromBase64(response.body(),
                org.roylance.yaorm.YaormModel.UIYaormResponse.getDefaultInstance())
    }

    override fun get_record_count(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
        val base64request = protoSerializer.serializeToBase64(request)
        val responseCall = restYaormMain.get_record_count(base64request)
        val response = responseCall.execute()
        return protoSerializer.deserializeFromBase64(response.body(),
                org.roylance.yaorm.YaormModel.UIYaormResponse.getDefaultInstance())
    }

    override fun get_records(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
        val base64request = protoSerializer.serializeToBase64(request)
        val responseCall = restYaormMain.get_records(base64request)
        val response = responseCall.execute()
        return protoSerializer.deserializeFromBase64(response.body(),
                org.roylance.yaorm.YaormModel.UIYaormResponse.getDefaultInstance())
    }
}