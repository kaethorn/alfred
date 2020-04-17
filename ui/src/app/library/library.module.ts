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
  declarations: [
    EditComponent,
    IssueActionsComponent,
    IssuesComponent,
    PublishersComponent,
    SeriesComponent,
    VolumeActionsComponent,
    VolumesComponent
  ],
  entryComponents: [
    VolumeActionsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    IonicModule,
    ReactiveFormsModule,
    RouterModule.forChild([{
      children: [{
        component: PublishersComponent, path: 'publishers'
      }, {
        component: SeriesComponent, path: 'publishers/:publisher/series'
      }, {
        component: VolumesComponent, path: 'publishers/:publisher/series/:series/volumes'
      }, {
        component: IssuesComponent,
        path: 'publishers/:publisher/series/:series/volumes/:volume/issues'
      }, {
        component: EditComponent,
        path: 'publishers/:publisher/series/:series/volumes/:volume/issues/:id/edit'
      }, {
        path: '',
        pathMatch: 'full',
        redirectTo: '/library/publishers'
      }],
      path: ''
    }])
  ]
})
export class LibraryPageModule {}
