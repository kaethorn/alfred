import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';
import { ThumbnailsServiceMocks as thumbnailsService } from '../../testing/thumbnails.service.mocks';

import { ComicsService } from '../comics.service';
import { ThumbnailsService } from '../thumbnails.service';
import { BookmarksPage } from './bookmarks.page';
import { BookmarksPageModule } from './bookmarks.module';
import { ComicDatabaseService } from '../comic-database.service';

describe('BookmarksPage', () => {
  let component: BookmarksPage;
  let fixture: ComponentFixture<BookmarksPage>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [
        BookmarksPageModule
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ThumbnailsService, useValue: thumbnailsService
      }]
    });

    const dbService = TestBed.inject(ComicDatabaseService);
    await dbService.ready.toPromise();

    fixture = TestBed.createComponent(BookmarksPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
