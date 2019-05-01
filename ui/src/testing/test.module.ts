import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { APP_BASE_HREF } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { RouterTestingModule } from '@angular/router/testing';

import { MockComponent } from './mock.component';
import { AppComponent } from '../app/app.component';
import { PublishersComponent } from '../app/library/publishers/publishers.component';
import { SeriesComponent } from '../app/library/series/series.component';
import { ScannerComponent } from '../app/preferences/scanner/scanner.component';
import { VolumesComponent } from '../app/library/volumes/volumes.component';
import { VolumeActionsComponent } from '../app/library/volumes/volume-actions/volume-actions.component';
import { BookmarkActionsComponent } from '../app/bookmarks/bookmark-actions/bookmark-actions.component';

import { BrowserPage } from '../app/browser/browser.page';
import { PreferencesPage } from '../app/preferences/preferences.page';
import { ReaderPage } from '../app/reader/reader.page';
import { IssuesPage } from '../app/issues/issues.page';
import { BookmarksPage } from '../app/bookmarks/bookmarks.page';

export const TestModule = () => {
  return {
    declarations: [
      MockComponent,

      AppComponent,
      ScannerComponent,
      PublishersComponent,
      SeriesComponent,
      VolumesComponent,
      VolumeActionsComponent,
      BookmarkActionsComponent,

      IssuesPage,
      BrowserPage,
      ReaderPage,
      PreferencesPage,
      BookmarksPage
    ],
    imports: [
      IonicModule.forRoot(),
      FormsModule,
      HttpClientModule,
      RouterTestingModule.withRoutes([
        { path: 'browse/:id/:page', component: MockComponent }
      ])
    ],
    providers: [
      { provide: APP_BASE_HREF, useValue: '/' }
    ]
  };
};
