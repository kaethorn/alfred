import { LibraryPage } from './library.po';
import { ScannerPage } from './scanner.po';
import { MongoDBTools } from './mongodb.tools';

fdescribe('VolumesComponent', () => {
  let scannerPage: ScannerPage;
  let page: LibraryPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    scannerPage = new ScannerPage();
    page = new LibraryPage();
  });

  it('scans for comics', async () => {
    await scannerPage.scan();
  });

  it('sorts publishers alphabetically', async () => {
    expect(await page.getComicPublishers())
      .toEqual(['DC Comics', 'F5 Enteratinment', 'Top Cow']);
  });

  it('sorts series alphabetically', async () => {
    expect(await page.getComicSeries())
      .toEqual([ 'Batman', 'Batgirl', 'The Tenth: Resurrected', 'The Tenth', 'Rising Stars' ]);
  });

  it('sorts volumes alphabetically', async () => {
    await page.getComicVolume('Batgirl').click();
    expect(await page.getComicVolumes())
      .toEqual(['Vol. 2000', 'Vol. 2008', 'Vol. 2009', 'Vol. 2011', 'Vol. 2016']);
  });

  it('shows the read issue counter', async () => {
    expect(await page.getComicVolumeStats())
      .toEqual([ '0 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
  });

  it('updates its read issue counter when marking the volume as read', async () => {
    await page.clickVolumeMenuItem('Vol. 2000', 'Mark volume as read');
    expect(await page.getComicVolumeStats())
      .toEqual([ '73 of 73 issues read', '0 of 6 issues read', '0 of 24 issues read', '0 of 53 issues read', '0 of 6 issues read']);
  });
});
