import { BookmarksPage } from './bookmarks.po';
import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { IssuesPage } from './issues.po';
import { MongoDBTools } from './mongodb.tools';
import { ProxySettings } from './proxy-settings';
import { AppPage } from './app.po';
import { Page } from './page.po';

describe('Sync', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  afterAll(async () => {
    await ProxySettings.set({ offline: false });
  });

  it('scans for comics', async () => {
    await SettingsPage.scan();
  });

  it('starts a volume', async () => {
    await LibraryPage.navigateTo();
    await LibraryPage.clickPublisher('DC Comics');
    await LibraryPage.clickSeries('Batgirl');
    await LibraryPage.clickVolumeListButton('Vol. 2008');
    await IssuesPage.wait();
    await IssuesPage.toggleMarkAsRead(0);
  });

  it('is cached implicitly', async () => {
    expect(await Page.getToastMessage()).toEqual('Volume cached.');
    expect(await IssuesPage.getIssues().count()).toBe(6);
    expect(await IssuesPage.getUnreadIssues().count()).toBe(5);
    expect(await IssuesPage.getSyncedIssues().count()).toBe(5);
    expect(await IssuesPage.getSyncedIssueNumbers()).toEqual(['1', '2', '3', '4', '5']);

    await BookmarksPage.navigateTo();
    expect(await BookmarksPage.getUnsyncButton(0).isPresent()).toBe(true);
    expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
    expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
  });

  it('caches again when reading an issue', async () => {
    await BookmarksPage.clickBookmarkMenuItem(0, 'View in volume');
    await IssuesPage.wait();
    await IssuesPage.toggleMarkAsRead(1);
    expect(await Page.getToastMessage()).toEqual('Volume cached.');
    expect(await IssuesPage.getUnreadIssues().count()).toBe(4);
    expect(await IssuesPage.getSyncedIssues().count()).toBe(5);
    expect(await IssuesPage.getSyncedIssueNumbers()).toEqual(['2', '3', '4', '5', '6']);

    await BookmarksPage.navigateTo();
    expect(await BookmarksPage.getUnsyncButton(0).isPresent()).toBe(true);
    expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
    expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #3' ]);
  });

  describe('when going offline with synced volumes', () => {

    beforeAll(async () => {
      await ProxySettings.set({ offline: true });
      await AppPage.clickMenuItem('Library');
      await AppPage.clickMenuItem('Bookmarks');
      await BookmarksPage.wait();
    });

    it('shows bookmarks', async () => {
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #3' ]);
    });
  });

  describe('without synced volumes', () => {

    beforeAll(async () => {
      await ProxySettings.set({ offline: false });
      await BookmarksPage.navigateTo();
    });

    it('unsyncs the volume', async () => {
      await BookmarksPage.getUnsyncButton(0).click();
      await BookmarksPage.waitForUnsync(0);
    });

    it('shows no booksmarks while offline', async () => {
      await ProxySettings.set({ offline: true });
      await AppPage.clickMenuItem('Library');
      await AppPage.clickMenuItem('Bookmarks');
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(0);
      expect(await BookmarksPage.getBookmarks().getText()).toContain('No comics found');
      expect(await BookmarksPage.getBookmarks().getText()).toContain('START NOW');
    });
  });
});
