import { BookmarksPage } from './bookmarks.po';
import { LibraryPage } from './library.po';
import { ScannerPage } from './scanner.po';
import { VolumesPage } from './volumes.po';
import { MongoDBTools } from './mongodb.tools';

describe('BookmarksComponent', () => {

  let bookmarksPage: BookmarksPage;
  let volumesPage: VolumesPage;
  let scannerPage: ScannerPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    bookmarksPage = new BookmarksPage();
    volumesPage = new VolumesPage();
    scannerPage = new ScannerPage();
    libraryPage = new LibraryPage();
  });

  it('scans for comics', async () => {
    await scannerPage.scan();
  });

  it('shows no bookmarks by default', async () => {
    await bookmarksPage.navigateTo();
    expect(await bookmarksPage.getBookmarkTitles().count()).toBe(0);
  });

  describe('with a started volume', () => {

    beforeAll(async () => {
      await libraryPage.navigateTo();
      await libraryPage.getSeries('Batgirl').click();
      await libraryPage.waitForSeries('Batgirl');
      await libraryPage.getListButton('Vol. 2008').click();
      await volumesPage.getMarkAsReadButton(0).click();
    });

    it('updates the read issue counter', async () => {
      await volumesPage.clickIssueMenuItem(0, 'View in library');
      await libraryPage.waitForSeries('Batgirl');
      expect(await libraryPage.getVolumeStats().getText())
        .toEqual([ '0 of 73 issues read', '1 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('shows that volume in the bookmarks', async () => {
      await bookmarksPage.navigateTo();
      expect(await bookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await bookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2\nVol. 2008' ]);
    });
  });

  describe('with a completed volume', () => {

    beforeAll(async () => {
      await bookmarksPage.clickBookmarkMenuItem(0, 'View in library');
      await libraryPage.waitForSeries('Batgirl');
      await libraryPage.clickVolumeMenuItem('Vol. 2009', 'Mark volume as read');
    });

    it('updates the read issue counter', async () => {
      expect(await libraryPage.getVolumeStats().getText())
        .toEqual([ '0 of 73 issues read', '1 of 6 issues read', '24 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('does not show that volume in the bookmarks', async () => {
      await bookmarksPage.navigateTo();
      expect(await bookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await bookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2\nVol. 2008' ]);
    });
  });
});

