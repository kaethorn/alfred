import { browser, by, element, ExpectedConditions } from 'protractor';
import { Page } from './page.po';

export class LibraryPage {

  private page: Page;
  private selectSeries = 'app-library .series mat-panel-title';
  private selectVolume = 'mat-expansion-panel.mat-expanded app-volume';

  constructor () {
    this.page = new Page();
  }

  navigateTo () {
    return browser.get('/library');
  }

  getAllPublishers () {
    return element.all(by.css('app-library .publisher h3'));
  }

  getAllSeries () {
    return element.all(by.css(this.selectSeries));
  }

  getSeries (series: string) {
    return element(by.cssContainingText(this.selectSeries, series));
  }

  async waitForSeries (series: string) {
    await browser.wait(ExpectedConditions.elementToBeClickable(this.getSeries(series)), 200);
  }

  getVolumeTitles () {
    return element.all(by.css(`${ this.selectVolume } mat-card-title`));
  }

  getVolumeStats () {
    return element.all(by.css(`${ this.selectVolume } mat-card-subtitle`));
  }

  getUnreadVolumes () {
    return element.all(by.css(this.selectVolume)).all(by.css('a.mat-badge-hidden'));
  }

  getListButton (volume: string) {
    return element(by.cssContainingText(this.selectVolume, volume))
      .element(by.buttonText('LIST'));
  }

  get markVolumeAsReadButton () {
    return element(by.partialButtonText('Mark volume as read'));
  }

  async clickVolumeMenuItem (volume: string, item: string) {
    await this.page.clickMenuItem(element(by.cssContainingText(this.selectVolume, volume)), item);
  }
}
