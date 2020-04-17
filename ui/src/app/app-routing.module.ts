import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

import { AuthGuard } from './auth.guard';
import { BookmarksPageModule } from './bookmarks/bookmarks.module';
import { LibraryPageModule } from './library/library.module';
import { LoginPageModule } from './login/login.module';
import { ReaderPageModule } from './reader/reader.module';
import { SettingsPageModule } from './settings/settings.module';

const routes: Routes = [{
  path: '',
  redirectTo: 'library',
  pathMatch: 'full'
}, {
  path: 'library',
  loadChildren: (): Promise<LibraryPageModule> => import('./library/library.module').then(m => m.LibraryPageModule),
  canActivate: [ AuthGuard ]
}, {
  path: 'read/:id',
  loadChildren: (): Promise<ReaderPageModule> => import('./reader/reader.module').then(m => m.ReaderPageModule),
  canActivate: [ AuthGuard ]
}, {
  path: 'bookmarks',
  loadChildren: (): Promise<BookmarksPageModule> => import('./bookmarks/bookmarks.module').then(m => m.BookmarksPageModule),
  canActivate: [ AuthGuard ]
}, {
  path: 'settings',
  loadChildren: (): Promise<SettingsPageModule> => import('./settings/settings.module').then(m => m.SettingsPageModule),
  canActivate: [ AuthGuard ]
}, {
  path: 'login',
  loadChildren: (): Promise<LoginPageModule> => import('./login/login.module').then(m => m.LoginPageModule)
}];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
