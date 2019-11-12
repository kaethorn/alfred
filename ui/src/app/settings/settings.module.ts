import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { MomentModule } from 'ngx-moment';
import { HttpClientModule } from '@angular/common/http';

import { SettingsPage } from './settings.page';
import { ScannerComponent } from './scanner/scanner.component';
import { QueueComponent } from './queue/queue.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HttpClientModule,
    MomentModule,
    RouterModule.forChild([{
      path: '',
      component: SettingsPage
    }, {
      path: 'queue',
      component: QueueComponent
    }])
  ],
  declarations: [
    SettingsPage,
    ScannerComponent,
    QueueComponent
  ]
})
export class SettingsPageModule {}
