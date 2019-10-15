import { browser, by, element } from 'protractor';
import { Page } from './page.po';

export class BookmarksPage {

  static navigateTo () {
    return browser.get('/bookmarks');
  }

  static getBookmarks () {
    return element(by.css('app-bookmarks'));
  }

  static getBookmarkItems () {
    return this.getBookmarks().all(by.css('.comic-tile'));
  }

  static getBookmarkTitles () {
    return this.getBookmarkItems().all(by.css('ion-card-title'));
  }

  static clickBookmarkMenuItem (volume: number, item: string) {
    return Page.clickActionItem(this.getBookmarkItems().get(volume), item);
  }

  static getSyncButton (volume: number) {
    return element.all(by.css('ion-card.comic-tile')).get(volume)
      .element(by.cssContainingText('ion-button', 'Sync'));
  }

  static getSyncedButton (volume: number) {
    return element.all(by.css('ion-card.comic-tile')).get(volume)
      .element(by.cssContainingText('ion-button', 'Synced'));
  }

  static waitForSync (volume: number) {
    return Page.waitForElement(this.getSyncedButton(0));
  }
}
