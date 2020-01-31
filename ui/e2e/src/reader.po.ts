import { by, element, protractor, browser } from 'protractor';
import { Page } from './page.po';

export class ReaderPage {

  static async openOverlay (offset = 0) {
    await Page.waitForElement(element.all(by.css('app-reader img')).get(offset));
    await element.all(by.css('app-reader img')).get(offset).click();
    await Page.waitForElement(element.all(by.css('app-reader .bottom ion-button')).first());
  }

  static getNavigationButtons () {
    return element.all(by.css('app-reader .bottom ion-button'));
  }

  static getOverlayNextButton () {
    return this.getNavigationButtons().get(2);
  }

  static getOverlayPreviousButton () {
    return this.getNavigationButtons().get(1);
  }

  static exit () {
    return browser.actions().mouseMove(element.all(by.css('app-reader img')).last())
      .sendKeys(protractor.Key.ESCAPE)
      .perform();
  }

  static async getPageNumberFromUrl (): Promise<number> {
    const url = await browser.getCurrentUrl();
    const pageParts = url.match(/page=(\d+)/);
    if (pageParts && pageParts.length > 1) {
      return parseInt(pageParts[1], 10);
    }
    return -1;
  }

  static async getIssueIdFromUrl (): Promise<string> {
    const url = await browser.getCurrentUrl();
    const parts = url.match(/read\/(.+)\?/);
    if (parts && parts.length > 1) {
      return parts[1];
    }
    return null;
  }
}
