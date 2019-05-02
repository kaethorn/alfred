import { browser, by, element, ExpectedConditions, ElementFinder } from 'protractor';

export class Page {

  async clickMenuItem (target: ElementFinder, item: string) {
    await target.element(by.css('ion-icon[name="more"]')).click();
    const button = element(by.cssContainingText('ion-item', item));
    await this.waitForElement(button);
    await element(by.cssContainingText('ion-item', item)).click();
  }

  /**
   * Waits for the given element to be clickable, especially
   * when it's already in the DOM due to being on another
   * page.
   */
  async waitForElement (target: ElementFinder) {
    await browser.wait(ExpectedConditions.elementToBeClickable(target), 2000);
  }

  async waitForText (target: ElementFinder, text: string) {
    await this.waitForElement(target);
    await browser.wait(ExpectedConditions.textToBePresentInElement(target, text), 2000);
  }
}
