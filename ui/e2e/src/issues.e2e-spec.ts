import { IssuesPage } from './issues.po';
import { SettingsPage } from './settings.po';
import { LibraryPage } from './library.po';
import { MongoDBTools } from './mongodb.tools';

describe('IssuesComponent', () => {

  let issuesPage: IssuesPage;
  let settingsPage: SettingsPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    issuesPage = new IssuesPage();
    settingsPage = new SettingsPage();
    libraryPage = new LibraryPage();
  });

  it('scans for comics', async () => {
    await settingsPage.scan();
  });

  it('opens the list view for a volume', async () => {
    await libraryPage.navigateTo();
    await libraryPage.clickPublisher('DC Comics');
    await libraryPage.clickSeries('Batgirl');
    await libraryPage.clickVolumeListButton('Vol. 2000');
    await issuesPage.wait();
    expect(await issuesPage.getIssues().count()).toBe(73);
  });

  it('shows all issues in unread state', async () => {
    expect(await issuesPage.getUnreadIssues().count()).toBe(73);
  });

  it('can mark as read until a certain issue', async () => {
    await issuesPage.clickIssueMenuItem(4, 'Mark read until here');
    expect(await issuesPage.getUnreadIssues().count()).toBe(68);
  });

  it('can mark an individual issue as read', async () => {
    await issuesPage.toggleMarkAsRead(10);
    expect(await issuesPage.getUnreadIssues().count()).toBe(67);
  });

  it('can mark an individual issue as not read', async () => {
    await issuesPage.toggleMarkAsRead(10);
    expect(await issuesPage.getUnreadIssues().count()).toBe(68);
  });

  it('has links back to the library', async () => {
    await issuesPage.clickIssueMenuItem(0, 'View in library');
    expect(await libraryPage.getVolumeTitles().getText())
      .toEqual(['Vol. 2000', 'Vol. 2008', 'Vol. 2009', 'Vol. 2011', 'Vol. 2016']);
  });
});
