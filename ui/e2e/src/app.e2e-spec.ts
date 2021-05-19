import { browser } from 'protractor';

import { AppPage } from 'e2e/src/app.po';
import { MongoDBTools } from 'e2e/src/mongodb.tools';
import { Page } from 'e2e/src/page.po';

describe('AppComponent', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  it('displays the top level library view', async () => {
    await AppPage.navigateTo();
    expect(await browser.getCurrentUrl()).toContain('library/publishers');
  });

  it('informs the user how to populate the library', async () => {
    await Page.waitForLoadingGone();
    expect(await AppPage.getPublishersText()).toContain('No comics found');
    expect(await AppPage.getPublishersText()).toContain('SCAN FOR COMICS');
  });
});
