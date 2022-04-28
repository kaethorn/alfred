import { connect, connection } from 'mongoose';

export class MongoDBTools {

  private static connect: Promise<any> = new Promise((resolve, reject) => {
    const host = process.env.DOCKER_MODE === 'true' ? 'mongo' : 'localhost';
    connect(`mongodb://${ host }/alfred`);
    connection.on('error', error => {
      reject(error);
    });
    connection.once('open', () => {
      resolve(null);
    });
  });

  // Prepare DB and fixtures for E2E tests.
  public static async prepare(): Promise<any> {
    const comicsPath = process.env.DOCKER_MODE === 'true' ? '/comics' : 'src/test/resources/fixtures/full';
    await this.connect;
    await connection.db.dropDatabase();
    const collection = await connection.db.createCollection('setting');
    return collection.insertOne({
      comment: 'Path to your comic library',
      key    : 'comics.path',
      name   : 'Path',
      value  : comicsPath
    });
  }
}
