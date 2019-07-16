import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../testing/test.module';
import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';

import { ComicsService } from '../comics.service';
import { BookmarksPage } from './bookmarks.page';

describe('BookmarksPage', () => {
  let component: BookmarksPage;
  let fixture: ComponentFixture<BookmarksPage>;

  beforeEach(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: ComicsService, useValue: comicsService
    });
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(BookmarksPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
