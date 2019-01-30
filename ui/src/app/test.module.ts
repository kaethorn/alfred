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

import { AppRoutingModule } from './app-routing.module';
import { ComicsComponent } from './comics/comics.component';
import { ReaderComponent } from './reader/reader.component';
import { PreferencesComponent } from './preferences/preferences.component';
import { FullScreenReaderComponent } from './full-screen-reader/full-screen-reader.component';
import { ScannerComponent } from './comics/scanner/scanner.component';
import { VolumeComponent } from './comics/volume/volume.component';
import { AppComponent } from './app.component';

export const TestModule = {
  declarations: [
    AppComponent,
    VolumeComponent,
    ComicsComponent,
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
}
