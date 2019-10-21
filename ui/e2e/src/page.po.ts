import { browser, by, element, ExpectedConditions, ElementFinder, WebElement } from 'protractor';

export class Page {

  static async clickActionItem (target: ElementFinder, item: string) {
    const menuButton = target.element(by.css('ion-button.menu'));
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
  static async waitForElement (target: ElementFinder, timeout = 4000) {
    await browser.wait(ExpectedConditions.elementToBeClickable(target), timeout);
  }

  static async waitForText (target: ElementFinder, text: string) {
    await this.waitForElement(target);
    await browser.wait(ExpectedConditions.textToBePresentInElement(target, text), 2500);
  }

  static async scrollIntoView (target: ElementFinder) {
    await browser.executeScript('arguments[0].scrollIntoView(true)', target.getWebElement());
    await browser.sleep(200);
  }

  // Work around broken by.deepCss
  static getShadowRoot (parentSelector: string, childSelector: string) {
    return browser.executeScript(
      'return document.querySelector(arguments[0]).shadowRoot.querySelector(arguments[1]);',
      parentSelector, childSelector);
  }

  static waitForToast (timeout = 500) {
    return browser.wait(ExpectedConditions.presenceOf(element(by.css('ion-toast'))), timeout);
  }

  static async getToastMessage (timeout = 500) {
    await this.waitForToast(timeout);
    return (await Page.getShadowRoot('ion-toast', '.toast-message') as WebElement).getText();
  }
}
