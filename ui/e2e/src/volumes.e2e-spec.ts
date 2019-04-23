import { ScannerPage } from './scanner.po';
import { MongoDBTools } from './mongodb.tools';

describe('VolumesComponent', () => {
  let scannerPage: ScannerPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    scannerPage = new ScannerPage();
  });

  it('scans for comics', async () => {
    await scannerPage.scan();
  });

  xit('sorts publishers alphabetically', async () => {
    // TODO
  });

  xit('sorts series alphabetically', async () => {
    // TODO
  });

  xit('sorts volumes alphabetically', async () => {
    // TODO
  });

  xit('shows the read issue counter', async () => {
    // TODO
  });

  xit('can mark a whole volume as read and updates its read issue counter', async () => {
    // TODO
  });
});
