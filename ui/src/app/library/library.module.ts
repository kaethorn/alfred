import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';

import { PublishersComponent } from './publishers/publishers.component';
import { SeriesComponent } from './series/series.component';
import { VolumesComponent } from './volumes/volumes.component';
import { VolumeActionsComponent } from './volumes/volume-actions/volume-actions.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
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
