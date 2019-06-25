import { browser, by, element } from 'protractor';

export class AppPage {
  navigateTo () {
    return browser.get('/');
  }

  getTitleText () {
    return element.all(by.css('app-root ion-toolbar ion-title')).last().getText();
  }

  getPublishersText () {
    return element(by.css('app-publishers')).getText();
  }
}
