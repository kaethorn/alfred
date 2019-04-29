import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';

import { PreferencesPage } from './preferences.page';
import { ScannerComponent } from './scanner/scanner.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild([{
      path: '',
      component: PreferencesPage
    }])
  ],
  declarations: [
    PreferencesPage,
    ScannerComponent
  ]
})
export class PreferencesPageModule {}
