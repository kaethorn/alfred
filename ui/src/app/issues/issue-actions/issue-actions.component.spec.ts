import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavParams } from '@ionic/angular';

import { TestModule } from '../../../testing/test.module';
import { comic1 as comic } from '../../../testing/comic.fixtures';

import { IssueActionsComponent } from './issue-actions.component';

describe('IssueActionsComponent', () => {
  let component: IssueActionsComponent;
  let fixture: ComponentFixture<IssueActionsComponent>;
  let navParams: NavParams;

  beforeEach(() => {
    navParams = new NavParams({ comic });
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: NavParams, useValue: navParams
    });
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(IssueActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
