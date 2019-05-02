import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { MongoDBTools } from './mongodb.tools';

describe('LibraryComponent', () => {
  let settingsPage: SettingsPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    settingsPage = new SettingsPage();
    libraryPage = new LibraryPage();
  });

  it('scans for comics', async () => {
    await settingsPage.scan();
  });

  it('sorts publishers alphabetically', async () => {
    await libraryPage.navigateTo();
    expect(await libraryPage.getAllPublishers().getText())
      .toEqual(['DC COMICS', 'F5 ENTERATINMENT', 'TOP COW']);
  });

  it('sorts series alphabetically', async () => {
    await libraryPage.clickPublisher('DC Comics');
    await libraryPage.waitForSeries();
    expect(await libraryPage.getAllSeries().getText())
      .toEqual([ 'BATGIRL', 'BATMAN' ]);
  });

  it('sorts volumes alphabetically', async () => {
    await libraryPage.clickSeries('Batgirl');
    await libraryPage.waitForVolumes();
    expect(await libraryPage.getVolumeTitles().getText())
      .toEqual(['Vol. 2000', 'Vol. 2008', 'Vol. 2009', 'Vol. 2011', 'Vol. 2016']);
  });

  it('shows the read issue counter', async () => {
    await libraryPage.expectVolumeStats(
      [ '0 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
  });

  it('shows no read icon', async () => {
    expect(await libraryPage.getUnreadVolumes().count()).toBe(5);
  });

  describe('when marking an entire volume as read', () => {

    beforeAll(async () => {
      await libraryPage.clickVolumeMenuItem('Vol. 2000', 'Mark volume as read');
    });

    it('updates its read issue counter', async () => {
      expect(await libraryPage.getVolumeStats())
        .toEqual([ '73 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('shows a read icon', async () => {
      expect(await libraryPage.getUnreadVolumes().count()).toBe(4);
    });
  });

  describe('when marking an entire volume as unread', () => {

    beforeAll(async () => {
      await libraryPage.clickVolumeMenuItem('Vol. 2000', 'Mark volume as not read');
    });

    it('updates its read issue counter', async () => {
      expect(await libraryPage.getVolumeStats())
        .toEqual([ '0 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('removes the read icon', async () => {
      expect(await libraryPage.getUnreadVolumes().count()).toBe(5);
    });
  });
});
