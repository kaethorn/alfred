import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

// Material design
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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

// Own
import { AppComponent } from './app.component';
import { ComicsComponent } from './comics/comics.component';
import { ReaderComponent } from './reader/reader.component';
import { AppRoutingModule } from './app-routing.module';
import { PreferencesComponent } from './preferences/preferences.component';
import { FullScreenReaderComponent } from './full-screen-reader/full-screen-reader.component';
import { ScannerComponent } from './comics/scanner/scanner.component';
import { VolumeComponent } from './comics/volume/volume.component';

@NgModule({
  declarations: [
    AppComponent,
    ComicsComponent,
    ReaderComponent,
    PreferencesComponent,
    FullScreenReaderComponent,
    ScannerComponent,
    VolumeComponent
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

    BrowserAnimationsModule,
    AppRoutingModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
