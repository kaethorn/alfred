import * as http from 'http';

export class ProxySettings {

  static set (flag) {
    return this.post('/flags', flag);
  }

  private static post (path, body = {}): Promise<any> {
    const bodyString = JSON.stringify(body);
    return new Promise((resolve, reject) => {
      const req = http.request({
        port   : '8090',
        path,
        headers: {
          'Content-Type'  : 'application/json',
          'Content-Length': Buffer.byteLength(bodyString)
        },
        method: 'POST'
      }, (res) => {
        try {
          if (res.statusCode > 399) {
            reject(res.statusCode);
          } else {
            res.setEncoding('utf8');
            let rawData = '';
            res.on('data', (chunk) => {
              rawData += chunk;
            });
            res.on('end', () => {
              try {
                resolve(JSON.parse(rawData));
              } catch (e) {
                reject(e.message);
              }
            });
          }
        } catch (e) {
          reject(e.message);
        }
      }).on('error', (e) => {
        reject(e.message);
      });
      req.end(bodyString);
    });
  }
}
