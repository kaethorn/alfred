import * as mongoose from 'mongoose';

export class MongoDBTools {

  // Drop DB and set it up it for E2E tests.
  static prepare(): Promise<any> {
    return new Promise((resolve, reject) => {
      mongoose.connect('mongodb://localhost/alfred');
      mongoose.connection.on('error', error => {
        reject(error);
      });
      mongoose.connection.once('open', () => {
        mongoose.connection.db.dropDatabase().then(() => {
          mongoose.connection.db.createCollection('preference').then(collection => {
            collection.insert([{
              key    : 'comics.path',
              name   : 'Path',
              value  : process.env.COMICS_PATH || '/comics',
              comment: 'Path to your comic library'
            }]).then(() => {
              resolve();
            });
          });
        });
      });
    });
  }
}
