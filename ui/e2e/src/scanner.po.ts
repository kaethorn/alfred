import { browser, by, element, ExpectedConditions } from 'protractor';

export class ScannerPage {

  private get progress () {
    return element(by.css('app-scanner .progress'));
  }

  navigateTo() {
    return browser.get('/library');
  }

  getScanButton() {
    return element(by.buttonText('Scan'));
  }

  waitForScanProgress() {
    return browser.wait(ExpectedConditions.presenceOf(this.progress), 500);
  }

  getScanProgress() {
    return this.progress.getText();
  }

  getScanErrors() {
    return element(by.css('app-scanner .errors'));
  }
}
