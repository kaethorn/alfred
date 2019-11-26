import { by, element, browser } from 'protractor';
import { Page } from './page.po';

export class IssuesPage {

  static getIssues () {
    return element.all(by.css('app-issues ion-card'));
  }

  static getUnreadIssues () {
    return this.getIssues().filter((e) => {
      return e.element(by.css('.read-badge')).isPresent().then(present => !present);
    });
  }

  static getSyncedIssues () {
    return this.getIssues().filter((e) => {
      return e.element(by.css('.synced-badge')).isPresent().then(present => present);
    });
  }

  static getSyncedIssueNumbers () {
    return this.getSyncedIssues().map(async (issue) => {
      const title = await issue.element(by.css('ion-card-title')).getText();
      return title.match(/#(\d+)/)[1];
    });
  }

  static wait () {
    return Page.waitForElement(this.getIssues().first());
  }

  static async toggleMarkAsRead (issueNumber: number) {
    const issue = await this.getIssues().get(issueNumber);
    await Page.scrollIntoView(issue);

    const previousState = await issue.element(by.css('.read-badge')).isPresent();

    await this.getIssues().get(issueNumber)
      .element(by.css('ion-button.read-toggle')).click();

    return browser.wait(async () => {
      const nextState = await issue.element(by.css('.read-badge')).isPresent();
      return previousState !== nextState;
    }, 6500);
  }

  static async markAsReaduntil (issueNumber: number) {
    const previousCount = await this.getUnreadIssues().count();
    await this.clickIssueMenuItem(issueNumber, 'Mark read until here');

    return browser.wait(async () => {
      const nextCount = await this.getUnreadIssues().count();
      return previousCount !== nextCount;
    }, 7500);
  }

  static get markReadUntilHereButton () {
    return element(by.partialButtonText('Mark read until here'));
  }

  static async clickButtonByLabel (issue: number, label: string) {
    await Page.scrollIntoView(this.getIssues().get(issue));
    return this.getIssues().get(issue)
      .element(by.cssContainingText('ion-button', label)).click();
  }

  static clickIssueMenuItem (issue: number, item: string) {
    return Page.clickActionItem(this.getIssues().get(issue), item);
  }
}
