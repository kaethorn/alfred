import { AppPage } from './app.po';

describe('AppComponent', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should display welcome message', async () => {
    await page.navigateTo();
    expect(await page.getTitleText()).toContain('Welcome to KomiX');
  });
});
