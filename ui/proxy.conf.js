const PROXY_CONFIG = {
  '/api': {
    target: 'http://localhost:8080',
    secure: false,
    bypass(req) {
      delete req.headers[''];
    }
  }
};

module.exports = PROXY_CONFIG;

