import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PopoverController, ToastController } from '@ionic/angular';

import { ComicDatabaseServiceMocks } from '../../testing/comic-database.service.mocks';
import { ComicStorageServiceMocks } from '../../testing/comic-storage.service.mocks';
import { ComicFixtures } from '../../testing/comic.fixtures';
import { ComicsServiceMocks } from '../../testing/comics.service.mocks';
import { PopoverControllerMocks } from '../../testing/popover.controller.mocks';
import { ThumbnailsServiceMocks } from '../../testing/thumbnails.service.mocks';
import { ToastControllerMocks } from '../../testing/toast.controller.mocks';
import { ComicDatabaseService } from '../comic-database.service';
import { ComicStorageService } from '../comic-storage.service';
import { ComicsService } from '../comics.service';
import { ThumbnailsService } from '../thumbnails.service';

import { BookmarksPageModule } from './bookmarks.module';
import { BookmarksPage } from './bookmarks.page';

let component: BookmarksPage;
let fixture: ComponentFixture<BookmarksPage>;
let comicsService: jasmine.SpyObj<ComicsService>;
let thumbnailsService: jasmine.SpyObj<ThumbnailsService>;
let toastController: jasmine.SpyObj<ToastController>;
let toastElement: jasmine.SpyObj<HTMLIonToastElement>;
let popoverElement: jasmine.SpyObj<HTMLIonPopoverElement>;
let popoverController: jasmine.SpyObj<PopoverController>;
let comicStorageService: jasmine.SpyObj<ComicStorageService>;
let comicDatabaseService: jasmine.SpyObj<ComicDatabaseService>;

describe('BookmarksPage', () => {

  beforeEach(async () => {
    comicsService = ComicsServiceMocks.comicsService;
    thumbnailsService = ThumbnailsServiceMocks.thumbnailsService;
    popoverController = PopoverControllerMocks.popoverController;
    popoverElement = PopoverControllerMocks.popoverElementSpy;
    toastController = ToastControllerMocks.toastController;
    toastElement = ToastControllerMocks.toastElementSpy;
    comicStorageService = ComicStorageServiceMocks.comicStorageService;
    comicDatabaseService = ComicDatabaseServiceMocks.comicDatabaseService;

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
      }, {
        provide: ToastController, useValue: toastController
      }, {
        provide: ComicStorageService, useValue: comicStorageService
      }, {
        provide: ComicDatabaseService, useValue: comicDatabaseService
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

  describe('#openMenu', () => {

    it('creates a popover', async () => {
      component.openMenu(new Event(''), ComicFixtures.comic);

      expect(popoverController.create).toHaveBeenCalled();
      expect(popoverController.create.calls.mostRecent().args[0].componentProps)
        .toEqual({ comic: ComicFixtures.comic });
      await popoverController.create.calls.mostRecent().returnValue;
      expect(popoverElement.present).toHaveBeenCalled();
    });
  });

  describe('#sync', () => {

    it('stores adjacent comics', () => {
      component.sync(ComicFixtures.comic);

      expect(comicStorageService.storeSurrounding).toHaveBeenCalledWith(ComicFixtures.comic.id);
    });

    describe('on success', () => {

      it('shows a success toast and updates comic status', async () => {
        component.sync(ComicFixtures.comic);
        expect(component.syncing).toBe(true);

        await comicStorageService.storeSurrounding.calls.mostRecent().returnValue;
        expect(comicDatabaseService.isStored).toHaveBeenCalledWith(ComicFixtures.comic.id);
        expect(component.syncing).toBe(false);
        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Volume cached.',
          duration: 3000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicStorageService.storeSurrounding.and.returnValue(Promise.reject());
      });

      it('shows an error toast', async () => {
        component.sync(ComicFixtures.comic);
        expect(component.syncing).toBe(true);

        await new Promise(resolve => {
          comicStorageService.storeSurrounding.calls.mostRecent().returnValue.catch(resolve);
        });
        expect(component.syncing).toBe(false);
        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error while syncing volume.',
          duration: 4000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });

  describe('#delete', () => {

    it('removes the entire volume from cache', () => {
      component.delete(ComicFixtures.comic);

      expect(comicStorageService.deleteVolume).toHaveBeenCalledWith(ComicFixtures.comic);
    });

    it('updates comic status once removed from cache', async () => {
      component.delete(ComicFixtures.comic);

      await comicStorageService.deleteVolume.calls.mostRecent().returnValue;
      expect(comicDatabaseService.isStored).toHaveBeenCalledWith(ComicFixtures.comic.id);
    });
  });
});
