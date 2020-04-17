import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { EditComponent } from './edit/edit.component';
import { IssueActionsComponent } from './issues/issue-actions/issue-actions.component';
import { IssuesComponent } from './issues/issues.component';
import { PublishersComponent } from './publishers/publishers.component';
import { SeriesComponent } from './series/series.component';
import { VolumeActionsComponent } from './volumes/volume-actions/volume-actions.component';
import { VolumesComponent } from './volumes/volumes.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
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
        path: 'publishers/:publisher/series/:series/volumes/:volume/issues',
        component: IssuesComponent
      }, {
        path: 'publishers/:publisher/series/:series/volumes/:volume/issues/:id/edit',
        component: EditComponent
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
    VolumeActionsComponent,
    IssuesComponent,
    IssueActionsComponent,
    EditComponent
  ],
  entryComponents: [
    VolumeActionsComponent
  ]
})
export class LibraryPageModule {}
