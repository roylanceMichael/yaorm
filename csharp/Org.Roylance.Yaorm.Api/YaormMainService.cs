// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
using System.Threading.Tasks;
using Google.Protobuf;

namespace Org.Roylance.Yaorm.Api
{
    public class YaormMainService: IYaormMainService
    {
        private readonly IHttpExecute httpExecute;
        public YaormMainService(IHttpExecute httpExecute)
        {
            this.httpExecute = httpExecute;
        }

        public async Task<Org.Roylance.Yaorm.UIYaormResponse> get_schemas(Org.Roylance.Yaorm.UIYaormRequest request)
        {
            var base64request = System.Convert.ToBase64String(request.ToByteArray());
            var responseCall = await this.httpExecute.PostAsync("/rest/yaormmain/get-schemas", base64request);
            var bytes = System.Convert.FromBase64String(responseCall);
            return Org.Roylance.Yaorm.UIYaormResponse.Parser.ParseFrom(bytes);
        }

        public async Task<Org.Roylance.Yaorm.UIYaormResponse> get_tables(Org.Roylance.Yaorm.UIYaormRequest request)
        {
            var base64request = System.Convert.ToBase64String(request.ToByteArray());
            var responseCall = await this.httpExecute.PostAsync("/rest/yaormmain/get-tables", base64request);
            var bytes = System.Convert.FromBase64String(responseCall);
            return Org.Roylance.Yaorm.UIYaormResponse.Parser.ParseFrom(bytes);
        }

        public async Task<Org.Roylance.Yaorm.UIYaormResponse> get_table_definition(Org.Roylance.Yaorm.UIYaormRequest request)
        {
            var base64request = System.Convert.ToBase64String(request.ToByteArray());
            var responseCall = await this.httpExecute.PostAsync("/rest/yaormmain/get-table-definition", base64request);
            var bytes = System.Convert.FromBase64String(responseCall);
            return Org.Roylance.Yaorm.UIYaormResponse.Parser.ParseFrom(bytes);
        }

        public async Task<Org.Roylance.Yaorm.UIYaormResponse> get_table_definitions(Org.Roylance.Yaorm.UIYaormRequest request)
        {
            var base64request = System.Convert.ToBase64String(request.ToByteArray());
            var responseCall = await this.httpExecute.PostAsync("/rest/yaormmain/get-table-definitions", base64request);
            var bytes = System.Convert.FromBase64String(responseCall);
            return Org.Roylance.Yaorm.UIYaormResponse.Parser.ParseFrom(bytes);
        }

        public async Task<Org.Roylance.Yaorm.UIYaormResponse> get_record_count(Org.Roylance.Yaorm.UIYaormRequest request)
        {
            var base64request = System.Convert.ToBase64String(request.ToByteArray());
            var responseCall = await this.httpExecute.PostAsync("/rest/yaormmain/get-record-count", base64request);
            var bytes = System.Convert.FromBase64String(responseCall);
            return Org.Roylance.Yaorm.UIYaormResponse.Parser.ParseFrom(bytes);
        }

        public async Task<Org.Roylance.Yaorm.UIYaormResponse> get_records(Org.Roylance.Yaorm.UIYaormRequest request)
        {
            var base64request = System.Convert.ToBase64String(request.ToByteArray());
            var responseCall = await this.httpExecute.PostAsync("/rest/yaormmain/get-records", base64request);
            var bytes = System.Convert.FromBase64String(responseCall);
            return Org.Roylance.Yaorm.UIYaormResponse.Parser.ParseFrom(bytes);
        }
	}
}