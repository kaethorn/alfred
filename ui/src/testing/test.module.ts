import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { APP_BASE_HREF } from '@angular/common';

import { MatButtonModule } from '@angular/material';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';

import { AppRoutingModule } from './../app/app-routing.module';
import { AppComponent } from './../app/app.component';
import { LibraryComponent } from './../app/library/library.component';
import { BrowserComponent } from './../app/browser/browser.component';
import { PreferencesComponent } from './../app/preferences/preferences.component';
import { ReaderComponent } from './../app/reader/reader.component';
import { ScannerComponent } from './../app/library/scanner/scanner.component';
import { VolumeComponent } from './../app/library/volume/volume.component';
import { VolumesComponent } from './../app/volumes/volumes.component';
import { BookmarksComponent } from './../app/bookmarks/bookmarks.component';

export const TestModule = () => {
  return {
    declarations: [
      AppComponent,
      VolumeComponent,
      VolumesComponent,
      LibraryComponent,
      BrowserComponent,
      ReaderComponent,
      PreferencesComponent,
      ScannerComponent,
      BookmarksComponent
    ],
    imports: [
      FormsModule,
      HttpClientModule,

      MatButtonModule,
      MatProgressBarModule,
      MatChipsModule,
      MatCardModule,
      MatListModule,
      MatToolbarModule,
      MatTooltipModule,
      MatExpansionModule,
      MatIconModule,
      MatInputModule,
      MatDividerModule,
      MatBadgeModule,
      MatSnackBarModule,
      MatMenuModule,

      NoopAnimationsModule,
      AppRoutingModule
    ],
    providers: [
      { provide: APP_BASE_HREF, useValue: '/' }
    ]
  };
};
