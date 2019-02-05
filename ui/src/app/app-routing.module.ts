import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LibraryComponent } from './library/library.component';
import { VolumesComponent } from './volumes/volumes.component';
import { BrowserComponent } from './browser/browser.component';
import { PreferencesComponent } from './preferences/preferences.component';
import { FullScreenReaderComponent } from './full-screen-reader/full-screen-reader.component';
import { BookmarksComponent } from './bookmarks/bookmarks.component';

const routes: Routes = [
  { path: '', redirectTo: '/library', pathMatch: 'full' },
  { path: 'library/:publisher/:series', component: LibraryComponent },
  { path: 'library/:publisher', component: LibraryComponent },
  { path: 'library', component: LibraryComponent },
  { path: 'volumes/:publisher/:series/:volume', component: VolumesComponent },
  { path: 'read/:publisher/:series/:volume', component: BrowserComponent },
  { path: 'read/:id/:page', component: BrowserComponent },
  { path: 'read/:id', component: BrowserComponent },
  { path: 'read-full-screen/:id/:page', component: FullScreenReaderComponent },
  { path: 'preferences', component: PreferencesComponent },
  { path: 'bookmarks', component: BookmarksComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
