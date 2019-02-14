import { browser, by, element, ExpectedConditions } from 'protractor';

export class ScannerPage {

  private get progress () {
    return element(by.css('app-scanner .progress'));
  }

  navigateTo() {
    return browser.get('/library');
  }

  waitForScanStart() {
    return browser.wait(ExpectedConditions.presenceOf(this.progress), 500);
  }

  waitForScanEnd() {
    return browser.wait(ExpectedConditions.not(ExpectedConditions.presenceOf(this.progress)), 3000);
  }

  getScanButton() {
    return element(by.buttonText('SCAN'));
  }

  getScanProgress() {
    return this.progress.getText();
  }

  getScanErrors() {
    return element(by.css('app-scanner .errors'));
  }
}
