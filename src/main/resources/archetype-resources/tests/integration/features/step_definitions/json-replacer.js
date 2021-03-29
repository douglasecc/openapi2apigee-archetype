/* jslint node: true */
'use strict';
var apickli = require('apickli');

const jsonPath = require('JSONPath');


module.exports = function () {

    this.Given(/^I set request body path (.*) to (.*)$/, function (path, value, callback) {
        path = this.apickli.replaceVariables(path);
        value = this.apickli.replaceVariables(value);
        var body = JSON.parse(this.apickli.requestBody);
        var nodes = jsonPath({json: body, path: path, resultType: 'path', autostart:true});
        if (nodes.length > 0) {
            for (var i = 0; i < nodes.length; i++) {
                var nodePath = JSON.parse(nodes[i]
                    .substring(1)
                    .replace(/'/g,'"')
                    .replace(/]\[/g, ',')).join('.');
                eval ('body.' + nodePath + '=value');
            }
        } else {
            throw 'Could not assing property with path ' + path;
        }
        this.apickli.setRequestBody(JSON.stringify(body));
        callback();
    });

    this.Given(/^I remove request body path (.*)$/, function (path, callback) {
        path = this.apickli.replaceVariables(path);
        var body = JSON.parse(this.apickli.requestBody);
        var nodes = jsonPath({json: body, path: path, resultType: 'path', autostart:true});
        if (nodes.length > 0) {
            for (var i = 0; i < nodes.length; i++) {
                var nodePath = JSON.parse(nodes[i]
                     .substring(1)
                     .replace(/'/g,'"')
                     .replace(/]\[/g, ',')).join('.');
                eval ('delete body.' + nodePath);
            }
        } else {
            throw 'Could not remove property with path ' + path;
        }
        this.apickli.setRequestBody(JSON.stringify(body));
        callback();
    });
};



