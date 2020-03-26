import { browser } from 'protractor';

import { IssuesPage } from './issues.po';
import { LibraryPage } from './library.po';
import { MongoDBTools } from './mongodb.tools';
import { SettingsPage } from './settings.po';

describe('IssuesComponent', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  it('scans for comics', async () => {
    await SettingsPage.scan();
  });

  it('opens the list view for a volume', async () => {
    await LibraryPage.navigateTo();
    await LibraryPage.clickPublisher('DC Comics');
    await LibraryPage.clickSeries('Batgirl');
    await LibraryPage.clickVolumeListButton('Vol. 2000');
    await IssuesPage.wait();
    expect(await IssuesPage.getIssues().count()).toBe(73);
  });

  it('shows all issues in unread state', async () => {
    expect(await IssuesPage.getUnreadIssuesCount()).toBe(73);
  });

  it('can mark as read until a certain issue', async () => {
    await IssuesPage.markAsReaduntil(4);
    expect(await IssuesPage.getUnreadIssuesCount()).toBe(68);
  });

  it('can mark an individual issue as read', async () => {
    await IssuesPage.toggleMarkAsRead(10);
    expect(await IssuesPage.getUnreadIssuesCount()).toBe(67);
  });

  it('can mark an individual issue as not read', async () => {
    await IssuesPage.toggleMarkAsRead(10);
    expect(await IssuesPage.getUnreadIssuesCount()).toBe(68);
  });

  it('has links back to the library', async () => {
    await IssuesPage.clickIssueMenuItem(0, 'View in library');
    await browser.sleep(500);
    await LibraryPage.waitForVolumes();
    expect(await LibraryPage.getVolumeTitles().getText())
      .toEqual(['Vol. 2000', 'Vol. 2008', 'Vol. 2009', 'Vol. 2011', 'Vol. 2016']);
  });
});
