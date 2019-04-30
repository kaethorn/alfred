import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'library', pathMatch: 'full' },
  { path: 'library', loadChildren: './library/library.module#LibraryPageModule' },
  { path: 'issues/:publisher/:series/:volume', loadChildren: './issues/issues.module#IssuesPageModule' },
  { path: 'browse/:id/:page', loadChildren: './browser/browser.module#BrowserPageModule' },
  { path: 'read/:id/:page', loadChildren: './reader/reader.module#ReaderPageModule' },
  { path: 'bookmarks', loadChildren: './bookmarks/bookmarks.module#BookmarksPageModule' },
  { path: 'settings', loadChildren: './preferences/preferences.module#PreferencesPageModule' }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
