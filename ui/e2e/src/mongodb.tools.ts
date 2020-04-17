import * as mongoose from 'mongoose';

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
  public static async prepare(): Promise<any> {
    const comicsPath = process.env.DOCKER_MODE === 'true' ? '/comics' : 'src/test/resources/fixtures/full';
    await this.connect;
    await mongoose.connection.db.dropDatabase();
    const collection = await mongoose.connection.db.createCollection('setting');
    return collection.insertOne({
      comment: 'Path to your comic library',
      key    : 'comics.path',
      name   : 'Path',
      value  : comicsPath
    });
  }
}
