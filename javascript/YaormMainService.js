"use strict";
var YaormMainService = (function () {
    function YaormMainService(httpExecute, modelFactory) {
        this.httpExecute = httpExecute;
        this.modelFactory = modelFactory;
    }
    YaormMainService.prototype.get_schemas = function (request, onSuccess, onError) {
        var self = this;
        this.httpExecute.performPost("/rest/yaormmain/get-schemas", request.toBase64(), function (result) {
            onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
        }, onError);
    };
    YaormMainService.prototype.get_tables = function (request, onSuccess, onError) {
        var self = this;
        this.httpExecute.performPost("/rest/yaormmain/get-tables", request.toBase64(), function (result) {
            onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
        }, onError);
    };
    YaormMainService.prototype.get_table_definition = function (request, onSuccess, onError) {
        var self = this;
        this.httpExecute.performPost("/rest/yaormmain/get-table-definition", request.toBase64(), function (result) {
            onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
        }, onError);
    };
    YaormMainService.prototype.get_table_definitions = function (request, onSuccess, onError) {
        var self = this;
        this.httpExecute.performPost("/rest/yaormmain/get-table-definitions", request.toBase64(), function (result) {
            onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
        }, onError);
    };
    YaormMainService.prototype.get_record_count = function (request, onSuccess, onError) {
        var self = this;
        this.httpExecute.performPost("/rest/yaormmain/get-record-count", request.toBase64(), function (result) {
            onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
        }, onError);
    };
    YaormMainService.prototype.get_records = function (request, onSuccess, onError) {
        var self = this;
        this.httpExecute.performPost("/rest/yaormmain/get-records", request.toBase64(), function (result) {
            onSuccess(self.modelFactory.UIYaormResponse.decode64(result));
        }, onError);
    };
    return YaormMainService;
}());
exports.YaormMainService = YaormMainService;
//# sourceMappingURL=YaormMainService.js.map