import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { SecureModule } from '../secure/secure.module';

import { BookmarkActionsComponent } from './bookmark-actions/bookmark-actions.component';
import { BookmarksPage } from './bookmarks.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SecureModule,
    RouterModule.forChild([{
      path: '',
      component: BookmarksPage
    }])
  ],
  declarations: [
    BookmarksPage,
    BookmarkActionsComponent
  ],
  entryComponents: [
    BookmarkActionsComponent
  ]
})
export class BookmarksPageModule {}
