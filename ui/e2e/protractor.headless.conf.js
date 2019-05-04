const config = require('./protractor.conf').config;

config.capabilities.chromeOptions = {
  args: [ '--headless', '--disable-gpu', '--window-size=1024,2048' ]
};

exports.config = config;
