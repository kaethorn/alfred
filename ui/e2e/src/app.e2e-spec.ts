import { AppPage } from './app.po';

describe('AppComponent', () => {
  let appPage: AppPage;

  beforeEach(() => {
    appPage = new AppPage();
  });

  it('displays the top level library view', async () => {
    await appPage.navigateTo();
    expect(await appPage.getTitleText()).toContain('Publishers');
  });

  it('informs the user how to populate the library', async () => {
    expect(await appPage.getPublishersText()).toContain('No comics found');
    expect(await appPage.getPublishersText()).toContain('SCAN FOR COMICS');
  });
});
