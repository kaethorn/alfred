import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';

import { IssuesPage } from './issues.page';
import { IssueActionsComponent } from './issue-actions/issue-actions.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
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
