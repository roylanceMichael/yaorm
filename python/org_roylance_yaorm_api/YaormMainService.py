import base64
import requests
import yaorm_model_pb2


class YaormMainService(object):
    def __init__(self, base_url):
        self.base_url = base_url

	def get_schemas(request):
		base64_request = base64.b64encode(request.SerializeToString())
		response_call = requests.post(self.base_url + '/rest/yaormmain/get-schemas', data = base64_request)
		response = yaorm_model_pb2.UIYaormResponse()
		response.ParseFromString(base64.b64decode(response_call.text))
		return response

	def get_tables(request):
		base64_request = base64.b64encode(request.SerializeToString())
		response_call = requests.post(self.base_url + '/rest/yaormmain/get-tables', data = base64_request)
		response = yaorm_model_pb2.UIYaormResponse()
		response.ParseFromString(base64.b64decode(response_call.text))
		return response

	def get_table_definition(request):
		base64_request = base64.b64encode(request.SerializeToString())
		response_call = requests.post(self.base_url + '/rest/yaormmain/get-table-definition', data = base64_request)
		response = yaorm_model_pb2.UIYaormResponse()
		response.ParseFromString(base64.b64decode(response_call.text))
		return response

	def get_table_definitions(request):
		base64_request = base64.b64encode(request.SerializeToString())
		response_call = requests.post(self.base_url + '/rest/yaormmain/get-table-definitions', data = base64_request)
		response = yaorm_model_pb2.UIYaormResponse()
		response.ParseFromString(base64.b64decode(response_call.text))
		return response

	def get_record_count(request):
		base64_request = base64.b64encode(request.SerializeToString())
		response_call = requests.post(self.base_url + '/rest/yaormmain/get-record-count', data = base64_request)
		response = yaorm_model_pb2.UIYaormResponse()
		response.ParseFromString(base64.b64decode(response_call.text))
		return response

	def get_records(request):
		base64_request = base64.b64encode(request.SerializeToString())
		response_call = requests.post(self.base_url + '/rest/yaormmain/get-records', data = base64_request)
		response = yaorm_model_pb2.UIYaormResponse()
		response.ParseFromString(base64.b64decode(response_call.text))
		return response


