import { LibraryPage } from './library.po';
import { ScannerPage } from './scanner.po';
import { MongoDBTools } from './mongodb.tools';

fdescribe('VolumesComponent', () => {
  let scannerPage: ScannerPage;
  let page: LibraryPage;

  beforeEach(async () => {
    scannerPage = new ScannerPage();
    page = new LibraryPage();
    await MongoDBTools.prepare();
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

  xit('shows the read issue counter', async () => {
    // TODO
  });

  xit('can mark a whole volume as read and updates its read issue counter', async () => {
    // TODO
  });
});
