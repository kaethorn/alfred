import { ScannerPage } from './scanner.po';
import { PreferencesPage } from './preferences.po';

describe('ScannerComponent', () => {
  let page: ScannerPage;
  let preferencesPage: PreferencesPage;

  beforeEach(async () => {
    page = new ScannerPage();
    preferencesPage = new PreferencesPage();
  });

  it('sets preferences', async () => {
    await preferencesPage.navigateTo();
    await preferencesPage.getComicsPathInput().clear();
    await preferencesPage.getComicsPathInput().sendKeys('src/test/resources/fixtures/full');
    await preferencesPage.getSaveButton().click();
  });

  it('should display a scan button', async () => {
    await page.navigateTo();
    expect(await page.getScanButton().isPresent()).toBe(true);
  });

  it('starts scanning comics when clicking the button', async () => {
    await page.getScanButton().click();
    await page.waitForScanProgress();
    expect(await page.getScanProgress())
      .toMatch(/Scanning\ file\ \d+\ of\ \d+\ at\ .*Batman\ \d+\ \(1940\)\.cbz/);
    expect(await page.getScanErrors().isPresent()).toBe(false);
  });
});
