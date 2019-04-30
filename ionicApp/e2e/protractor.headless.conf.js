const config = require('./protractor.conf').config;

config.capabilities.chromeOptions = {
  args: [ '--headless', '--disable-gpu' ]
};

exports.config = config;
