import { browser, by, element } from 'protractor';
import { Page } from './page.po';

export class BookmarksPage {

  static async navigateTo () {
    await browser.get('/bookmarks');
    return Page.waitForElement(this.getBookmarks());
  }

  static getBookmarks () {
    return element(by.css('app-bookmarks'));
  }

  static getBookmarkItems () {
    return this.getBookmarks().all(by.css('.comic-tile'));
  }

  static wait () {
    return Page.waitForElement(this.getBookmarkItems().first());
  }

  static getBookmarkTitles () {
    return this.getBookmarkItems().all(by.css('ion-card-title'));
  }

  static clickBookmarkMenuItem (volume: number, item: string) {
    return Page.clickActionItem(this.getBookmarkItems().get(volume), item);
  }

  static getSyncButton (volume: number) {
    return element.all(by.css('ion-card.comic-tile')).get(volume)
      .element(by.css('ion-button.sync'));
  }

  static getUnsyncButton (volume: number) {
    return element.all(by.css('ion-card.comic-tile')).get(volume)
      .element(by.css('ion-button.unsync'));
  }

  static waitForSync (volume: number) {
    return Page.waitForElement(this.getUnsyncButton(volume));
  }

  static waitForUnsync (volume: number) {
    return Page.waitForElement(this.getSyncButton(volume));
  }

  static getIssueCover (volume: number) {
    return element.all(by.css('ion-card.comic-tile a.thumbnail')).get(volume);
  }

  static async getPageNumberFromCover (volume: number): Promise<number> {
    const href = await this.getIssueCover(volume).getAttribute('href');
    const pageParts = href.match(/page=(\d+)/);
    if (pageParts && pageParts.length > 1) {
      return parseInt(pageParts[1], 10);
    }
    return -1;
  }
}
