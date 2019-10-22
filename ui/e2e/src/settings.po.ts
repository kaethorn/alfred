import { browser, by, element, ExpectedConditions } from 'protractor';

export class SettingsPage {

  static navigateTo () {
    return browser.get('/settings');
  }

  static getComicsPathInput () {
    return element(by.css('input[Placeholder="Path"]'));
  }

  static getSaveButton () {
    return element(by.cssContainingText('app-settings ion-button', 'SAVE'));
  }

  private static get progress () {
    return element(by.css('app-scanner .progress'));
  }

  static waitForScanStart () {
    return browser.wait(ExpectedConditions.presenceOf(this.progress), 1000);
  }

  static waitForScanEnd () {
    return browser.wait(ExpectedConditions.textToBePresentInElement(this.getStats().first(), '305'), 20000);
  }

  static getScanButton () {
    return element(by.cssContainingText('app-scanner ion-button', 'SCAN'));
  }

  static getScanProgress () {
    return this.progress.getText();
  }

  static getScanErrors () {
    return element(by.css('app-scanner .errors'));
  }

  static getStats () {
    return element.all(by.css('app-scanner ion-list.stats ion-item'));
  }

  static getStatsText () {
    return this.getStats().getText();
  }

  static async scan () {
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
