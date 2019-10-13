const express = require('express');
const proxy = require('http-proxy-middleware');

let server;
const flags = {
  offline: false,
};

const parse = (req) => {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', (chunk) => {
      body += chunk;
    });
    req.on('end', () => {
      resolve(JSON.parse(body));
    });
    req.on('error', reject);
  });
};

module.exports = {

  start: () => {
    const app = express();

    app.post('/flags', async (req, res) => {
      const body = await parse(req);
      console.log(`Setting flag ${ JSON.stringify(body) }`);
      Object.assign(flags, body);
      res.json({});
    });

    // Proxy only if the offline flag is not set
    const proxyFilter = (pathname, req) => {
      if (flags.offline) {
        console.log(`Blocking ${ req.method } to ${ req.url } due to offline flag.`);
        return false;
      }
      return true;
    };

    app.use('', proxy(proxyFilter, {
      target: 'http://localhost:8080',
    }));

    return new Promise((resolve, reject) => {
      server = app.listen(8090, '0.0.0.0', () => {
        console.log(`Started test proxy on port 8090.`);
        resolve();
      }).on('error', (error) => reject(error));
    });
  },

  stop: () => {
    return new Promise(resolve => {
      server.close(() => {
        console.log(`Shutting down test proxy.`);
        resolve();
      });
    });
  }
};
