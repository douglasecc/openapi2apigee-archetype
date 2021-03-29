/* jslint node: true */
'use strict';
var apickli = require('apickli');

// var jwt = require('jsonwebtoken');
// var cert = fs.readFileSync(process.cwd() + '/target/test/tpp.key');
// var CryptoJS = require("crypto-js");

const URL = require('url');
const jsonPath = require('JSONPath');

var fs = require('fs-extra');
var utils = new (require("../support/utils.js"))();

module.exports = function () {

    this.Given(/^I use variables from app (.*)$/, function (appParameter, callback) {
        var developerApp = this.apickli.replaceVariables(appParameter);
        for (var paramName in this.apickli.scenarioVariables['developerApps'][developerApp]) {
            this.apickli.storeValueInScenarioScope(paramName, this.apickli.scenarioVariables['developerApps'][developerApp][paramName]);
        }
        callback();
    });



    this.Given(/^I extract query parameters from (.*) and store them with prefix (.*)$/, function(variable, prefix, callback) {
        var variable = this.apickli.replaceVariables(variable);
        var prefix = prefix ? this.apickli.replaceVariables(prefix) + '.' : '';
        var url = URL.parse(variable);
        var self = this;
        url.query.split('&').forEach(function(item){
            var itemParts = item.split('=');
            var paramName = prefix + itemParts[0];
            var paramValue = decodeURIComponent(itemParts.slice(1).join('='));
            self.apickli.storeValueInScenarioScope(paramName, paramValue);
            // console.log(paramName + '= ' + paramValue);
        });
        callback();
    });

    // this.Given(/^I have a client credentials access_token using app (.*)$/, function (appParameter, callback) {
    //     var developerApp = this.apickli.replaceVariables(appParameter);
    //     var payload = {
    //         iss: this.apickli.scenarioVariables['developerApps'][developerApp].clientId,
    //         sub: this.apickli.scenarioVariables['developerApps'][developerApp].clientId,
    //         aud: this.apickli.scenarioVariables['audience'],
    //         scope: this.apickli.scenarioVariables['developerApps'][developerApp].tokenScope,
    //         nonce: utils.genNonce(12),
    //         exp: utils.genCurTime(),
    //         response_type: 'token'
    //     };
    //
    //     var jws = jwt.sign(payload, cert, {algorithm: "RS256"});
    //
    //     var domain = this.apickli.scenarioVariables["tokenDomain"];
    //     var basepath = this.apickli.scenarioVariables["tokenBasepath"];
    //
    //     var callout = new apickli.Apickli('https', domain + basepath);
    //     callout.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    //     callout.setRequestBody("grant_type=client_credentials&client_assertion="+jws);
    //
    //     var self = this;
    //
    //     callout.post("/token", function (error, response) {
    //         if (error) {
    //             callback(new Error(error));
    //         }
    //         var token = JSON.parse(response.body).access_token;
    //         self.apickli.setAccessToken(token);
    //         self.apickli.setBearerToken();
    //         self.apickli.scenarioVariables["access_token"] = token;
    //         self.apickli.setAccessToken(token);
    //         //console.log("Access Token: " + token);
    //         callback();
    //     });
    // });

    // this.Given(/^I unset access_token/, function(callback) {
    //     this.apickli.unsetAccessToken();
    //     this.apickli.setAccessToken(undefined);
    //     callback();
    // });
};
