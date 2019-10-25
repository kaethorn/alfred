import { by, element, protractor, browser } from 'protractor';

export class ReaderPage {

  static getImage (offset = 0) {
    return element.all(by.css('app-reader img')).get(offset);
  }

  static getNavigationButtons () {
    return element.all(by.css('app-reader .bottom ion-button'));
  }

  static getOverlayNextButton () {
    return this.getNavigationButtons().get(2);
  }

  static exit () {
    return browser.actions().mouseMove(element.all(by.css('app-reader img')).first())
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
}
