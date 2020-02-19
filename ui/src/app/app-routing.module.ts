import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

import { AuthGuard } from './auth.guard';

const routes: Routes = [{
    path: '',
    redirectTo: 'library',
    pathMatch: 'full'
  }, {
    path: 'library',
    loadChildren: () => import('./library/library.module').then(m => m.LibraryPageModule),
    canActivate: [AuthGuard]
  }, {
    path: 'issues/:publisher/:series/:volume',
    loadChildren: () => import('./issues/issues.module').then(m => m.IssuesPageModule),
    canActivate: [AuthGuard]
  }, {
    path: 'read/:id',
    loadChildren: () => import('./reader/reader.module').then(m => m.ReaderPageModule),
    canActivate: [AuthGuard]
  }, {
    path: 'bookmarks',
    loadChildren: () => import('./bookmarks/bookmarks.module').then(m => m.BookmarksPageModule),
    canActivate: [AuthGuard]
  }, {
    path: 'settings',
    loadChildren: () => import('./settings/settings.module').then(m => m.SettingsPageModule),
    canActivate: [AuthGuard]
  }, {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginPageModule)
  }, {
    path: 'edit',
    loadChildren: () => import('./edit/edit.module').then(m => m.EditPageModule),
    canActivate: [AuthGuard]
  }];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
