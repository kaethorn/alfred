import { SettingsPage } from './settings.po';

describe('SettingsPage', () => {
  let settingsPage: SettingsPage;

  beforeEach(async () => {
    settingsPage = new SettingsPage();
  });

  it('has default preferences', async () => {
    await settingsPage.navigateTo();
    const preference: string = await settingsPage.getComicsPathInput().getAttribute('value');
    expect(preference.length).toBeGreaterThan(0);
  });

  it('sets preferences', async () => {
    await settingsPage.navigateTo();
    await settingsPage.getComicsPathInput().clear();
    await settingsPage.getComicsPathInput().sendKeys('some-other-path');
    await settingsPage.getSaveButton().click();
  });

  it('does not produce an error', async () => {
    expect(await settingsPage.getError().isPresent()).toBe(false);
  });
});
