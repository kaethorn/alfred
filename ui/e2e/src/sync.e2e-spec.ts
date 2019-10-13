import { BookmarksPage } from './bookmarks.po';
import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { IssuesPage } from './issues.po';
import { MongoDBTools } from './mongodb.tools';
import { ProxySettings } from './proxy-settings';
import { browser } from 'protractor';

describe('Sync', () => {

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

  afterAll(async () => {
    await ProxySettings.set({ offline: false });
  });

  it('scans for comics', async () => {
    await settingsPage.scan();
  });

  it('starts a volume', async () => {
    await libraryPage.navigateTo();
    await libraryPage.clickPublisher('DC Comics');
    await libraryPage.clickSeries('Batgirl');
    await libraryPage.clickVolumeListButton('Vol. 2008');
    await issuesPage.wait();
    await issuesPage.toggleMarkAsRead(0);
  });

  it('shows bookmarks', async () => {
    await bookmarksPage.navigateTo();
    expect(await bookmarksPage.getBookmarkTitles().count()).toBe(1);
    expect(await bookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
  });

  describe('when going offline', () => {

    beforeAll(async () => {
      await ProxySettings.set({ offline: true });
    });

    it('shows no bookmarks', async () => {
      // FIXME use menu navigation:
      await libraryPage.navigateTo();
      await bookmarksPage.navigateTo();
      expect(await bookmarksPage.getBookmarkTitles().count()).toBe(0);
    });
  });
});
