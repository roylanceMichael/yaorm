// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.services;

class YaormMainServiceJNI: IYaormMainService {
    private val bridge = YaormMainJNIBridge()
	override fun get_schemas(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
		val bytes = request.toByteArray()
		val result = this.bridge.get_schemas(bytes)
		return org.roylance.yaorm.YaormModel.UIYaormResponse.parseFrom(result)
	}
	override fun get_tables(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
		val bytes = request.toByteArray()
		val result = this.bridge.get_tables(bytes)
		return org.roylance.yaorm.YaormModel.UIYaormResponse.parseFrom(result)
	}
	override fun get_table_definition(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
		val bytes = request.toByteArray()
		val result = this.bridge.get_table_definition(bytes)
		return org.roylance.yaorm.YaormModel.UIYaormResponse.parseFrom(result)
	}
	override fun get_table_definitions(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
		val bytes = request.toByteArray()
		val result = this.bridge.get_table_definitions(bytes)
		return org.roylance.yaorm.YaormModel.UIYaormResponse.parseFrom(result)
	}
	override fun get_record_count(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
		val bytes = request.toByteArray()
		val result = this.bridge.get_record_count(bytes)
		return org.roylance.yaorm.YaormModel.UIYaormResponse.parseFrom(result)
	}
	override fun get_records(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse {
		val bytes = request.toByteArray()
		val result = this.bridge.get_records(bytes)
		return org.roylance.yaorm.YaormModel.UIYaormResponse.parseFrom(result)
	}
}
