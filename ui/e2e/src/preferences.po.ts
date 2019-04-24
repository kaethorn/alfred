import { browser, by, element } from 'protractor';

export class PreferencesPage {
  navigateTo() {
    return browser.get('/preferences');
  }

  getComicsPathInput() {
    return element(by.css('input[Placeholder="Path"]'));
  }

  getSaveButton() {
    return element(by.buttonText('SAVE'));
  }

  getError() {
    return element(by.css('mat-error'));
  }
}
