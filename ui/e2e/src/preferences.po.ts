import { browser, by, element } from 'protractor';

export class PreferencesPage {
  navigateTo() {
    return browser.get('/preferences');
  }

  getComicsPathInput() {
    return element(by.css('input[ng-reflect-name="comics.path"]'));
  }

  getSaveButton() {
    return element(by.buttonText('Save'));
  }
}
