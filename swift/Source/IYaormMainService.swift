// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
import Foundation

protocol IYaormMainService {
	func get_schemas(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void)
	func get_tables(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void)
	func get_table_definition(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void)
	func get_table_definitions(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void)
	func get_record_count(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void)
	func get_records(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void)
}