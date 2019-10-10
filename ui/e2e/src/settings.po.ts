import { browser, by, element, ExpectedConditions, WebElement } from 'protractor';
import { Page } from './page.po';

export class SettingsPage {

  private page: Page;

  constructor () {
    this.page = new Page();
  }

  navigateTo () {
    return browser.get('/settings');
  }

  getComicsPathInput () {
    return element(by.css('input[Placeholder="Path"]'));
  }

  getSaveButton () {
    return element(by.cssContainingText('app-settings ion-button', 'SAVE'));
  }

  async getConfirmationMessage () {
    await browser.wait(ExpectedConditions.presenceOf(element(by.css('ion-toast'))), 500);
    return (await this.page.getShadowRoot('ion-toast', '.toast-message') as WebElement).getText();
  }

  private get progress () {
    return element(by.css('app-scanner .progress'));
  }

  waitForScanStart () {
    return browser.wait(ExpectedConditions.presenceOf(this.progress), 1000);
  }

  waitForScanEnd () {
    return browser.wait(ExpectedConditions.textToBePresentInElement(this.getStats().first(), '305'), 20000);
  }

  getScanButton () {
    return element(by.cssContainingText('app-scanner ion-button', 'SCAN'));
  }

  getScanProgress () {
    return this.progress.getText();
  }

  getScanErrors () {
    return element(by.css('app-scanner .errors'));
  }

  getStats () {
    return element.all(by.css('app-scanner ion-list.stats ion-item'));
  }

  getStatsText () {
    return this.getStats().getText();
  }

  async scan () {
    await this.navigateTo();
    expect(await this.getScanButton().isPresent()).toBe(true);
    await this.getScanButton().click();
    await this.waitForScanStart();
    expect(await this.getScanProgress())
      .toMatch(/Scanning\ file\ \d+\ of\ \d+\ at\ .*/);
    await this.waitForScanEnd();
    expect(await this.getScanErrors().isPresent()).toBe(false);
  }
}
