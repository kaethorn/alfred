import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NavParams } from '@ionic/angular';

import { TestModule } from '../../../testing/test.module';
import { comic1 as comic } from '../../../testing/comic.fixtures';

import { BookmarkActionsComponent } from './bookmark-actions.component';

describe('BookmarkActionsComponent', () => {
  let component: BookmarkActionsComponent;
  let fixture: ComponentFixture<BookmarkActionsComponent>;
  let navParams: NavParams;

  beforeEach(async(() => {
    navParams = new NavParams({ comic });
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: NavParams, useValue: navParams
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookmarkActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
