import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';

import { BookmarksPage } from './bookmarks.page';
import { BookmarkActionsComponent } from './bookmark-actions/bookmark-actions.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
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
