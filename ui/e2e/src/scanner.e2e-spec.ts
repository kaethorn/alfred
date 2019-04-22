import { ScannerPage } from './scanner.po';
import { LibraryPage } from './library.po';
import { MongoDBTools } from './mongodb.tools';

describe('ScannerComponent', () => {
  let page: ScannerPage;
  let libraryPage: LibraryPage;

  beforeEach(async () => {
    page = new ScannerPage();
    libraryPage = new LibraryPage();
    await MongoDBTools.prepare();
  });

  it('should display a scan button', async () => {
    await page.navigateTo();
    expect(await page.getScanButton().isPresent()).toBe(true);
  });

  it('starts scanning comics when clicking the button', async () => {
    await page.getScanButton().click();
    await page.waitForScanStart();
    expect(await page.getScanProgress())
      .toMatch(/Scanning\ file\ \d+\ of\ \d+\ at\ .*/);
    expect(await page.getScanErrors().isPresent()).toBe(false);
  });

  it('displays a list of publishers on completion', async () => {
    await page.waitForScanEnd();
    expect(await libraryPage.getComicPublishers())
      .toEqual(['DC Comics', 'F5 Enteratinment', 'Top Cow']);
  });
});
