import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NavParams } from '@ionic/angular';

import { comic1 as comic } from '../../../testing/comic.fixtures';

import { IssueActionsComponent } from './issue-actions.component';
import { IssuesPageModule } from '../issues.module';

describe('IssueActionsComponent', () => {
  let component: IssueActionsComponent;
  let fixture: ComponentFixture<IssueActionsComponent>;
  let navParams: NavParams;

  beforeEach(() => {
    navParams = new NavParams({ comic });
    TestBed.configureTestingModule({
      imports: [
        IssuesPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: NavParams, useValue: navParams
      }]
    });
    fixture = TestBed.createComponent(IssueActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
