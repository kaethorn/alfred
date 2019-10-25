import { BookmarksPage } from './bookmarks.po';
import { LibraryPage } from './library.po';
import { SettingsPage } from './settings.po';
import { IssuesPage } from './issues.po';
import { MongoDBTools } from './mongodb.tools';
import { ProxySettings } from './proxy-settings';
import { AppPage } from './app.po';
import { ReaderPage } from './reader.po';
import { browser } from 'protractor';
import { Page } from './page.po';

describe('Reader Component', () => {

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

  it('shows cached bookmarks', async () => {
    expect(await Page.getToastMessage(4000)).toEqual('Volume cached.');
    await BookmarksPage.navigateTo();
    expect(await BookmarksPage.getSyncedButton(0).isPresent()).toBe(true);
    expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
    expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
  });

  it('completes one entire issue', async () => {
    await BookmarksPage.clickBookmarkMenuItem(0, 'View in volume');
    await IssuesPage.wait();
    await IssuesPage.toggleMarkAsRead(1);
  });

  it('goes offline', async () => {
    await ProxySettings.set({ offline: true });
  });

  describe('when reading', () => {

    it('starts on the first page', async () => {
      await AppPage.clickMenuItem('Bookmarks');
      await BookmarksPage.getIssueCover(0).click();
      expect(await browser.getCurrentUrl()).toContain('page=0');
    });

    it('continues but does not finish the issue', async () => {
      // FIXME why wait?
      await browser.sleep(500);
      await ReaderPage.getImage().click();
      await ReaderPage.getOverlayNextButton().click();
      expect(await browser.getCurrentUrl()).toContain('page=1');
      await ReaderPage.getImage().click();
      await ReaderPage.getOverlayNextButton().click();
      // Depending on the browser width, side by side mode might trigger:
      expect(await ReaderPage.getPageNumberFromUrl()).toBeGreaterThanOrEqual(2);
    });

    it('quits the reader and returns to the bookmarks', async () => {
      await ReaderPage.exit();
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #3' ]);
    });

    it('remembers the last read page', async () => {
      // Wait for Service Worker to figure out that the server is offline
      await browser.sleep(250);
      expect(await BookmarksPage.getPageNumberFromCover(0)).toBeGreaterThanOrEqual(2);
    });

    it('continues reading at the last read page', async () => {
      await BookmarksPage.getIssueCover(0).click();
      expect(await ReaderPage.getPageNumberFromUrl()).toBeGreaterThanOrEqual(2);
    });
  });

  it('goes online', async () => {
    await ProxySettings.set({ offline: false });
  });

  it('reloads and navigates to the bookmarks', async () => {
    await BookmarksPage.navigateTo();
    expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #3' ]);
  });

  it('refreshes from server and receives offline updates', async () => {
    await browser.sleep(250);
    const nextPage = (await BookmarksPage.getIssueCover(0).getAttribute('href')).split('page=')[1][0];
    expect(nextPage).toBeGreaterThanOrEqual(2);
  });
});
