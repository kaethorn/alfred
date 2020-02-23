import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { IssueActionsComponent } from './issue-actions/issue-actions.component';
import { IssuesPage } from './issues.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    IonicModule,
    RouterModule.forChild([{
      path: '',
      component: IssuesPage
    }])
  ],
  declarations: [
    IssuesPage,
    IssueActionsComponent
  ],
  entryComponents: [
    IssueActionsComponent
  ]
})
export class IssuesPageModule {}
