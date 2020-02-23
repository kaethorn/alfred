import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { PublishersComponent } from './publishers/publishers.component';
import { SeriesComponent } from './series/series.component';
import { VolumeActionsComponent } from './volumes/volume-actions/volume-actions.component';
import { VolumesComponent } from './volumes/volumes.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    IonicModule,
    RouterModule.forChild([{
      path: '',
      children: [{
        path: 'publishers',
        component: PublishersComponent
      }, {
        path: 'publishers/:publisher/series',
        component: SeriesComponent
      }, {
        path: 'publishers/:publisher/series/:series/volumes',
        component: VolumesComponent
      }, {
        path: '',
        redirectTo: '/library/publishers',
        pathMatch: 'full'
      }]
    }])
  ],
  declarations: [
    PublishersComponent,
    SeriesComponent,
    VolumesComponent,
    VolumeActionsComponent
  ],
  entryComponents: [
    VolumeActionsComponent
  ]
})
export class LibraryPageModule {}
