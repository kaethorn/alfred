import { browser, by, element } from 'protractor';
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

  async clickPublisher (publisher: string) {
    await this.page.waitForElement(this.getAllPublishers().first());
    await element(by.cssContainingText(this.selectPublisher, publisher)).click();
  }

  async clickSeries (series: string) {
    await this.page.waitForElement(this.getAllSeries().first());
    await element(by.cssContainingText(this.selectSeries, series)).click();
  }

  async clickVolumeListButton (volume: string) {
    await this.page.waitForElement(this.getAllVolumes().first());
    await element(by.cssContainingText(this.selectVolumes, volume))
      .element(by.cssContainingText('ion-button', 'List')).click();
  }

  getVolumeTitles () {
    return element.all(by.css(`${ this.selectVolumes } ion-card-title`));
  }

  getVolumeSubtitles () {
    return element.all(by.css(`${ this.selectVolumes } ion-card-subtitle`));
  }

  getVolumeStats () {
    return this.getVolumeSubtitles().getText();
  }

  async expectVolumeStats (stats: string[]) {
    for (let index = 0; index < stats.length; index++) {
      await this.page.waitForText(this.getVolumeSubtitles().get(index), stats[index]);
    }
    expect(await this.getVolumeStats()).toEqual(stats);
  }

  getUnreadVolumes () {
    return element.all(by.css(this.selectVolumes)).all(by.css('a.mat-badge-hidden'));
  }

  get markVolumeAsReadButton () {
    return element(by.cssContainingText('ion-button', 'Mark volume as read'));
  }

  async clickVolumeMenuItem (volume: string, item: string) {
    await this.page.clickMenuItem(element(by.cssContainingText(this.selectVolumes, volume)), item);
  }
}
