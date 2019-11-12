import { BookmarksPage } from './bookmarks.po';
import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { IssuesPage } from './issues.po';
import { MongoDBTools } from './mongodb.tools';

describe('BookmarksComponent', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  it('scans for comics', async () => {
    await SettingsPage.scan();
  });

  it('shows no bookmarks by default', async () => {
    await BookmarksPage.navigateTo();
    expect(await BookmarksPage.getBookmarkTitles().count()).toBe(0);
    expect(await BookmarksPage.getBookmarks().getText()).toContain('No comics found');
    expect(await BookmarksPage.getBookmarks().getText()).toContain('START NOW');
  });

  describe('with a started volume', () => {

    beforeAll(async () => {
      await LibraryPage.navigateTo();
      await LibraryPage.clickPublisher('DC Comics');
      await LibraryPage.clickSeries('Batgirl');
      await LibraryPage.clickVolumeListButton('Vol. 2008');
      await IssuesPage.wait();
      await IssuesPage.toggleMarkAsRead(0);
    });

    it('updates the read issue counter', async () => {
      await IssuesPage.clickIssueMenuItem(3, 'View in library');
      await LibraryPage.expectVolumeStats(
        [ '0 of 73 issues read', '1 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('shows that volume in the bookmarks', async () => {
      await BookmarksPage.navigateTo();
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
    });
  });

  describe('with a completed volume', () => {

    it('opens the volumes library page', async () => {
      await BookmarksPage.clickBookmarkMenuItem(0, 'View in library');
      await LibraryPage.waitForVolumes();
    });

    it('marks the volume as read', async () => {
      await LibraryPage.clickVolumeMenuItem('Vol. 2009', 'Mark volume as read');
    });

    it('updates the read issue counter', async () => {
      await LibraryPage.expectVolumeStats(
        [ '0 of 73 issues read', '1 of 6 issues read', '24 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
    });

    it('does not show that volume in the bookmarks', async () => {
      await BookmarksPage.navigateTo();
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
    });
  });
});
