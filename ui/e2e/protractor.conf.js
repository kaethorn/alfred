// Protractor configuration file, see link for more information
// https://github.com/angular/protractor/blob/master/lib/config.ts

const { SpecReporter } = require('jasmine-spec-reporter');

exports.config = {
  allScriptsTimeout: 11000,
  specs            : [
    './src/**/*.e2e-spec.ts'
  ],
  capabilities: {
    browserName: 'chrome'
  },
  directConnect           : true,
  baseUrl                 : 'http://localhost:8080/',
  framework               : 'jasmine',
  SELENIUM_PROMISE_MANAGER: false,
  jasmineNodeOpts         : {
    showColors            : true,
    defaultTimeoutInterval: 30000,
    print                 : function () {}
  },
  async onPrepare () {
    await browser.get('/');
    await browser.executeScript(function () {
      const mockUser = {
        email: 'b.wayne@waynecorp.com',
        name: 'B.Wayne',
        picture: 'https://img.icons8.com/office/80/000000/batman-old.png',
        token: 'mock-123'
      };
      localStorage.setItem('token', mockUser.token);
      localStorage.setItem('user', JSON.stringify(mockUser));
    });

    require('ts-node').register({
      project: require('path').join(__dirname, './tsconfig.e2e.json')
    });
    jasmine.getEnv().addReporter(new SpecReporter({ spec: { displayStacktrace: true }}));
  }
};
