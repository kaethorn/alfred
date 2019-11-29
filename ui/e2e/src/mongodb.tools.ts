import * as mongoose from 'mongoose';
import { exec } from 'child_process';

export class MongoDBTools {

  private static connect: Promise<any> = new Promise((resolve, reject) => {
    const host = process.env.DOCKER_MODE === 'true' ? 'mongo' : 'localhost';
    mongoose.set('useNewUrlParser', true);
    mongoose.set('useUnifiedTopology', true);
    mongoose.connect(`mongodb://${ host }/alfred`);
    mongoose.connection.on('error', error => {
      reject(error);
    });
    mongoose.connection.once('open', () => {
      resolve();
    });
  });

  // Prepare DB and fixtures for E2E tests.
  static async prepare (): Promise<any> {
    await this.connect;
    await mongoose.connection.db.dropDatabase();
    const collection = await mongoose.connection.db.createCollection('setting');
    return collection.insertOne({
      key    : 'comics.path',
      name   : 'Path',
      value  : '/comics',
      comment: 'Path to your comic library'
    });
  }
}
