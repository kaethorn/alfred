import { browser, by, element, ExpectedConditions, ElementFinder, WebElement } from 'protractor';

export class Page {

  static getTitleText () {
    return element.all(by.css('app-root ion-toolbar ion-title')).last().getText();
  }

  static async clickActionItem (target: ElementFinder, item: string) {
    const menuButton = target.element(by.css('ion-button.menu'));
    await this.scrollIntoView(menuButton);
    await menuButton.click();
    const button = element(by.cssContainingText('ion-item', item));
    await this.waitForElement(button);
    return element(by.cssContainingText('ion-item', item)).click();
  }

  /**
   * Waits for the given element to be clickable, especially
   * when it's already in the DOM due to being on another
   * page.
   */
  static waitForElement (target: ElementFinder, timeout = 5000) {
    return browser.wait(
      ExpectedConditions.and(
        ExpectedConditions.elementToBeClickable(target),
        ExpectedConditions.presenceOf(target)
    ), timeout);
  }

  static async waitForText (target: ElementFinder, text: string) {
    await this.waitForElement(target);
    return browser.wait(ExpectedConditions.textToBePresentInElement(target, text), 2500);
  }

  static async scrollIntoView (target: ElementFinder) {
    await browser.executeScript(
      'arguments[0].scrollIntoView({ block: "center", behavior: "instant" })',
      target.getWebElement());
    return browser.sleep(200);
  }

  // Work around broken by.deepCss
  static getShadowRoot (parentSelector: string, childSelector: string) {
    return browser.executeScript(
      'return document.querySelector(arguments[0]).shadowRoot.querySelector(arguments[1]);',
      parentSelector, childSelector);
  }

  static waitForToast (timeout = 1000) {
    return browser.wait(ExpectedConditions.presenceOf(element(by.css('ion-toast'))), timeout);
  }

  static async getToastMessage (timeout = 4000) {
    await this.waitForToast(timeout);
    return (await Page.getShadowRoot('ion-toast', '.toast-message') as WebElement).getText();
  }

  static async expectToastMessage (message: string) {
    expect(await Page.getToastMessage()).toEqual(message);
    return Page.waitForToastMessageGone();
  }

  static async waitForToastMessageGone (timeout = 6000) {
    return browser.wait(
      ExpectedConditions.not(
        ExpectedConditions.presenceOf(
          element(by.css('ion-toast')))), timeout);
  }
}
