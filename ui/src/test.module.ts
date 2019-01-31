import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

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

import { AppRoutingModule } from './app/app-routing.module';
import { LibraryComponent } from './app/library/library.component';
import { ReaderComponent } from './app/reader/reader.component';
import { PreferencesComponent } from './app/preferences/preferences.component';
import { FullScreenReaderComponent } from './app/full-screen-reader/full-screen-reader.component';
import { ScannerComponent } from './app/library/scanner/scanner.component';
import { VolumeComponent } from './app/library/volume/volume.component';
import { VolumesComponent } from './app/volumes/volumes.component';
import { AppComponent } from './app/app.component';

export const TestModule = {
  declarations: [
    AppComponent,
    VolumeComponent,
    VolumesComponent,
    LibraryComponent,
    ReaderComponent,
    FullScreenReaderComponent,
    PreferencesComponent,
    ScannerComponent
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
    MatExpansionModule,
    MatIconModule,
    MatInputModule,
    MatDividerModule,

    AppRoutingModule
  ]
};
