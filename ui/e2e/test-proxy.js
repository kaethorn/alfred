const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

let server;
const flags = {
  offline: false
};

const parse = req =>
  new Promise((resolve, reject) => {
    let body = '';
    req.on('data', chunk => {
      body += chunk;
    });
    req.on('end', () => {
      resolve(JSON.parse(body));
    });
    req.on('error', reject);
  });

module.exports = {

  start: () => {
    const app = express();

    // Store test flags
    app.post('/flags', async (req, res) => {
      const body = await parse(req);
      Object.assign(flags, body);
      res.json({});
    });

    // Proxy only if the offline flag is not set
    const proxyFilter = () => !flags.offline;

    // Proxy requests to the web server
    app.use('', createProxyMiddleware(proxyFilter, {
      onProxyReq: (proxyReq, req) => {
        if ('' in req.headers) {
          delete req.headers[''];
        }
      },
      target: 'http://localhost:8082'
    }));

    return new Promise((resolve, reject) => {
      server = app.listen(8090, '0.0.0.0', () => resolve())
        .on('error', error => reject(error));
    });
  },

  stop: () =>
    new Promise(resolve => {
      server.close(() => resolve());
    })
};
