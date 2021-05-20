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
    coverageReporter: {
      check: {
        each: {
          branches: 100,
          functions: 100,
          lines: 100,
          statements: 100
        },
        global: {
          branches: 100,
          functions: 100,
          lines: 100,
          statements: 100
        }
      },
      reporters: [
        { subdir: '.', type:'lcovonly' },
        { subdir: '.', type:'html' },
        { subdir: '.', type:'text-summary' },
        { subdir: '.', type:'json' }
      ]
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
      '/api/read/923/0': '/assets/icons/alfred.svg',
      '/api/scan/resume': 'http://localhost:9876',
      '/api/scan/start': 'http://localhost:9876'
    },
    reporters: [ 'progress', 'kjhtml' ],
    singleRun: false
  });
};
