const cucumberJSON = require('multiple-cucumber-html-reporter');
const metadata = null;

exports.gerarRelatorioHtml = function () {
 
cucumberJSON.generate({
    
    jsonDir: './reports/',
    reportPath: './reports/',
    metadata:{
        browser: {
            name: 'chrome',
            version: '58'
        },
        device: 'Maquina Claro',
        platform: {
            name: 'Windows',
            version: ''
        },
            name: 'Environment v.', value: '12.3',
            name: 'Plugin v.', value: '32.1',
            name: 'Variable set', value: 'Foo'
    },
    customData: {
        title: 'Run info',
        data: [
            {label: 'Project', value: 'Claro - APIgee'},
            {label: 'Release', value: ''},
            {label: 'Cycle', value: ''},
            {label: 'Execution Start Time', value: (new Date)},
            {label: 'Execution End Time', value: (new Date)}
        ]
    }
}
);

}
