import { browser } from 'protractor';

import { AppPage } from './app.po';
import { MongoDBTools } from './mongodb.tools';

describe('AppComponent', () => {

  beforeAll(async () => {
    await MongoDBTools.prepare();
  });

  it('displays the top level library view', async () => {
    await AppPage.navigateTo();
    expect(await browser.getCurrentUrl()).toContain('library/publishers');
  });

  it('informs the user how to populate the library', async () => {
    expect(await AppPage.getPublishersText()).toContain('No comics found');
    expect(await AppPage.getPublishersText()).toContain('SCAN FOR COMICS');
  });
});
