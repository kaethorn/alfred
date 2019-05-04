import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';

import { BrowserPage } from './browser.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild([{
      path: '',
      component: BrowserPage
    }])
  ],
  declarations: [
    BrowserPage
  ],
  entryComponents: [
  ]
})
export class BrowserPageModule {}
