// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

module.exports = function(config) {
  config.set({
    autoWatch: true,
    basePath  : '',
    browsers : [ 'Chrome' ],
    client: {
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    colors   : true,
    coverageIstanbulReporter: {
      dir                  : require('path').join(__dirname, '../coverage'),
      fixWebpackSourcePaths: true,
      reports              : [ 'json', 'html', 'lcovonly', 'text-summary' ]
    },
    customLaunchers: {
      ChromeHeadlessDocker: {
        base: 'ChromeHeadless',
        flags: [
          '--no-sandbox',
          '--disable-setuid-sandbox'
        ]
      }
    },
    frameworks: [ 'jasmine', '@angular-devkit/build-angular' ],
    logLevel : config.LOG_INFO,
    plugins   : [
      require('karma-chrome-launcher'),
      require('karma-coverage'),
      require('karma-jasmine'),
      require('karma-jasmine-html-reporter'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    port     : 9876,
    proxies  : {
      '/api/read/923/0': '/assets/icons/alfred.svg'
    },
    reporters: [ 'progress', 'kjhtml' ],
    singleRun: false
  });
};
