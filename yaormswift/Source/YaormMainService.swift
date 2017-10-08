// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
import Foundation
import Alamofire
import SwiftProtobuf

public class YaormMainService: IYaormMainService {
    let baseUrl: String
    public init(baseUrl: String) {
        self.baseUrl = baseUrl
    }
	public func get_schemas(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void) {

            do {
                let serializedRequest = try request.serializedData()
                var urlRequest = URLRequest(url: URL(string: self.baseUrl + "/rest/yaormmain/get-schemas")!)
                urlRequest.httpMethod = HTTPMethod.post.rawValue
                urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
                urlRequest.httpBody = serializedRequest.base64EncodedData()

                Alamofire.request(urlRequest)
                    .response { alamoResponse in
                        let base64String = String(data: alamoResponse.data!, encoding: String.Encoding.utf8)
                        let decodedData = Data(base64Encoded: base64String!)!
                        do {
                            let actualResponse = try Org_Roylance_Yaorm_UIYaormResponse(serializedData: decodedData)
                            onSuccess(actualResponse)
                        }
                        catch {
                            onError("\(error)")
                        }
                    }
                }
            catch {
                onError("\(error)")
            }
	}
	public func get_tables(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void) {

            do {
                let serializedRequest = try request.serializedData()
                var urlRequest = URLRequest(url: URL(string: self.baseUrl + "/rest/yaormmain/get-tables")!)
                urlRequest.httpMethod = HTTPMethod.post.rawValue
                urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
                urlRequest.httpBody = serializedRequest.base64EncodedData()

                Alamofire.request(urlRequest)
                    .response { alamoResponse in
                        let base64String = String(data: alamoResponse.data!, encoding: String.Encoding.utf8)
                        let decodedData = Data(base64Encoded: base64String!)!
                        do {
                            let actualResponse = try Org_Roylance_Yaorm_UIYaormResponse(serializedData: decodedData)
                            onSuccess(actualResponse)
                        }
                        catch {
                            onError("\(error)")
                        }
                    }
                }
            catch {
                onError("\(error)")
            }
	}
	public func get_table_definition(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void) {

            do {
                let serializedRequest = try request.serializedData()
                var urlRequest = URLRequest(url: URL(string: self.baseUrl + "/rest/yaormmain/get-table-definition")!)
                urlRequest.httpMethod = HTTPMethod.post.rawValue
                urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
                urlRequest.httpBody = serializedRequest.base64EncodedData()

                Alamofire.request(urlRequest)
                    .response { alamoResponse in
                        let base64String = String(data: alamoResponse.data!, encoding: String.Encoding.utf8)
                        let decodedData = Data(base64Encoded: base64String!)!
                        do {
                            let actualResponse = try Org_Roylance_Yaorm_UIYaormResponse(serializedData: decodedData)
                            onSuccess(actualResponse)
                        }
                        catch {
                            onError("\(error)")
                        }
                    }
                }
            catch {
                onError("\(error)")
            }
	}
	public func get_table_definitions(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void) {

            do {
                let serializedRequest = try request.serializedData()
                var urlRequest = URLRequest(url: URL(string: self.baseUrl + "/rest/yaormmain/get-table-definitions")!)
                urlRequest.httpMethod = HTTPMethod.post.rawValue
                urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
                urlRequest.httpBody = serializedRequest.base64EncodedData()

                Alamofire.request(urlRequest)
                    .response { alamoResponse in
                        let base64String = String(data: alamoResponse.data!, encoding: String.Encoding.utf8)
                        let decodedData = Data(base64Encoded: base64String!)!
                        do {
                            let actualResponse = try Org_Roylance_Yaorm_UIYaormResponse(serializedData: decodedData)
                            onSuccess(actualResponse)
                        }
                        catch {
                            onError("\(error)")
                        }
                    }
                }
            catch {
                onError("\(error)")
            }
	}
	public func get_record_count(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void) {

            do {
                let serializedRequest = try request.serializedData()
                var urlRequest = URLRequest(url: URL(string: self.baseUrl + "/rest/yaormmain/get-record-count")!)
                urlRequest.httpMethod = HTTPMethod.post.rawValue
                urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
                urlRequest.httpBody = serializedRequest.base64EncodedData()

                Alamofire.request(urlRequest)
                    .response { alamoResponse in
                        let base64String = String(data: alamoResponse.data!, encoding: String.Encoding.utf8)
                        let decodedData = Data(base64Encoded: base64String!)!
                        do {
                            let actualResponse = try Org_Roylance_Yaorm_UIYaormResponse(serializedData: decodedData)
                            onSuccess(actualResponse)
                        }
                        catch {
                            onError("\(error)")
                        }
                    }
                }
            catch {
                onError("\(error)")
            }
	}
	public func get_records(request: Org_Roylance_Yaorm_UIYaormRequest, onSuccess: @escaping (_ response: Org_Roylance_Yaorm_UIYaormResponse) -> Void, onError: @escaping (_ response: String) -> Void) {

            do {
                let serializedRequest = try request.serializedData()
                var urlRequest = URLRequest(url: URL(string: self.baseUrl + "/rest/yaormmain/get-records")!)
                urlRequest.httpMethod = HTTPMethod.post.rawValue
                urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
                urlRequest.httpBody = serializedRequest.base64EncodedData()

                Alamofire.request(urlRequest)
                    .response { alamoResponse in
                        let base64String = String(data: alamoResponse.data!, encoding: String.Encoding.utf8)
                        let decodedData = Data(base64Encoded: base64String!)!
                        do {
                            let actualResponse = try Org_Roylance_Yaorm_UIYaormResponse(serializedData: decodedData)
                            onSuccess(actualResponse)
                        }
                        catch {
                            onError("\(error)")
                        }
                    }
                }
            catch {
                onError("\(error)")
            }
	}
}