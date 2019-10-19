import { BookmarksPage } from './bookmarks.po';
import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { IssuesPage } from './issues.po';
import { MongoDBTools } from './mongodb.tools';
import { ProxySettings } from './proxy-settings';
import { AppPage } from './app.po';

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

  it('shows bookmarks', async () => {
    await BookmarksPage.navigateTo();
    expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
    expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
  });

  it('shows no cached issues', async () => {
    await BookmarksPage.clickBookmarkMenuItem(0, 'View in volume');
    await IssuesPage.wait();
    expect(await IssuesPage.getIssues().count()).toBe(6);
    expect(await IssuesPage.getUnreadIssues().count()).toBe(5);
    expect(await IssuesPage.getSyncedIssues().count()).toBe(0);
  });

  describe('when going offline without synced volumes', () => {

    beforeAll(async () => {
      await ProxySettings.set({ offline: true });
    });

    it('shows no bookmarks', async () => {
      await AppPage.clickMenuItem('Library');
      await AppPage.clickMenuItem('Bookmarks');
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(0);
      expect(await BookmarksPage.getBookmarks().getText()).toContain('No comics found');
      expect(await BookmarksPage.getBookmarks().getText()).toContain('START NOW');
    });
  });

  describe('when going offline with synced volumes', () => {

    beforeAll(async () => {
      await ProxySettings.set({ offline: false });
      await BookmarksPage.navigateTo();
    });

    it('shows bookmarks while online', async () => {
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
    });

    it('syncs volumes and goes offline', async () => {
      await BookmarksPage.getSyncButton(0).click();
      await BookmarksPage.waitForSync(0);
      expect(await BookmarksPage.getSyncedButton(0).isPresent()).toBe(true);
    });

    it('caches the previous, current and next three issues', async () => {
      await BookmarksPage.clickBookmarkMenuItem(0, 'View in volume');
      await IssuesPage.wait();
      expect(await IssuesPage.getIssues().count()).toBe(6);
      expect(await IssuesPage.getUnreadIssues().count()).toBe(5);
      expect(await IssuesPage.getSyncedIssues().count()).toBe(5);
    });

    it('still shows bookmarks when offline', async () => {
      await ProxySettings.set({ offline: true });
      await AppPage.clickMenuItem('Bookmarks');
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
    });
  });

  describe('removing synced volumes', () => {

    it('will result in empty bookmarks while offline', async () => {
      await BookmarksPage.getSyncedButton(0).click();
      await BookmarksPage.waitForUnsync(0);
      await AppPage.clickMenuItem('Library');
      await AppPage.clickMenuItem('Bookmarks');
      expect(await BookmarksPage.getBookmarkTitles().count()).toBe(0);
      expect(await BookmarksPage.getBookmarks().getText()).toContain('No comics found');
      expect(await BookmarksPage.getBookmarks().getText()).toContain('START NOW');
    });
  });
});
