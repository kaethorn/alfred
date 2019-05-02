import { VolumesPage } from './volumes.po';
import { ScannerPage } from './scanner.po';
import { LibraryPage } from './library.po';
import { MongoDBTools } from './mongodb.tools';

describe('VolumesComponent', () => {

  let volumesPage: VolumesPage;
  let scannerPage: ScannerPage;
  let libraryPage: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    volumesPage = new VolumesPage();
    scannerPage = new ScannerPage();
    libraryPage = new LibraryPage();
  });

  it('scans for comics', async () => {
    await scannerPage.scan();
  });

  it('opens the list view for a volume', async () => {
    await libraryPage.clickSeries('Batgirl');
    await libraryPage.getListButton('Vol. 2000').click();
    expect(await volumesPage.getIssues().count()).toBe(73);
  });

  it('shows all issues in unread state', async () => {
    expect(await volumesPage.getUnreadIssues().count()).toBe(73);
  });

  it('can mark as read until a certain issue', async () => {
    await volumesPage.clickIssueMenuItem(4, 'Mark read until here');
    expect(await volumesPage.getUnreadIssues().count()).toBe(68);
  });

  it('can mark an individual issue as read', async () => {
    await volumesPage.getMarkAsReadButton(10).click();
    expect(await volumesPage.getUnreadIssues().count()).toBe(67);
  });

  it('can mark an individual issue as not read', async () => {
    await volumesPage.getMarkAsReadButton(10).click();
    expect(await volumesPage.getUnreadIssues().count()).toBe(68);
  });

  it('has links back to the library', async () => {
    await volumesPage.clickIssueMenuItem(0, 'View in library');
    await libraryPage.waitForSeries('Batgirl');
    expect(await libraryPage.getVolumeTitles().getText())
      .toEqual(['Vol. 2000', 'Vol. 2008', 'Vol. 2009', 'Vol. 2011', 'Vol. 2016']);
  });
});
