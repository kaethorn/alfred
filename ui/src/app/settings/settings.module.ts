import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { MomentModule } from 'ngx-moment';

import { CoversComponent } from './covers/covers.component';
import { QueueComponent } from './queue/queue.component';
import { ScannerComponent } from './scanner/scanner.component';
import { SettingsPage } from './settings.page';

@NgModule({
  declarations: [
    SettingsPage,
    ScannerComponent,
    QueueComponent,
    CoversComponent
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
      component: QueueComponent, path: 'queue'
    }, {
      component: CoversComponent, path: 'covers'
    }])
  ]
})
export class SettingsPageModule {}
