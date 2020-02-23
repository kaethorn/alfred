import { browser, by, element, promise } from 'protractor';

export class AppPage {

  public static navigateTo(): promise.Promise<any> {
    return browser.get('/');
  }

  public static getPublishersText(): promise.Promise<string> {
    return element(by.css('app-publishers')).getText();
  }

  public static clickMenuItem(item: string): promise.Promise<void> {
    return element(by.cssContainingText('ion-menu ion-item ion-label', item)).click();
  }
}
