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
    expect(await Page.getToastMessage()).toEqual('Volume cached.');
    await BookmarksPage.navigateTo();
    expect(await BookmarksPage.getUnsyncButton(0).isPresent()).toBe(true);
    expect(await BookmarksPage.getBookmarkTitles().count()).toBe(1);
    expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #2' ]);
  });

  it('completes one entire issue', async () => {
    await BookmarksPage.clickBookmarkMenuItem(0, 'View in volume');
    await IssuesPage.wait();
    await IssuesPage.toggleMarkAsRead(1);
  });

  describe('reading while offline', () => {

    it('goes offline', async () => {
      await ProxySettings.set({ offline: true });
    });

    it('starts on the first page', async () => {
      await AppPage.clickMenuItem('Bookmarks');
      await BookmarksPage.wait();
      await BookmarksPage.getIssueCover(0).click();
      expect(await ReaderPage.getPageNumberFromUrl()).toBe(0);
    });

    it('continues but does not finish the issue', async () => {
      // Wait for caching to fail
      await browser.sleep(500);
      await ReaderPage.getImage().click();
      await ReaderPage.getOverlayNextButton().click();
      expect(await ReaderPage.getPageNumberFromUrl()).toBe(1);
    });

    it('quits the reader and returns to the bookmarks', async () => {
      await ReaderPage.exit();
      // Wait for Service Worker to figure out that the server is offline
      await browser.sleep(1000);
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #3' ]);
    });

    it('remembers the last read page', async () => {
      expect(await BookmarksPage.getPageNumberFromCover(0)).toBe(2);
    });

    it('continues reading at the last read page', async () => {
      await BookmarksPage.getIssueCover(0).click();
      expect(await ReaderPage.getPageNumberFromUrl()).toBe(2);
    });
  });

  describe('reading while online', () => {

    it('goes online', async () => {
      await ProxySettings.set({ offline: false });
      await BookmarksPage.navigateTo();
      await browser.sleep(250);
    });

    it('reloads and navigates to the bookmarks', async () => {
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #3' ]);
    });

    it('refreshes from server and receives offline updates', async () => {
      expect(await BookmarksPage.getPageNumberFromCover(0)).toBe(2);
    });
  });

  describe('completing an issue', async () => {

    it('resumes reading and caches agagin', async () => {
      await BookmarksPage.getIssueCover(0).click();
      expect(await ReaderPage.getPageNumberFromUrl()).toBe(2);
      expect(await Page.getToastMessage()).toEqual('Volume cached.');
      await Page.waitForToastMessageGone();
      expect(await ReaderPage.getPageNumberFromUrl()).toBe(1);
    });

    it('reads until the last page', async () => {
      await ReaderPage.getImage().click();
      await ReaderPage.getOverlayNextButton().click();
      expect(await ReaderPage.getPageNumberFromUrl()).toBe(3);
    });

    it('opens the next issue', async () => {
      const previousId = await ReaderPage.getIssueIdFromUrl();
      await ReaderPage.getImage().click();
      await ReaderPage.getOverlayNextButton().click();

      const nextId = await ReaderPage.getIssueIdFromUrl();
      expect(previousId).not.toEqual(nextId);
      expect(await Page.getToastMessage()).toEqual('Opening next issue of Batgirl (2008).');
      await Page.waitForToastMessageGone();
      expect(await ReaderPage.getPageNumberFromUrl()).toBe(0);
      await browser.sleep(250);
    });

    it('marks the previous issue as read on the bookmarks page', async () => {
      await ReaderPage.exit();
      await browser.sleep(1000);
      expect(await BookmarksPage.getBookmarkTitles().getText()).toEqual([ 'Batgirl #4' ]);
    });

    it('marks the previous issue as read on the issues page', async () => {
      await BookmarksPage.clickBookmarkMenuItem(0, 'View in volume');
      await IssuesPage.wait();
      const unreadIssues = await IssuesPage.getUnreadIssues().getText();
      expect(unreadIssues.length).toBe(3);
      expect(unreadIssues[0]).toContain('#4');
      expect(unreadIssues[1]).toContain('#5');
      expect(unreadIssues[2]).toContain('#6');
    });
  });
});
