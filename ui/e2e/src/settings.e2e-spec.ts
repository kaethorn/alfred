import { SettingsPage } from './settings.po';
import { MongoDBTools } from './mongodb.tools';

describe('SettingsPage', () => {
  let settingsPage: SettingsPage;

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  beforeEach(async () => {
    settingsPage = new SettingsPage();
  });

  it('has default preferences', async () => {
    await settingsPage.navigateTo();
    const preference: string = await settingsPage.getComicsPathInput().getAttribute('value');
    expect(preference.length).toBeGreaterThan(0);
  });

  it('sets preferences', async () => {
    await settingsPage.getComicsPathInput().clear();
    await settingsPage.getComicsPathInput().sendKeys('some-other-path');
    await settingsPage.getSaveButton().click();
  });

  it('confirms that settings are saved', async () => {
    expect(await settingsPage.getConfirmationMessage()).toEqual('Settings saved.');
  });
});
