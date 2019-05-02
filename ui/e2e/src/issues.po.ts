import { browser, by, element } from 'protractor';
import { Page } from './page.po';

export class IssuesPage {

  private page: Page;

  constructor () {
    this.page = new Page();
  }

  getIssues () {
    return element.all(by.css('app-issues ion-card'));
  }

  getUnreadIssues () {
    return this.getIssues().all(by.css('a.mat-badge-hidden'));
  }

  async clickMarkAsReadButton (issue: number) {
    await this.page.waitForElement(this.getIssues().first());
    await this.getIssues().get(issue)
      .element(by.css('ion-button.read-toggle')).click();
  }

  get markReadUntilHereButton () {
    return element(by.partialButtonText('Mark read until here'));
  }

  getViewInLibraryButton (issue: number) {
    return this.getIssues().get(issue)
      .element(by.cssContainingText('ion-button', 'View in library'));
  }
}
