import { browser, by, element, ElementFinder, promise, ElementArrayFinder } from 'protractor';

import { Page } from 'e2e/src/page.po';

export class LibraryPage {

  private static selectPublisher = 'app-publishers ion-item.publisher ion-button';
  private static selectPublisherSeries = 'app-publishers ion-item.serie ion-button';
  private static selectSeries = 'app-series ion-item.serie ion-button';
  private static selectVolumes = 'app-volumes ion-card.volume';

  public static async navigateTo(): Promise<void> {
    await browser.get('/library/publishers');
    return Page.waitForLoadingGone();
  }

  public static getAllPublishers(): ElementArrayFinder {
    return element.all(by.css(this.selectPublisher));
  }

  public static getAllSeries(): ElementArrayFinder {
    return element.all(by.css(this.selectSeries));
  }

  public static getAllVolumes(): ElementArrayFinder {
    return element.all(by.css(this.selectVolumes));
  }

  public static getAllPublisherSeries(): ElementArrayFinder {
    return element.all(by.css(this.selectPublisherSeries));
  }

  public static async clickPublisher(publisher: string): Promise<void> {
    await this.waitForPublishers();
    return element(by.cssContainingText(this.selectPublisher, publisher)).click();
  }

  public static async clickSeries(series: string): Promise<void> {
    await this.waitForSeries();
    return element(by.cssContainingText(this.selectSeries, series)).click();
  }

  public static async clickVolumeListButton(volume: string): Promise<void> {
    await this.waitForVolumes();
    return element(by.cssContainingText(this.selectVolumes, volume))
      .element(by.cssContainingText('ion-button', 'List')).click();
  }

  public static getVolumeTitles(): ElementArrayFinder {
    return element.all(by.css(`${ this.selectVolumes } ion-card-title`));
  }

  public static getVolumeSubtitles(): ElementArrayFinder {
    return element.all(by.css(`${ this.selectVolumes } ion-card-subtitle`));
  }

  public static getVolumeStats(): promise.Promise<string> {
    return this.getVolumeSubtitles().getText();
  }

  public static async expectVolumeStats(stats: string[]): Promise<void> {
    for (let index = 0; index < stats.length; index++) {
      await Page.waitForText(this.getVolumeSubtitles().get(index), stats[index]);
    }
    expect(await this.getVolumeStats()).toEqual(stats);
  }

  public static getUnreadVolumes(): ElementArrayFinder {
    return this.getAllVolumes().filter(e => e.element(by.css('ion-badge.read-badge')).isPresent().then(present => !present));
  }

  public static get markVolumeAsReadButton(): ElementFinder {
    return element(by.cssContainingText('ion-button', 'Mark volume as read'));
  }

  public static async clickVolumeMenuItem(volume: string, item: string): Promise<void> {
    await Page.clickActionItem(element(by.cssContainingText(this.selectVolumes, volume)), item);
  }

  public static waitForPublishers(): promise.Promise<void> {
    return Page.waitForElement(this.getAllPublishers().first());
  }

  public static waitForSeries(): promise.Promise<void> {
    return Page.waitForElement(this.getAllSeries().first());
  }

  public static waitForVolumes(): promise.Promise<void> {
    return Page.waitForElement(this.getAllVolumes().first(), 7000);
  }
}
