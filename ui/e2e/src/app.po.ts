import { browser, by, element } from 'protractor';

export class AppPage {

  static navigateTo () {
    return browser.get('/');
  }

  static getTitleText () {
    return element.all(by.css('app-root ion-toolbar ion-title')).last().getText();
  }

  static getPublishersText () {
    return element(by.css('app-publishers')).getText();
  }

  static async clickMenuItem (item: string) {
    // TODO
  }
}
