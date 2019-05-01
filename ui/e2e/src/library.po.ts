import { browser, by, element, ExpectedConditions } from 'protractor';
import { Page } from './page.po';

export class LibraryPage {

  private page: Page;
  private selectPublisher = 'app-publishers ion-item.publisher ion-button';
  private selectSeries = 'app-series ion-item.serie ion-button';
  private selectVolumes = 'app-volumes ion-card.volume';

  constructor () {
    this.page = new Page();
  }

  navigateTo () {
    return browser.get('/library/publishers');
  }

  getAllPublishers () {
    return element.all(by.css(this.selectPublisher));
  }

  getAllSeries () {
    return element.all(by.css(this.selectSeries));
  }

  getAllVolumes () {
    return element.all(by.css(this.selectVolumes));
  }

  getPublisher (publisher: string) {
    return element(by.cssContainingText(this.selectPublisher, publisher));
  }

  getSeries (series: string) {
    return element(by.cssContainingText(this.selectSeries, series));
  }

  getVolumeTitles () {
    return element.all(by.css(`${ this.selectVolumes } ion-card-title`));
  }

  getVolumeStats () {
    return element.all(by.css(`${ this.selectVolumes } ion-card-subtitle`));
  }

  getUnreadVolumes () {
    return element.all(by.css(this.selectVolumes)).all(by.css('a.mat-badge-hidden'));
  }

  getVolumeListButton (volume: string) {
    return element(by.cssContainingText(this.selectVolumes, volume))
      .element(by.cssContainingText('ion-button', 'List'));
  }

  get markVolumeAsReadButton () {
    return element(by.cssContainingText('ion-button', 'Mark volume as read'));
  }

  async clickVolumeMenuItem (volume: string, item: string) {
    await this.page.clickMenuItem(element(by.cssContainingText(this.selectVolumes, volume)), item);
  }

  async waitForSeries () {
    await browser.wait(ExpectedConditions.elementToBeClickable(this.getAllSeries().first()), 200);
  }

  async waitForVolumes () {
    await browser.wait(ExpectedConditions.elementToBeClickable(this.getAllVolumes().first()), 200);
  }
}
