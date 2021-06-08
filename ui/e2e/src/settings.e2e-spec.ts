import { MongoDBTools } from './mongodb.tools';
import { Page } from './page.po';
import { SettingsPage } from './settings.po';

describe('SettingsPage', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  it('has default settings', async () => {
    await SettingsPage.navigateTo();
    const setting: string = await SettingsPage.getComicsPath();
    expect(setting.length).toBeGreaterThan(0);
  });

  it('sets settings', async () => {
    await SettingsPage.getComicsPathInput().clear();
    await SettingsPage.getComicsPathInput().sendKeys('some-other-path');
    await SettingsPage.getSaveButton().click();
  });

  it('confirms that settings are saved', async () => {
    expect(await Page.getToastMessage()).toEqual('Settings saved.');
  });
});
