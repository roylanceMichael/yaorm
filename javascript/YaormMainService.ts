// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
import {IYaormMainService} from "./IYaormMainService";
import {IHttpExecute} from "./IHttpExecute";
import ProtoBufBuilder = org.roylance.yaorm.ProtoBufBuilder;

export class YaormMainService implements IYaormMainService {
    httpExecute:IHttpExecute;
    modelFactory:ProtoBufBuilder;

    constructor(httpExecute:IHttpExecute,
                modelFactory:ProtoBufBuilder) {
        this.httpExecute = httpExecute;
        this.modelFactory = modelFactory;
    }
	get_schemas(request: org.roylance.yaorm.UIYaormRequest, onSuccess:(response: org.roylance.yaorm.UIYaormResponse)=>void, onError:(response:any)=>void) {
            const self = this;
            this.httpExecute.performPost("/rest/yaormmain/get-schemas",
                    request.toBase64(),
                    function(result:string) {
                        onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
                    },
                    onError);
        }
	get_tables(request: org.roylance.yaorm.UIYaormRequest, onSuccess:(response: org.roylance.yaorm.UIYaormResponse)=>void, onError:(response:any)=>void) {
            const self = this;
            this.httpExecute.performPost("/rest/yaormmain/get-tables",
                    request.toBase64(),
                    function(result:string) {
                        onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
                    },
                    onError);
        }
	get_table_definition(request: org.roylance.yaorm.UIYaormRequest, onSuccess:(response: org.roylance.yaorm.UIYaormResponse)=>void, onError:(response:any)=>void) {
            const self = this;
            this.httpExecute.performPost("/rest/yaormmain/get-table-definition",
                    request.toBase64(),
                    function(result:string) {
                        onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
                    },
                    onError);
        }
	get_table_definitions(request: org.roylance.yaorm.UIYaormRequest, onSuccess:(response: org.roylance.yaorm.UIYaormResponse)=>void, onError:(response:any)=>void) {
            const self = this;
            this.httpExecute.performPost("/rest/yaormmain/get-table-definitions",
                    request.toBase64(),
                    function(result:string) {
                        onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
                    },
                    onError);
        }
	get_record_count(request: org.roylance.yaorm.UIYaormRequest, onSuccess:(response: org.roylance.yaorm.UIYaormResponse)=>void, onError:(response:any)=>void) {
            const self = this;
            this.httpExecute.performPost("/rest/yaormmain/get-record-count",
                    request.toBase64(),
                    function(result:string) {
                        onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
                    },
                    onError);
        }
	get_records(request: org.roylance.yaorm.UIYaormRequest, onSuccess:(response: org.roylance.yaorm.UIYaormResponse)=>void, onError:(response:any)=>void) {
            const self = this;
            this.httpExecute.performPost("/rest/yaormmain/get-records",
                    request.toBase64(),
                    function(result:string) {
                        onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
                    },
                    onError);
        }
}