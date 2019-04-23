import { ScannerPage } from './scanner.po';
import { LibraryPage } from './library.po';
import { MongoDBTools } from './mongodb.tools';

describe('ScannerComponent', () => {
  let scannerPage: ScannerPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    scannerPage = new ScannerPage();
    libraryPage = new LibraryPage();
  });

  it('should display a scan button', async () => {
    await scannerPage.navigateTo();
    expect(await scannerPage.getScanButton().isPresent()).toBe(true);
  });

  it('starts scanning comics when clicking the button', async () => {
    await scannerPage.getScanButton().click();
    await scannerPage.waitForScanStart();
    expect(await scannerPage.getScanProgress())
      .toMatch(/Scanning\ file\ \d+\ of\ \d+\ at\ .*/);
    expect(await scannerPage.getScanErrors().isPresent()).toBe(false);
  });

  it('displays a list of publishers on completion', async () => {
    await scannerPage.waitForScanEnd();
    expect(await libraryPage.getAllPublishers().getText())
      .toEqual(['DC Comics', 'F5 Enteratinment', 'Top Cow']);
  });
});
