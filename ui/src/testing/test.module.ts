import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { APP_BASE_HREF } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { RouterTestingModule } from '@angular/router/testing';

import { MockComponent } from './mock.component';
import { AppComponent } from '../app/app.component';
import { PublishersComponent } from '../app/library/publishers/publishers.component';
import { SeriesComponent } from '../app/library/series/series.component';
import { ScannerComponent } from '../app/settings/scanner/scanner.component';
import { VolumesComponent } from '../app/library/volumes/volumes.component';
import { VolumeActionsComponent } from '../app/library/volumes/volume-actions/volume-actions.component';
import { BookmarkActionsComponent } from '../app/bookmarks/bookmark-actions/bookmark-actions.component';
import { IssueActionsComponent } from '../app/issues/issue-actions/issue-actions.component';

import { BrowserPage } from '../app/browser/browser.page';
import { SettingsPage } from '../app/settings/settings.page';
import { ReaderPage } from '../app/reader/reader.page';
import { IssuesPage } from '../app/issues/issues.page';
import { BookmarksPage } from '../app/bookmarks/bookmarks.page';
import { LoginPage } from '../app/login/login.page';

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
      IssueActionsComponent,

      IssuesPage,
      BrowserPage,
      ReaderPage,
      SettingsPage,
      BookmarksPage,
      LoginPage
    ],
    imports: [
      IonicModule.forRoot(),
      FormsModule,
      HttpClientModule,
      RouterTestingModule.withRoutes([
        { path: 'browse/:id', component: MockComponent },
        { path: 'library', component: MockComponent }
      ])
    ],
    providers: [
      { provide: APP_BASE_HREF, useValue: '/' }
    ]
  };
};
