import { browser, by, element, ExpectedConditions } from 'protractor';

export class LibraryPage {

  private selectSeries = 'app-library .series mat-panel-title';
  private selectVolume = 'mat-expansion-panel.mat-expanded app-volume';

  navigateTo() {
    return browser.get('/library');
  }

  getComicPublishers() {
    return element.all(by.css('app-library .publisher h3')).getText();
  }

  getComicSeries() {
    return element.all(by.css(this.selectSeries)).getText();
  }

  getComicVolume(series: string) {
    return element(by.cssContainingText(this.selectSeries, series));
  }

  getComicVolumes() {
    return element.all(by.css(`${ this.selectVolume } mat-card-title`)).getText();
  }

  getComicVolumeStats() {
    return element.all(by.css(`${ this.selectVolume } mat-card-subtitle`)).getText();
  }

  get markVolumeAsReadButton() {
    return element(by.partialButtonText('Mark volume as read'));
  }

  async clickVolumeMenuItem(volume: string, item: string) {
    await element(by.cssContainingText(this.selectVolume, volume))
      .element(by.cssContainingText('mat-icon', 'more_vert')).click();

    await browser.wait(ExpectedConditions.elementToBeClickable(this.markVolumeAsReadButton), 200);

    await element(by.partialButtonText(item)).click();
  }
}
