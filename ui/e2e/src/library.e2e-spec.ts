import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { AppPage } from './app.po';
import { MongoDBTools } from './mongodb.tools';

describe('LibraryComponent', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  it('scans for comics', async () => {
    await SettingsPage.scan();
  });

  it('sorts publishers alphabetically', async () => {
    await LibraryPage.navigateTo();
    expect(await AppPage.getTitleText()).toContain('Series');
    expect(await LibraryPage.getAllPublishers().getText())
      .toEqual(['DC COMICS', 'F5 ENTERATINMENT', 'TOP COW']);
  });

  it('sorts nested series alphabetically', async () => {
    expect(await LibraryPage.getAllPublisherSeries().getText())
      .toEqual([ 'BATGIRL', 'BATMAN', 'THE TENTH', 'THE TENTH: RESURRECTED', 'RISING STARS' ]);
  });

  it('sorts series alphabetically', async () => {
    await LibraryPage.clickPublisher('DC Comics');
    await LibraryPage.waitForSeries();
    expect(await AppPage.getTitleText()).toContain('DC Comics series');
    expect(await LibraryPage.getAllSeries().getText())
      .toEqual([ 'BATGIRL', 'BATMAN' ]);
  });

  it('sorts volumes alphabetically', async () => {
    await LibraryPage.clickSeries('Batgirl');
    await LibraryPage.waitForVolumes();
    expect(await AppPage.getTitleText()).toContain('Batgirl volumes');
    expect(await LibraryPage.getVolumeTitles().getText())
      .toEqual(['Vol. 2000', 'Vol. 2008', 'Vol. 2009', 'Vol. 2011', 'Vol. 2016']);
  });

  it('shows the read issue counter', async () => {
    await LibraryPage.expectVolumeStats(
      [ '0 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
  });

  it('shows no read icon', async () => {
    expect(await LibraryPage.getUnreadVolumes().count()).toBe(5);
  });

  describe('when marking an entire volume as read', () => {

    beforeAll(async () => {
      await LibraryPage.clickVolumeMenuItem('Vol. 2000', 'Mark volume as read');
    });

    it('updates its read issue counter', async () => {
      expect(await LibraryPage.getVolumeStats())
        .toEqual([ '73 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('shows a read icon', async () => {
      expect(await LibraryPage.getUnreadVolumes().count()).toBe(4);
    });
  });

  describe('when marking an entire volume as unread', () => {

    beforeAll(async () => {
      await LibraryPage.clickVolumeMenuItem('Vol. 2000', 'Mark volume as not read');
    });

    it('updates its read issue counter', async () => {
      expect(await LibraryPage.getVolumeStats())
        .toEqual([ '0 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('removes the read icon', async () => {
      expect(await LibraryPage.getUnreadVolumes().count()).toBe(5);
    });
  });
});
