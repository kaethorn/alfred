import { browser, by, element } from 'protractor';

export class AppPage {
  navigateTo () {
    return browser.get('/');
  }

  getTitleText () {
    return element(by.css('app-root ion-toolbar ion-title')).getText();
  }

  getPublishersText () {
    return element(by.css('app-publishers')).getText();
  }
}
