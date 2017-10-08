// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
/// <reference path="../node_modules/roylance.common/bytebuffer.d.ts" />
import {YaormModel} from "../node_modules/org.roylance.yaorm/YaormModelFactory";

import {YaormMainService} from "../node_modules/org.roylance.yaorm/YaormMainService";

import {HttpExecute} from "./HttpExecute"
import {furtherAngularSetup} from "./FurtherAngularSetup"

declare var angular: any;
const app = angular.module('jsapp', [
    "ngRoute"
]);

app.factory("httpExecute", function ($window, $http) {
    return new HttpExecute($http);
});

app.factory("yaormModel", function () {
    return YaormModel.org.roylance;
});

app.factory("yaormMainService", function(httpExecute:HttpExecute, yaormModel:org.roylance.yaorm.ProtoBufBuilder) {
    return new YaormMainService(httpExecute, yaormModel)
});


furtherAngularSetup(app);
