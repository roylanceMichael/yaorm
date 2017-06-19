// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
import {YaormMainService} from "../node_modules/org.roylance.yaorm.api/YaormMainService";

import {HttpExecute} from "./HttpExecute"
import {furtherAngularSetup} from "./FurtherAngularSetup"

declare var angular: any;
const app = angular.module('jsapp', [
    "ngRoute"
]);

app.factory("httpExecute", function ($window, $http) {
    return new HttpExecute($http);
});

app.factory("yaormMainService", function(httpExecute:HttpExecute) {
    return new YaormMainService(httpExecute);
});


furtherAngularSetup(app);
