import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ComicsComponent } from './comics/comics.component';
import { ReaderComponent } from './reader/reader.component';
import { PreferencesComponent } from './preferences/preferences.component';

const routes: Routes = [
  { path: '', redirectTo: '/comics', pathMatch: 'full' },
  { path: 'comics', component: ComicsComponent },
  { path: 'read/:id/:page', component: ReaderComponent },
  { path: 'preferences', component: PreferencesComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
