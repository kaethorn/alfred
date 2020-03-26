import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { SecureModule } from '../secure/secure.module';

import { ReaderPage } from './reader.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SecureModule,
    RouterModule.forChild([{
      path: '',
      component: ReaderPage
    }])
  ],
  declarations: [
    ReaderPage
  ],
  entryComponents: [
  ]
})
export class ReaderPageModule {}
