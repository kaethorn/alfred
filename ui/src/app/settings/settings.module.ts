import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { MomentModule } from 'ngx-moment';

import { SettingsPage } from './settings.page';
import { ScannerComponent } from './scanner/scanner.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    MomentModule,
    RouterModule.forChild([{
      path: '',
      component: SettingsPage
    }])
  ],
  declarations: [
    SettingsPage,
    ScannerComponent
  ]
})
export class SettingsPageModule {}
