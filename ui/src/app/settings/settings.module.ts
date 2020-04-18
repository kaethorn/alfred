import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { MomentModule } from 'ngx-moment';

import { CoversPage } from './covers/covers.page';
import { QueuePage } from './queue/queue.page';
import { ScannerComponent } from './scanner/scanner.component';
import { SettingsPage } from './settings.page';

@NgModule({
  declarations: [
    SettingsPage,
    ScannerComponent,
    QueuePage,
    CoversPage
  ],
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HttpClientModule,
    MomentModule,
    RouterModule.forChild([{
      component: SettingsPage, path: ''
    }, {
      component: QueuePage, path: 'queue'
    }, {
      component: CoversPage, path: 'covers'
    }])
  ]
})
export class SettingsPageModule {}
