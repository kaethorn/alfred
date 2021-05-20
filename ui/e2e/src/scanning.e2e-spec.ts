import { LibraryPage } from './library.po';
import { MongoDBTools } from './mongodb.tools';
import { SettingsPage } from './settings.po';

describe('Scanning', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  it('should display a scan button', async () => {
    await SettingsPage.navigateTo();
    expect(await SettingsPage.getScanButton().isPresent()).toBe(true);
  });

  it('starts scanning comics when clicking the button', async () => {
    await SettingsPage.getScanButton().click();
    await SettingsPage.waitForScanStart();
  });

  it('display scan progress', async () => {
    expect(await SettingsPage.getScanProgress())
      .toMatch(/Scanning file \d+ of \d+ at .*/);
    expect(await SettingsPage.getScanErrors().isPresent()).toBe(false);
  });

  it('displays comics stats when the scan is finished', async () => {
    await SettingsPage.waitForScanEnd();
    const stats = await SettingsPage.getStatsText();
    expect(stats[0]).toMatch(/^Last scan finished\s+a few seconds ago$/);
    expect(stats[1]).toMatch(/^Last scan duration\s+a few seconds$/);
    expect(stats[2]).toMatch(/^Publishers\s+3$/);
    expect(stats[3]).toMatch(/^Series\s+5$/);
    expect(stats[4]).toMatch(/^Volumes\s+9$/);
    expect(stats[5]).toMatch(/^Issues\s+305$/);
    expect(stats[6]).toMatch(/^Users\s+0$/);
  });

  it('displays publishers in the library', async () => {
    await LibraryPage.navigateTo();
    expect(await LibraryPage.getAllPublishers().getText())
      .toEqual([ 'DC COMICS', 'F5 ENTERATINMENT', 'TOP COW' ]);
  });
});
