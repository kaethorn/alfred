import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NavParams } from '@ionic/angular';

import { comic1 as comic } from '../../../testing/comic.fixtures';

import { BookmarkActionsComponent } from './bookmark-actions.component';
import { BookmarksPageModule } from '../bookmarks.module';

describe('BookmarkActionsComponent', () => {
  let component: BookmarkActionsComponent;
  let fixture: ComponentFixture<BookmarkActionsComponent>;
  let navParams: NavParams;

  beforeEach(() => {
    navParams = new NavParams({ comic });
    TestBed.configureTestingModule({
      imports: [
        BookmarksPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: NavParams, useValue: navParams
      }]
    });
    fixture = TestBed.createComponent(BookmarkActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
