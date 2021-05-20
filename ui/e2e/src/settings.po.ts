import { browser, by, element, ExpectedConditions, promise, ElementFinder, ElementArrayFinder } from 'protractor';

import { Page } from './page.po';

export class SettingsPage {

  public static async navigateTo(): Promise<void> {
    await browser.get('/settings');
    return Page.waitForElement(this.getComicsPathInput());
  }

  public static getComicsPathInput(): ElementFinder {
    return element(by.css('input[Placeholder="Path"]'));
  }

  public static getSaveButton(): ElementFinder {
    return element(by.cssContainingText('app-settings ion-button', 'Save'));
  }

  private static get progress(): ElementFinder {
    return element(by.css('app-scanner .progress.scanning'));
  }

  public static waitForScanStart(): promise.Promise<void> {
    return browser.wait(ExpectedConditions.visibilityOf(this.progress), 1000);
  }

  public static waitForScanEnd(): promise.Promise<void> {
    return browser.wait(ExpectedConditions.textToBePresentInElement(this.getStats().get(5), '305'), 20000);
  }

  public static getScanButton(): ElementFinder {
    return element(by.cssContainingText('app-scanner ion-button', 'Scan'));
  }

  public static getClearButton(): ElementFinder {
    return element(by.cssContainingText('app-scanner ion-button', 'Clear'));
  }

  public static getScanProgress(): promise.Promise<string> {
    return this.progress.getText();
  }

  public static getScanErrors(): ElementFinder {
    return element(by.css('app-scanner .errors'));
  }

  public static getStats(): ElementArrayFinder {
    return element.all(by.css('app-scanner ion-list.stats ion-item'));
  }

  public static getStatsText(): promise.Promise<string> {
    return this.getStats().getText();
  }

  public static async scan(): Promise<void> {
    await this.navigateTo();
    expect(await this.getScanButton().isPresent()).toBe(true);

    // Clear IndexedDB
    const canClear: boolean = await this.getClearButton().getAttribute('disabled').then(attr =>
      attr !== 'true'
    );
    if (canClear) {
      await this.getClearButton().click();
    }

    // Scan and expect progress
    await this.getScanButton().click();
    await this.waitForScanStart();
    expect(await this.getScanProgress())
      .toMatch(/Scanning file \d+ of \d+ at .*/);
    await this.waitForScanEnd();
    expect(await this.getScanErrors().isPresent()).toBe(false);
  }
}
