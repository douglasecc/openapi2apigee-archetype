/* jslint node: true */
'use strict';

var CryptoJS = require("crypto-js");

var hmacUtil = function () {
    var self = this;

    self.hmac = function(data, clientSecret, nonce, issued) {

        var data = data + nonce + issued;

        var signature = CryptoJS.HmacSHA512(data, clientSecret);
        var hmac = signature.toString(CryptoJS.enc.Base64);

        return hmac;
    }

    self.nonce = function(length) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for(var i = 0; i < length; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }
        return text;
    }
}

module.exports = hmacUtil;