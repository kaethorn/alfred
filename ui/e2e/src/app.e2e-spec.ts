import { AppPage } from './app.po';

describe('AppComponent', () => {
  let appPage: AppPage;

  beforeEach(() => {
    appPage = new AppPage();
  });

  it('should display welcome message', async () => {
    await appPage.navigateTo();
    expect(await appPage.getTitleText()).toContain('Alfred');
  });
});
