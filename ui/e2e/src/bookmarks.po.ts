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
    return element(by.css('app-bookmarks'));
  }

  getBookmarkItems () {
    return this.getBookmarks().all(by.css('.comic-tile'));
  }

  getBookmarkTitles () {
    return this.getBookmarkItems().all(by.css('ion-card-title'));
  }

  async clickBookmarkMenuItem (volume: number, item: string) {
    await this.page.clickMenuItem(this.getBookmarkItems().get(volume), item);
  }
}
