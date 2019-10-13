import { by, element } from 'protractor';
import { Page } from './page.po';

export class IssuesPage {

  static getIssues () {
    return element.all(by.css('app-issues ion-card'));
  }

  static getUnreadIssues () {
    return this.getIssues().filter((e, index) => {
      return e.element(by.css('ion-badge.read-badge')).isPresent().then(present => !present);
    });
  }

  static wait () {
    return Page.waitForElement(this.getIssues().first());
  }

  static async toggleMarkAsRead (issue: number) {
    await Page.scrollIntoView(this.getIssues().get(issue));
    await this.getIssues().get(issue)
      .element(by.css('ion-button.read-toggle')).click();
  }

  static get markReadUntilHereButton () {
    return element(by.partialButtonText('Mark read until here'));
  }

  static async clickButtonByLabel (issue: number, label: string) {
    await Page.scrollIntoView(this.getIssues().get(issue));
    await this.getIssues().get(issue)
      .element(by.cssContainingText('ion-button', label)).click();
  }

  static clickIssueMenuItem (issue: number, item: string) {
    return Page.clickActionItem(this.getIssues().get(issue), item);
  }
}
