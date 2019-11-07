const config = require('./protractor.conf').config;

config.capabilities.chromeOptions = {
  args: [ '--headless', '--window-size=3840,2160' ]
};

exports.config = config;
