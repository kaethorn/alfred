import { browser, by, element } from 'protractor';

export class LibraryPage {

  navigateTo() {
    return browser.get('/library');
  }

  getComicPublishers() {
    return element(by.css('app-library .publishers')).getText();
  }
}
