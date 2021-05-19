import { by, element, browser, promise, ElementArrayFinder, ElementFinder } from 'protractor';

import { Page } from './page.po';

export class IssuesPage {

  public static getIssues(): ElementArrayFinder {
    return element.all(by.css('app-issues ion-card'));
  }

  public static getUnreadIssues(): ElementArrayFinder {
    return this.getIssues().filter(issue =>
      issue.$('.read-badge').isPresent().then(read => !read)
    );
  }

  public static getUnreadIssuesCount(): promise.Promise<number> {
    return this.getIssues().reduce(async (result: number, issue: ElementArrayFinder) => {
      const read = await issue.element(by.css('.read-badge')).isPresent();
      return read ? result : result + 1;
    }, 0);
  }

  public static getSyncedIssues(): ElementArrayFinder {
    return this.getIssues().filter(e =>
      e.element(by.css('.synced-badge')).isPresent().then(present => present)
    );
  }

  public static getSyncedIssueNumbers(): promise.Promise<string[]> {
    return this.getSyncedIssues().map(async issue => {
      const title = await issue?.element(by.css('ion-card-title')).getText();
      const match = title?.match(/#(\d+)/);
      return match ? match[1] : null;
    });
  }

  public static wait(): promise.Promise<void> {
    return Page.waitForElement(this.getIssues().first());
  }

  public static async toggleMarkAsRead(issueNumber: number): Promise<boolean> {
    const issue = this.getIssues().get(issueNumber);
    await Page.scrollIntoView(issue);

    const previousState = await issue.element(by.css('.read-badge')).isPresent();

    await this.getIssues().get(issueNumber)
      .element(by.css('ion-button.read-toggle')).click();

    return browser.wait(async () => {
      const nextState = await issue.element(by.css('.read-badge')).isPresent();
      return previousState !== nextState;
    }, 6500);
  }

  public static async markAsReaduntil(issueNumber: number): Promise<boolean | void> {
    const previousCount = await this.getUnreadIssuesCount();
    await this.clickIssueMenuItem(issueNumber, 'Mark read until here');

    return browser.wait(() =>
      this.getUnreadIssuesCount().then(count => count !== previousCount)
    , 7500);
  }

  public static get markReadUntilHereButton(): ElementFinder {
    return element(by.partialButtonText('Mark read until here'));
  }

  public static async clickButtonByLabel(issue: number, label: string): Promise<void> {
    await Page.scrollIntoView(this.getIssues().get(issue));
    return this.getIssues().get(issue)
      .element(by.cssContainingText('ion-button', label)).click();
  }

  public static clickIssueMenuItem(issue: number, item: string): Promise<void> {
    return Page.clickActionItem(this.getIssues().get(issue), item);
  }
}
