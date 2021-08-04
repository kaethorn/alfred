import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { SecureModule } from '../secure/secure.module';

import { BookmarkActionsComponent } from './bookmark-actions/bookmark-actions.component';
import { BookmarksPage } from './bookmarks.page';

@NgModule({
  declarations: [
    BookmarksPage,
    BookmarkActionsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild([{
      component: BookmarksPage, path: ''
    }]),
    SecureModule
  ]
})
export class BookmarksPageModule {}
