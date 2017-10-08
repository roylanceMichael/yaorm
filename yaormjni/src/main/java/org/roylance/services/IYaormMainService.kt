// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.services

interface IYaormMainService {
	fun get_schemas(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse
	fun get_tables(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse
	fun get_table_definition(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse
	fun get_table_definitions(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse
	fun get_record_count(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse
	fun get_records(request: org.roylance.yaorm.YaormModel.UIYaormRequest): org.roylance.yaorm.YaormModel.UIYaormResponse
}