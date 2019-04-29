import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { APP_BASE_HREF } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { RouterTestingModule } from '@angular/router/testing';

import { AppComponent } from '../app/app.component';
import { LibraryPage } from '../app/library/library.page';
import { VolumeActionsComponent } from '../app/library/volumes/volume-actions/volume-actions.component'
import { BrowserPage } from '../app/browser/browser.page';
import { PreferencesPage } from '../app/preferences/preferences.page';
import { ReaderPage } from '../app/reader/reader.page';
import { ScannerComponent } from '../app/preferences/scanner/scanner.component';
import { VolumesComponent } from '../app/library/volumes/volumes.component';
import { IssuesPage } from '../app/issues/issues.page';
import { BookmarksPage } from '../app/bookmarks/bookmarks.page';

export const TestModule = () => {
  return {
    declarations: [
      AppComponent,
      IssuesPage,
      VolumesComponent,
      VolumeActionsComponent,
      LibraryPage,
      BrowserPage,
      ReaderPage,
      PreferencesPage,
      ScannerComponent,
      BookmarksPage
    ],
    imports: [
      IonicModule.forRoot(),
      FormsModule,
      HttpClientModule,
      RouterTestingModule
    ],
    providers: [
      { provide: APP_BASE_HREF, useValue: '/' }
    ]
  };
};
