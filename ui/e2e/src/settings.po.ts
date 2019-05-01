import { browser, by, element, ExpectedConditions } from 'protractor';

export class SettingsPage {
  navigateTo () {
    return browser.get('/settings');
  }

  getComicsPathInput () {
    return element(by.css('input[Placeholder="Path"]'));
  }

  getSaveButton () {
    return element(by.buttonText('SAVE'));
  }

  getError () {
    return element(by.css('mat-error'));
  }

  private get progress () {
    return element(by.css('app-scanner .progress'));
  }

  waitForScanStart () {
    return browser.wait(ExpectedConditions.presenceOf(this.progress), 200);
  }

  waitForScanEnd () {
    return browser.wait(ExpectedConditions.not(ExpectedConditions.presenceOf(this.progress)), 20000);
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
    return element.all(by.css('app-scanner ion-list.stats ion-item')).getText();
  }

  async scan () {
    await this.navigateTo();
    expect(await this.getScanButton().isPresent()).toBe(true);
    await this.getScanButton().click();
    await this.waitForScanStart();
    expect(await this.getScanProgress())
      .toMatch(/Scanning\ file\ \d+\ of\ \d+\ at\ .*/);
    expect(await this.getScanErrors().isPresent()).toBe(false);
    await this.waitForScanEnd();
  }
}
