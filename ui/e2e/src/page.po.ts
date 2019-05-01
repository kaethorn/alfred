import { browser, by, element, ElementFinder } from 'protractor';

export class Page {

  async clickMenuItem (target: ElementFinder, item: string) {
    await target.element(by.cssContainingText('ion-icon', 'more')).click();

    await browser.sleep(500);
    await element(by.partialButtonText(item)).click();
  }
}
