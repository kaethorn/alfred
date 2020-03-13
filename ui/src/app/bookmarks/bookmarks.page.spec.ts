import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PopoverController } from '@ionic/angular';

import { comic1 as comic } from '../../testing/comic.fixtures';
import { ComicsServiceMocks } from '../../testing/comics.service.mocks';
import { PopoverControllerMocks } from '../../testing/popover.controller.mocks';
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
let popoverElement: jasmine.SpyObj<HTMLIonPopoverElement>;
let popoverController: jasmine.SpyObj<PopoverController>;

describe('BookmarksPage', () => {

  beforeEach(async () => {
    comicsService = ComicsServiceMocks.comicsService;
    thumbnailsService = ThumbnailsServiceMocks.thumbnailsService;
    popoverController = PopoverControllerMocks.popoverController;
    popoverElement = PopoverControllerMocks.popoverElementSpy;

    TestBed.configureTestingModule({
      imports: [
        BookmarksPageModule
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ThumbnailsService, useValue: thumbnailsService
      }, {
        provide: PopoverController, useValue: popoverController
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

  fdescribe('#openMenu', () => {

    it('creates a popover', async () => {
      component.openMenu(new Event(''), comic);

      expect(popoverController.create).toHaveBeenCalled();
      expect(popoverController.create.calls.mostRecent().args[0].componentProps)
        .toEqual({ comic });
      await popoverController.create.calls.mostRecent().returnValue;
      expect(popoverElement.present).toHaveBeenCalled();
    });
  });
});
