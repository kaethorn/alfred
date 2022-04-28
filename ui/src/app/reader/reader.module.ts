import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { SecureModule } from '../secure/secure.module';

import { ReaderPage } from './reader.page';

@NgModule({
  declarations: [
    ReaderPage
  ],
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild([{
      component: ReaderPage, path: ''
    }]),
    SecureModule
  ]
})
export class ReaderPageModule {}
