import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComicsServiceMocks } from '../../testing/comics.service.mocks';
import { ThumbnailsServiceMocks } from '../../testing/thumbnails.service.mocks';
import { ComicDatabaseService } from '../comic-database.service';
import { ComicsService } from '../comics.service';
import { ThumbnailsService } from '../thumbnails.service';

import { BookmarksPageModule } from './bookmarks.module';
import { BookmarksPage } from './bookmarks.page';

let component: BookmarksPage;
let fixture: ComponentFixture<BookmarksPage>;
let comicsService: jasmine.SpyObj<ComicsService>;
let thumbnailsService: jasmine.SpyObj<ThumbnailsService>;

describe('BookmarksPage', () => {

  beforeEach(async () => {
    comicsService = ComicsServiceMocks.comicsService;
    thumbnailsService = ThumbnailsServiceMocks.thumbnailsService;

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
