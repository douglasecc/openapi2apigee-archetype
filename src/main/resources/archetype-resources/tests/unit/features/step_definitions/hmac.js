/* jslint node: true */
'use strict';

// Create a new singleton
var hmacUtil = new (require("../support/hmac-util.js"))();

module.exports = function () {

    var setupCommon = function(apickli, verb, developerApp, pathSuffix) {

        var issued = (Math.floor(new Date().getTime() / 1000)) + '';
        var nonce = hmacUtil.nonce(12);

        var clientSecret = apickli.scenarioVariables['developerApps'][developerApp].clientSecret;

        var data = apickli.requestBody != undefined && apickli.requestBody != null && apickli.requestBody.length > 0
            ? apickli.requestBody
            : pathSuffix;

        var hmac = hmacUtil.hmac(data, clientSecret, nonce, issued);

        apickli.addRequestHeader("hmac-token", hmac);
        apickli.addRequestHeader("hmac-nonce", nonce);
        apickli.addRequestHeader("hmac-issued", issued);
    };

    this.Given(/^I set HMAC headers for application (.*)$/, function (appParameter, callback) {
        setupCommon(this.apickli, "", this.apickli.replaceVariables(appParameter), "");
        callback();
    });

    this.When(/^I use HMAC for application (.*) and GET (.*)$/, function (appParameter, pathSuffix, callback) {
        setupCommon(this.apickli, "GET", this.apickli.replaceVariables(appParameter), pathSuffix);
        this.apickli.get(pathSuffix, function(err, response) {
            callback(err);
        });
    });

    this.When(/^I use HMAC for application (.*) and PUT to (.*)$/, function (appParameter, pathSuffix, callback) {

        setupCommon(this.apickli, "PUT", this.apickli.replaceVariables(appParameter), pathSuffix);
        this.apickli.put(pathSuffix, function(err, response) {
            callback(err);
        });
    });

    this.When(/^I use HMAC for application (.*) and POST to (.*)$/, function (appParameter, pathSuffix, callback) {
        setupCommon(this.apickli, "POST", this.apickli.replaceVariables(appParameter), pathSuffix);
        this.apickli.post(pathSuffix, function(err, response) {
            callback(err);
        });
    });

    this.When(/^I use HMAC for application (.*) and DELETE (.*)$/, function (appParameter, pathSuffix, callback) {
        setupCommon(this.apickli, "DELETE", this.apickli.replaceVariables(appParameter), pathSuffix);
        this.apickli.delete(pathSuffix, function(err, response) {
            callback(err);
        });
    });

};