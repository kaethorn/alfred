import { browser, by, element } from 'protractor';
import { Page } from './page.po';

export class LibraryPage {

  private static selectPublisher = 'app-publishers ion-item.publisher ion-button';
  private static selectPublisherSeries = 'app-publishers ion-item.serie ion-button';
  private static selectSeries = 'app-series ion-item.serie ion-button';
  private static selectVolumes = 'app-volumes ion-card.volume';

  static navigateTo () {
    return browser.get('/library/publishers');
  }

  static getAllPublishers () {
    return element.all(by.css(this.selectPublisher));
  }

  static getAllSeries () {
    return element.all(by.css(this.selectSeries));
  }

  static getAllVolumes () {
    return element.all(by.css(this.selectVolumes));
  }

  static getAllPublisherSeries () {
    return element.all(by.css(this.selectPublisherSeries));
  }

  static async clickPublisher (publisher: string) {
    await this.waitForPublishers();
    await element(by.cssContainingText(this.selectPublisher, publisher)).click();
  }

  static async clickSeries (series: string) {
    await this.waitForSeries();
    await element(by.cssContainingText(this.selectSeries, series)).click();
  }

  static async clickVolumeListButton (volume: string) {
    await this.waitForVolumes();
    await element(by.cssContainingText(this.selectVolumes, volume))
      .element(by.cssContainingText('ion-button', 'List')).click();
  }

  static getVolumeTitles () {
    return element.all(by.css(`${ this.selectVolumes } ion-card-title`));
  }

  static getVolumeSubtitles () {
    return element.all(by.css(`${ this.selectVolumes } ion-card-subtitle`));
  }

  static getVolumeStats () {
    return this.getVolumeSubtitles().getText();
  }

  static async expectVolumeStats (stats: string[]) {
    for (let index = 0; index < stats.length; index++) {
      await Page.waitForText(this.getVolumeSubtitles().get(index), stats[index]);
    }
    expect(await this.getVolumeStats()).toEqual(stats);
  }

  static getUnreadVolumes () {
    return this.getAllVolumes().filter((e, index) => {
      return e.element(by.css('ion-badge.read-badge')).isPresent().then(present => !present);
    });
  }

  static get markVolumeAsReadButton () {
    return element(by.cssContainingText('ion-button', 'Mark volume as read'));
  }

  static async clickVolumeMenuItem (volume: string, item: string) {
    await Page.clickActionItem(element(by.cssContainingText(this.selectVolumes, volume)), item);
  }

  static waitForPublishers () {
    return Page.waitForElement(this.getAllPublishers().first());
  }

  static waitForSeries () {
    return Page.waitForElement(this.getAllSeries().first());
  }

  static waitForVolumes () {
    return Page.waitForElement(this.getAllVolumes().first());
  }
}
