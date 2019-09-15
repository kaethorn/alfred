import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

import { AuthGuard } from './auth.guard';

const routes: Routes = [
  { path: '', redirectTo: 'library', pathMatch: 'full' },
  { path: 'library', loadChildren: './library/library.module#LibraryPageModule', canActivate: [AuthGuard] },
  { path: 'issues/:publisher/:series/:volume', loadChildren: './issues/issues.module#IssuesPageModule', canActivate: [AuthGuard] },
  { path: 'read/:id', loadChildren: './reader/reader.module#ReaderPageModule', canActivate: [AuthGuard] },
  { path: 'bookmarks', loadChildren: './bookmarks/bookmarks.module#BookmarksPageModule', canActivate: [AuthGuard] },
  { path: 'settings', loadChildren: './settings/settings.module#SettingsPageModule', canActivate: [AuthGuard] },
  { path: 'login', loadChildren: './login/login.module#LoginPageModule' },
  { path: 'queue', loadChildren: './queue/queue.module#QueuePageModule' },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
