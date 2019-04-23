import { LibraryPage } from './library.po';
import { ScannerPage } from './scanner.po';
import { MongoDBTools } from './mongodb.tools';

describe('LibraryComponent', () => {
  let scannerPage: ScannerPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    scannerPage = new ScannerPage();
    libraryPage = new LibraryPage();
  });

  it('scans for comics', async () => {
    await scannerPage.scan();
  });

  it('sorts publishers alphabetically', async () => {
    expect(await libraryPage.getAllPublishers().getText())
      .toEqual(['DC Comics', 'F5 Enteratinment', 'Top Cow']);
  });

  it('sorts series alphabetically', async () => {
    expect(await libraryPage.getAllSeries().getText())
      .toEqual([ 'Batman', 'Batgirl', 'The Tenth: Resurrected', 'The Tenth', 'Rising Stars' ]);
  });

  it('sorts volumes alphabetically', async () => {
    await libraryPage.getSeries('Batgirl').click();
    await libraryPage.waitForSeries('Batgirl');
    expect(await libraryPage.getVolumeTitles().getText())
      .toEqual(['Vol. 2000', 'Vol. 2008', 'Vol. 2009', 'Vol. 2011', 'Vol. 2016']);
  });

  it('shows the read issue counter', async () => {
    expect(await libraryPage.getVolumeStats().getText())
      .toEqual([ '0 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
  });

  it('shows no read icon', async () => {
    expect(await libraryPage.getUnreadVolumes().count()).toBe(5);
  });

  describe('when marking an entire volume as read', () => {

    beforeAll(async () => {
      await libraryPage.clickVolumeMenuItem('Vol. 2000', 'Mark volume as read');
    });

    it('updates its read issue counter', async () => {
      expect(await libraryPage.getVolumeStats().getText())
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
      expect(await libraryPage.getVolumeStats().getText())
        .toEqual([ '0 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('removes the read icon', async () => {
      expect(await libraryPage.getUnreadVolumes().count()).toBe(5);
    });
  });
});
