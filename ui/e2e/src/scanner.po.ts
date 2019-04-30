import { browser, by, element, ExpectedConditions } from 'protractor';

export class ScannerPage {

  private get progress () {
    return element(by.css('app-scanner .progress'));
  }

  navigateTo () {
    return browser.get('/library');
  }

  waitForScanStart () {
    return browser.wait(ExpectedConditions.presenceOf(this.progress), 200);
  }

  waitForScanEnd () {
    return browser.wait(ExpectedConditions.not(ExpectedConditions.presenceOf(this.progress)), 10000);
  }

  getScanButton () {
    return element(by.buttonText('SCAN'));
  }

  getScanProgress () {
    return this.progress.getText();
  }

  getScanErrors () {
    return element(by.css('app-scanner .errors'));
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
