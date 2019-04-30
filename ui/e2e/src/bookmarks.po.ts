import { browser, by, element } from 'protractor';
import { Page } from './page.po';

export class BookmarksPage {

  private page: Page;

  constructor () {
    this.page = new Page();
  }

  navigateTo () {
    return browser.get('/bookmarks');
  }

  getBookmarks () {
    return element.all(by.css('app-bookmarks .comic-tile'));
  }

  getBookmarkTitles () {
    return this.getBookmarks().all(by.css('mat-card-header'));
  }

  async clickBookmarkMenuItem (volume: number, item: string) {
    await this.page.clickMenuItem(this.getBookmarks().get(volume), item);
  }
}
