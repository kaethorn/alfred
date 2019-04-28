import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';

import { LibraryPage } from './library.page';
import { PublishersComponent } from './publishers/publishers.component';
import { SeriesComponent } from './series/series.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild([{
      path: '',
      component: LibraryPage,
      children: [{
        path: 'publishers',
        component: PublishersComponent
      }, {
        path: 'series',
        component: SeriesComponent
      }, {
        path: '',
        redirectTo: '/library/publishers',
        pathMatch: 'full'
      }]
    }])
  ],
  declarations: [
    LibraryPage,
    PublishersComponent,
    SeriesComponent
  ],
  entryComponents: [
  ]
})
export class LibraryPageModule {}
