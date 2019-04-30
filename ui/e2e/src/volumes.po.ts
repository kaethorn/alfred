import { by, element } from 'protractor';
import { Page } from './page.po';

export class VolumesPage {

  private page: Page;

  constructor () {
    this.page = new Page();
  }

  getIssues () {
    return element.all(by.css('app-volumes mat-card'));
  }

  getUnreadIssues () {
    return this.getIssues().all(by.css('a.mat-badge-hidden'));
  }

  getMarkAsReadButton (issue: number) {
    return this.getIssues().get(issue)
      .all(by.css('mat-card-actions .read-toggle')).first();
  }

  get markReadUntilHereButton () {
    return element(by.partialButtonText('Mark read until here'));
  }

  async clickIssueMenuItem (issue: number, item: string) {
    await this.page.clickMenuItem(this.getIssues().get(issue), item);
  }
}
