import * as mongoose from 'mongoose';

export class MongoDBTools {

  private static connection: Promise<any> = new Promise((resolve, reject) => {
    mongoose.connect('mongodb://localhost/alfred');
    mongoose.connection.on('error', error => {
      reject(error);
    });
    mongoose.connection.once('open', () => {
      resolve();
    });
  });

  // Drop DB and set it up it for E2E tests.
  static prepare (): Promise<any> {
    return new Promise((resolve, reject) => {
      this.connection.then(() => {
        mongoose.connection.db.dropDatabase().then(() => {
          mongoose.connection.db.createCollection('setting').then(collection => {
            collection.insertOne({
              key    : 'comics.path',
              name   : 'Path',
              value  : '/comics',
              comment: 'Path to your comic library'
            }).then(() => {
              resolve();
            });
          });
        });
      });
    });
  }
}
