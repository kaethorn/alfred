import * as http from 'http';

export class ProxySettings {

  public static set(flag: {} | undefined): Promise<any> {
    return this.post('/flags', flag);
  }

  private static post(path: string, body = {}): Promise<any> {
    const bodyString = JSON.stringify(body);
    return new Promise((resolve, reject) => {
      const req = http.request({
        headers: {
          'Content-Length': Buffer.byteLength(bodyString),
          'Content-Type'  : 'application/json'
        },
        method: 'POST',
        path,
        port   : '8090'
      }, res => {
        try {
          if ((res.statusCode || 0) > 399) {
            reject(res.statusCode);
          } else {
            res.setEncoding('utf8');
            let rawData = '';
            res.on('data', chunk => {
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
      }).on('error', e => {
        reject(e.message);
      });
      req.end(bodyString);
    });
  }
}
