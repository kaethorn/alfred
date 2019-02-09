import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// Material design
import { MatButtonModule } from '@angular/material';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';

// Own
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LibraryComponent } from './library/library.component';
import { BrowserComponent } from './browser/browser.component';
import { PreferencesComponent } from './preferences/preferences.component';
import { ReaderComponent } from './reader/reader.component';
import { ScannerComponent } from './library/scanner/scanner.component';
import { VolumeComponent } from './library/volume/volume.component';
import { VolumesComponent } from './volumes/volumes.component';
import { BookmarksComponent } from './bookmarks/bookmarks.component';

@NgModule({
  declarations: [
    AppComponent,
    LibraryComponent,
    BrowserComponent,
    PreferencesComponent,
    ReaderComponent,
    ScannerComponent,
    VolumeComponent,
    VolumesComponent,
    BookmarksComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,

    MatButtonModule,
    MatProgressBarModule,
    MatChipsModule,
    MatCardModule,
    MatListModule,
    MatToolbarModule,
    MatExpansionModule,
    MatIconModule,
    MatInputModule,
    MatDividerModule,
    MatBadgeModule,
    MatSnackBarModule,
    MatMenuModule,

    BrowserAnimationsModule,
    AppRoutingModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
