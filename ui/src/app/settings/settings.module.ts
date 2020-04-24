import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { MomentModule } from 'ngx-moment';

import { EditPage } from '../library/edit/edit.page';

import { QueuePage } from './queue/queue.page';
import { ScannerComponent } from './scanner/scanner.component';
import { SettingsPage } from './settings.page';

@NgModule({
  declarations: [
    QueuePage,
    SettingsPage,
    ScannerComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HttpClientModule,
    MomentModule,
    ReactiveFormsModule,
    RouterModule.forChild([{
      component: SettingsPage, path: ''
    }, {
      component: QueuePage, path: 'queue'
    }, {
      component: EditPage, path: 'queue/edit/:id'
    }])
  ]
})
export class SettingsPageModule {}
