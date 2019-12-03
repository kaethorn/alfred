const config = require('./protractor.conf').config;

config.capabilities.chromeOptions = {
  args: [ '--headless', '--window-size=3840,2160', '--no-sandbox', '--disable-setuid-sandbox' ]
};

exports.config = config;
