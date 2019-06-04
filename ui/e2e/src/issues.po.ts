import { browser, by, element, ElementFinder } from 'protractor';
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
    return this.getIssues().filter((e, index) => {
      return e.element(by.css('ion-badge.read-badge')).isPresent().then(present => !present);
    });
  }

  wait () {
    return this.page.waitForElement(this.getIssues().first());
  }

  async toggleMarkAsRead (issue: number) {
    await this.page.scrollIntoView(this.getIssues().get(issue));
    await this.getIssues().get(issue)
      .element(by.css('ion-button.read-toggle')).click();
  }

  get markReadUntilHereButton () {
    return element(by.partialButtonText('Mark read until here'));
  }

  async clickButtonByLabel (issue: number, label: string) {
    await this.page.scrollIntoView(this.getIssues().get(issue));
    await this.getIssues().get(issue)
      .element(by.cssContainingText('ion-button', label)).click();
  }

  clickIssueMenuItem (issue: number, item: string) {
    return this.page.clickMenuItem(this.getIssues().get(issue), item);
  }
}
