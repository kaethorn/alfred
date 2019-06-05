import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'library', pathMatch: 'full' },
  { path: 'library', loadChildren: './library/library.module#LibraryPageModule' },
  { path: 'issues/:publisher/:series/:volume', loadChildren: './issues/issues.module#IssuesPageModule' },
  { path: 'browse/:id', loadChildren: './browser/browser.module#BrowserPageModule' },
  { path: 'read/:id', loadChildren: './reader/reader.module#ReaderPageModule' },
  { path: 'bookmarks', loadChildren: './bookmarks/bookmarks.module#BookmarksPageModule' },
  { path: 'settings', loadChildren: './settings/settings.module#SettingsPageModule' },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
