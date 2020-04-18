const PROXY_CONFIG = {
  '/api': {
    bypass(req) {
      delete req.headers[''];
    },
    secure: false,
    target: 'http://localhost:8080'
  }
};

module.exports = PROXY_CONFIG;

