import { by, element, protractor, browser, ElementFinder, ElementArrayFinder } from 'protractor';

import { Page } from './page.po';

export class ReaderPage {

  public static async openOverlay(offset = 0): Promise<void> {
    await Page.waitForLoadingGone();
    await Page.waitForElement(element.all(by.css('app-reader img')).get(offset));
    await element.all(by.css('app-reader img')).get(offset).click();
    return Page.waitForElement(element.all(by.css('app-reader .bottom ion-button')).first());
  }

  public static getNavigationButtons(): ElementArrayFinder {
    return element.all(by.css('app-reader .bottom ion-button'));
  }

  public static getOverlayNextButton(): ElementFinder {
    return this.getNavigationButtons().get(2);
  }

  public static getOverlayPreviousButton(): ElementFinder {
    return this.getNavigationButtons().get(1);
  }

  public static async exit(): Promise<void> {
    await browser
      .actions()
      .sendKeys(protractor.Key.ESCAPE)
      .perform();
    // Wait for Service Worker to figure out that the server is offline
    return browser.sleep(1000);
  }

  public static async getPageNumberFromUrl(): Promise<number> {
    const url = await browser.getCurrentUrl();
    const pageParts = url.match(/page=(\d+)/);
    if (pageParts && pageParts.length > 1) {
      return parseInt(pageParts[1], 10);
    }
    return -1;
  }

  public static async getIssueIdFromUrl(): Promise<string> {
    const url = await browser.getCurrentUrl();
    const parts = url.match(/read\/(.+)\?/);
    if (parts && parts.length > 1) {
      return parts[1];
    }
    return null;
  }
}
