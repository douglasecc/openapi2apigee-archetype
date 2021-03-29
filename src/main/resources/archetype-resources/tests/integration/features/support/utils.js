'use strict';

module.exports = function() {

    var self = this;

    self.convertTable2Object = function(table, apickli) {
        var object = {};
        var entries = table.hashes();
        for (var i = 0; i < entries.length; i++) {
            object[entries[i].name] = apickli.replaceVariables(entries[i].value);
        }
        //console.log(entries);
        return object;
    }

    self.genNonce = function (length) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for(var i = 0; i < length; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }
        return text;
    }

    self.genCurTime = function () {
        return (Math.floor(new Date().getTime() / 1000)) + 1800;
    }

    self.stringToBytes = function (str) {
        var ch, st, re = [];
        for (var i = 0; i < str.length; i++ ) {
            ch = str.charCodeAt(i);  // get char
            st = [];                 // set up "stack"
            do {
                st.push( ch & 0xFF );  // push byte to stack
                ch = ch >> 8;          // shift value down by 1 byte
            }
            while ( ch );
            // add stack contents to result
            // done because chars have "wrong" endianness
            re = re.concat( st.reverse() );
        }
        // return an array of bytes
        return re;
    }

    self.base64Encode = function (value) {
        return Buffer.from(value).toString('base64');
    }

    self.base64Decode = function (value) {
        return Buffer.from(value, 'base64').toString('ascii');
    }
}