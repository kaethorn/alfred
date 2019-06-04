import { browser, by, element, ExpectedConditions, ElementFinder } from 'protractor';

export class Page {

  async clickMenuItem (target: ElementFinder, item: string) {
    const menuButton = target.element(by.css('ion-icon[name="more"]'));
    await this.scrollIntoView(menuButton);
    await menuButton.click();
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
    await browser.wait(ExpectedConditions.textToBePresentInElement(target, text), 2500);
  }

  async scrollIntoView (target: ElementFinder) {
    await browser.executeScript('arguments[0].scrollIntoView(true)', target.getWebElement());
    await browser.sleep(200);
  }

  // Work around broken by.deepCss
  getShadowRoot (parentSelector: string, childSelector: string) {
    return browser.executeScript(
      'return document.querySelector(arguments[0]).shadowRoot.querySelector(arguments[1]);',
      parentSelector, childSelector);
  }
}
