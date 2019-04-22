import { browser, by, element } from 'protractor';

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
}
