/* jslint node: true */
'use strict';

var prettyJson = require('prettyjson');

var stepContext = {};

const callbackWithAssertion = function(callback, assertion) {
  if (assertion.success) {
    callback();
  } else {
    callback(prettyPrintJson(assertion));
  }
};

module.exports = function () {
    // this.Given(/^I set variable (.*) in global scope to (.*)$/, function (variableName, variableValue, callback) {
    //     this.apickli.setGlobalVariable(variableName, variableValue);
    //     callback();
    // });

    this.Then(/^if global variable (.*) is (.*) also response header (.*) should be (.*)$/, function (variableName, variableValue, header, expression, callback) {
        if (this.apickli.getGlobalVariable(variableName) !== variableValue) {
			callback();
		}
		else
		{
			const assertion = this.apickli.assertHeaderValue(header, expression);
			callbackWithAssertion(callback, assertion);
		}
    });

    this.Then(/^if global variable (.*) is (.*) also response header (.*) should not be (.*)$/, function (variableName, variableValue, header, expression, callback) {
        if (this.apickli.getGlobalVariable(variableName) !== variableValue) {
			callback();
		}
		else
		{
			var assertion = this.apickli.assertHeaderValue(header, expression);
			assertion.success = !assertion.success;
			callbackWithAssertion(callback, assertion);
		}
    });

	this.When(/^I GET$/, function (callback) {
        this.apickli.get("", function (error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

    this.When(/^I POST$/, function (callback) {
        this.apickli.post("", function (error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

    this.When(/^I POST ((?!to).+)$/, function (resource, callback) {
        this.apickli.post(resource, function (error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

    this.When(/^I PUT$/, function (callback) {
        this.apickli.put("", function (error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

    this.When(/^I DELETE$/, function (callback) {
        this.apickli.delete("", function (error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

    this.When(/^I PATCH$/, function (callback) {
        this.apickli.patch("", function (error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

    this.When(/^I request OPTIONS$/, function(callback) {
        this.apickli.options("", function(error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

    this.When(/^I request OPTIONS ((?!for).+)$/, function(resource, callback) {
        this.apickli.options(resource, function(error, response) {
            if (error) {
                callback(new Error(error));
            }

            callback();
        });
    });

};

var prettyPrintJson = function(json) {
    var output = {
        stepContext: stepContext,
        testOutput: json
    };

    return prettyJson.render(output, {
        noColor: true
    });
};
