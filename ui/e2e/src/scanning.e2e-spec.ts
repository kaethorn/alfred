import { SettingsPage } from './settings.po';
import { LibraryPage } from './library.po';
import { MongoDBTools } from './mongodb.tools';

describe('Scanning', () => {
  let settingsPage: SettingsPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    settingsPage = new SettingsPage();
    libraryPage = new LibraryPage();
  });

  it('should display a scan button', async () => {
    await settingsPage.navigateTo();
    expect(await settingsPage.getScanButton().isPresent()).toBe(true);
  });

  it('starts scanning comics when clicking the button', async () => {
    await settingsPage.getScanButton().click();
    await settingsPage.waitForScanStart();
  });

  it('display scan progress', async () => {
    expect(await settingsPage.getScanProgress())
      .toMatch(/Scanning\ file\ \d+\ of\ \d+\ at\ .*/);
    expect(await settingsPage.getScanErrors().isPresent()).toBe(false);
  });

  it('displays comics stats when the scan is finished', async () => {
    await settingsPage.waitForScanEnd();
    const stats = await settingsPage.getStats();
    expect(stats[0]).toMatch(/^issues\s+305$/);
    expect(stats[1]).toMatch(/^publishers\s+3$/);
    expect(stats[2]).toMatch(/^series\s+5$/);
    expect(stats[3]).toMatch(/^users\s+0$/);
    expect(stats[4]).toMatch(/^volumes\s+9$/);
  });

  it('displays publishers in the library', async () => {
    await libraryPage.navigateTo();
    expect(await libraryPage.getAllPublishers().getText())
      .toEqual(['DC COMICS', 'F5 ENTERATINMENT', 'TOP COW']);
  });
});
