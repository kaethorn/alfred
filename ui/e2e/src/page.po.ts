import { browser, by, element, ElementFinder, ExpectedConditions } from 'protractor';

export class Page {

  async clickMenuItem(target: ElementFinder, item: string) {
    await target.element(by.cssContainingText('mat-icon', 'more_vert')).click();

    const linkElement = element(by.partialButtonText(item));
    await browser.wait(ExpectedConditions.elementToBeClickable(linkElement), 200);
    await linkElement.click();
  }
}
