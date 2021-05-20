import { browser, by, element, promise, ElementFinder, ElementArrayFinder } from 'protractor';

import { Page } from './page.po';

export class BookmarksPage {

  public static async navigateTo(): Promise<void> {
    await browser.get('/bookmarks');
    await Page.waitForElement(this.getBookmarks());
    return Page.waitForLoadingGone();
  }

  public static getBookmarks(): ElementFinder {
    return element(by.css('app-bookmarks'));
  }

  public static getBookmarkItems(): ElementArrayFinder {
    return this.getBookmarks().all(by.css('.comic-tile'));
  }

  public static wait(): promise.Promise<void> {
    return Page.waitForElement(this.getBookmarkItems().first());
  }

  public static getBookmarkTitles(): ElementArrayFinder {
    return this.getBookmarkItems().all(by.css('ion-card-title'));
  }

  public static clickBookmarkMenuItem(volume: number, item: string): Promise<void> {
    return Page.clickActionItem(this.getBookmarkItems().get(volume), item);
  }

  public static getSyncButton(volume: number): ElementFinder {
    return element.all(by.css('ion-card.comic-tile')).get(volume)
      .element(by.css('ion-button.sync'));
  }

  public static getUnsyncButton(volume: number): ElementFinder {
    return element.all(by.css('ion-card.comic-tile')).get(volume)
      .element(by.css('ion-button.unsync'));
  }

  public static waitForSync(volume: number): promise.Promise<void> {
    return Page.waitForElement(this.getUnsyncButton(volume));
  }

  public static waitForUnsync(volume: number): promise.Promise<void> {
    return Page.waitForElement(this.getSyncButton(volume));
  }

  public static getIssueCover(volume: number): ElementFinder {
    return element.all(by.css('ion-card.comic-tile a.thumbnail')).get(volume);
  }

  public static async getPageNumberFromCover(volume: number): Promise<number> {
    const href = await this.getIssueCover(volume).getAttribute('href');
    const pageParts = href.match(/page=(\d+)/);
    if (pageParts && pageParts.length > 1) {
      return parseInt(pageParts[1], 10);
    }
    return -1;
  }
}
