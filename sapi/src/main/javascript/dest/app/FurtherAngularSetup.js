"use strict";
function furtherAngularSetup(app) {
    // including show-tab in there, I find this useful for angular
    app.directive('showTab', function () {
        return {
            link: function (scope, element, attrs) {
                element.click(function (e) {
                    e.preventDefault();
                    $(element).tab('show');
                });
            }
        };
    });
}
exports.furtherAngularSetup = furtherAngularSetup;
