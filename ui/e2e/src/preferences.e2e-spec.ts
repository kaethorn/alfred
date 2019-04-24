import { PreferencesPage } from './preferences.po';

describe('PreferencesComponent', () => {
  let preferencesPage: PreferencesPage;

  beforeEach(async () => {
    preferencesPage = new PreferencesPage();
  });

  it('has default preferences', async () => {
    await preferencesPage.navigateTo();
    const preference: string = await preferencesPage.getComicsPathInput().getAttribute('value');
    expect(preference.length).toBeGreaterThan(0);
  });

  it('sets preferences', async () => {
    await preferencesPage.navigateTo();
    await preferencesPage.getComicsPathInput().clear();
    await preferencesPage.getComicsPathInput().sendKeys('some-other-path');
    await preferencesPage.getSaveButton().click();
  });

  it('does not produce an error', async () => {
    expect(await preferencesPage.getError().isPresent()).toBe(false);
  });
});
