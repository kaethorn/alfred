import { BookmarksPage } from './bookmarks.po';
import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { IssuesPage } from './issues.po';
import { MongoDBTools } from './mongodb.tools';

describe('BookmarksComponent', () => {

  let bookmarksPage: BookmarksPage;
  let issuesPage: IssuesPage;
  let settingsPage: SettingsPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    bookmarksPage = new BookmarksPage();
    issuesPage = new IssuesPage();
    settingsPage = new SettingsPage();
    libraryPage = new LibraryPage();
  });

  it('scans for comics', async () => {
    await settingsPage.scan();
  });

  it('shows no bookmarks by default', async () => {
    await bookmarksPage.navigateTo();
    expect(await bookmarksPage.getBookmarkTitles().count()).toBe(0);
    expect(await bookmarksPage.getBookmarks().getText()).toContain('No comics found');
    expect(await bookmarksPage.getBookmarks().getText()).toContain('START NOW');
  });

  describe('with a started volume', () => {

    beforeAll(async () => {
      await libraryPage.navigateTo();
      await libraryPage.clickPublisher('DC Comics');
      await libraryPage.clickSeries('Batgirl');
      await libraryPage.clickVolumeListButton('Vol. 2008');
      await issuesPage.wait();
      await issuesPage.toggleMarkAsRead(0);
    });

    it('updates the read issue counter', async () => {
      await issuesPage.clickButtonByLabel(3, 'View in library');
      await libraryPage.expectVolumeStats(
        [ '0 of 73 issues read', '1 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('shows that volume in the bookmarks', async () => {
      await bookmarksPage.navigateTo();
      expect(await bookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await bookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
    });
  });

  describe('with a completed volume', () => {

    beforeAll(async () => {
      await bookmarksPage.clickBookmarkMenuItem(0, 'View in library');
      await libraryPage.clickVolumeMenuItem('Vol. 2009', 'Mark volume as read');
    });

    it('updates the read issue counter', async () => {
      await libraryPage.expectVolumeStats(
        [ '0 of 73 issues read', '1 of 6 issues read', '24 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('does not show that volume in the bookmarks', async () => {
      await bookmarksPage.navigateTo();
      expect(await bookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await bookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
    });
  });
});

