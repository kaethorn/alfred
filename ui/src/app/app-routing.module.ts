import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LibraryComponent } from './library/library.component';
import { VolumesComponent } from './volumes/volumes.component';
import { BrowserComponent } from './browser/browser.component';
import { PreferencesComponent } from './preferences/preferences.component';
import { ReaderComponent } from './reader/reader.component';
import { BookmarksComponent } from './bookmarks/bookmarks.component';

const routes: Routes = [
  { path: '', redirectTo: '/library', pathMatch: 'full' },
  { path: 'library/:publisher/:series', component: LibraryComponent },
  { path: 'library/:publisher', component: LibraryComponent },
  { path: 'library', component: LibraryComponent },
  { path: 'volumes/:publisher/:series/:volume', component: VolumesComponent },
  { path: 'browse/:publisher/:series/:volume', component: BrowserComponent },
  { path: 'browse/:id/:page', component: BrowserComponent },
  { path: 'browse/:id', component: BrowserComponent },
  { path: 'read/:id/:page', component: ReaderComponent },
  { path: 'preferences', component: PreferencesComponent },
  { path: 'bookmarks', component: BookmarksComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
