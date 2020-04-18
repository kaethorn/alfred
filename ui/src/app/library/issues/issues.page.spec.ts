import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastController, PopoverController } from '@ionic/angular';
import { of } from 'rxjs';

import { ComicStorageServiceMocks } from '../../../testing/comic-storage.service.mocks';
import { ComicFixtures } from '../../../testing/comic.fixtures';
import { ComicsServiceMocks } from '../../../testing/comics.service.mocks';
import { PopoverControllerMocks } from '../../../testing/popover.controller.mocks';
import { ThumbnailsServiceMocks } from '../../../testing/thumbnails.service.mocks';
import { ToastControllerMocks } from '../../../testing/toast.controller.mocks';
import { VolumesServiceMocks } from '../../../testing/volumes.service.mocks';
import { ComicStorageService } from '../../comic-storage.service';
import { ComicsService } from '../../comics.service';
import { ThumbnailsService } from '../../thumbnails.service';
import { VolumesService } from '../../volumes.service';
import { LibraryPageModule } from '../library.module';

import { IssuesPage } from './issues.page';

let component: IssuesPage;
let fixture: ComponentFixture<IssuesPage>;
let comicsService: jasmine.SpyObj<ComicsService>;
let comicStorageService: jasmine.SpyObj<ComicStorageService>;
let volumesService: jasmine.SpyObj<VolumesService>;
let thumbnailsService: jasmine.SpyObj<ThumbnailsService>;
let toastController: jasmine.SpyObj<ToastController>;
let popoverElement: jasmine.SpyObj<HTMLIonPopoverElement>;
let popoverController: jasmine.SpyObj<PopoverController>;

describe('IssuesPage', () => {

  beforeEach(() => {
    comicsService = ComicsServiceMocks.comicsService;
    comicStorageService = ComicStorageServiceMocks.comicStorageService;
    volumesService = VolumesServiceMocks.volumesService;
    thumbnailsService = ThumbnailsServiceMocks.thumbnailsService;
    toastController = ToastControllerMocks.toastController;
    popoverController = PopoverControllerMocks.popoverController;
    popoverElement = PopoverControllerMocks.popoverElementSpy;

    TestBed.configureTestingModule({
      imports: [
        LibraryPageModule,
        RouterTestingModule.withRoutes([
        ])
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ComicStorageService, useValue: comicStorageService
      }, {
        provide: VolumesService, useValue: volumesService
      }, {
        provide: ThumbnailsService, useValue: thumbnailsService
      }, {
        provide: PopoverController, useValue: popoverController
      }, {
        provide: ToastController, useValue: toastController
      }]
    });

    fixture = TestBed.createComponent(IssuesPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#markAsRead', () => {

    it('marks the comic as read', () => {
      component.markAsRead(ComicFixtures.volume[0]);
      expect(comicsService.markAsRead).toHaveBeenCalledWith(ComicFixtures.volume[0]);
    });

    describe('on success', () => {

      it('replaces the comic and caches the volume', async () => {
        expect(component.comics[0].read).toBeUndefined();
        component.markAsRead(ComicFixtures.volume[0]);
        await comicsService.markAsRead.calls.mostRecent().returnValue.toPromise();

        expect(comicStorageService.saveIfStored).toHaveBeenCalledWith(ComicFixtures.volume[0]);
        expect(comicStorageService.saveIfStored.calls.mostRecent().args[0].read).toBe(true);
        expect(component.comics[0].read).toBe(true);
        expect(comicStorageService.storeSurrounding).toHaveBeenCalledWith(ComicFixtures.volume[1].id);
      });

      it('does not cache the volume when at the end', async () => {
        component.markAsRead(ComicFixtures.volume[7]);
        await comicsService.markAsRead.calls.mostRecent().returnValue.toPromise();

        expect(comicStorageService.saveIfStored).toHaveBeenCalledWith(ComicFixtures.volume[7]);
        expect(comicStorageService.saveIfStored.calls.mostRecent().args[0].read).toBe(true);
        expect(component.comics[7].read).toBe(true);
        expect(comicStorageService.storeSurrounding).not.toHaveBeenCalled();
      });

      describe('on success', () => {

        it('shows a success toast', async () => {
          component.markAsRead(ComicFixtures.volume[0]);
          await comicsService.markAsRead.calls.mostRecent().returnValue.toPromise();
          await comicStorageService.storeSurrounding.calls.mostRecent().returnValue;

          expect(toastController.create).toHaveBeenCalledWith({
            duration: 3000,
            message: 'Volume cached.'
          });
        });
      });
    });
  });

  describe('#markAsUnread', () => {

    it('marks the comic as unread', () => {
      component.markAsUnread(ComicFixtures.volume[0]);
      expect(comicsService.markAsUnread).toHaveBeenCalledWith(ComicFixtures.volume[0]);
    });

    describe('on success', () => {

      it('replaces the comic and caches the volume', async () => {
        expect(component.comics[2].read).toBeUndefined();
        component.markAsUnread(ComicFixtures.volume[2]);
        await comicsService.markAsUnread.calls.mostRecent().returnValue.toPromise();

        expect(comicStorageService.saveIfStored).toHaveBeenCalledWith(ComicFixtures.volume[2]);
        expect(comicStorageService.saveIfStored.calls.mostRecent().args[0].read).toBe(false);
        expect(component.comics[2].read).toBe(false);
        expect(comicStorageService.storeSurrounding).toHaveBeenCalledWith(ComicFixtures.volume[1].id);
      });

      it('does not cache the volume when at the beginning', async () => {
        component.markAsUnread(ComicFixtures.volume[0]);
        await comicsService.markAsUnread.calls.mostRecent().returnValue.toPromise();

        expect(comicStorageService.saveIfStored).toHaveBeenCalledWith(ComicFixtures.volume[0]);
        expect(comicStorageService.saveIfStored.calls.mostRecent().args[0].read).toBe(false);
        expect(component.comics[0].read).toBe(false);
        expect(comicStorageService.storeSurrounding).not.toHaveBeenCalled();
      });

      describe('on success', () => {

        it('shows a success toast', async () => {
          component.markAsUnread(ComicFixtures.volume[2]);
          await comicsService.markAsUnread.calls.mostRecent().returnValue.toPromise();
          await comicStorageService.storeSurrounding.calls.mostRecent().returnValue;

          expect(toastController.create).toHaveBeenCalledWith({
            duration: 3000,
            message: 'Volume cached.'
          });
        });
      });
    });
  });

  describe('#markAsReadUntil', () => {

    it('marks the volume as unread until the given comic', () => {
      component.markAsReadUntil(ComicFixtures.volume[3]);
      expect(volumesService.markAllAsReadUntil).toHaveBeenCalledWith(ComicFixtures.volume[3]);
    });

    describe('on success', () => {

      beforeEach(() => {
        comicsService.listByVolume.and.returnValue(of(ComicFixtures.volumeInProgress));
      });

      it('refreshes and caches the volume', async () => {
        expect(component.comics.map(comic => comic.read))
          .toEqual([ undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined ]);
        component.markAsReadUntil(ComicFixtures.volume[1]);
        await volumesService.markAllAsReadUntil.calls.mostRecent().returnValue.toPromise();

        expect(component.comics.map(comic => comic.read))
          .toEqual([ true, true, undefined, undefined, undefined, undefined, undefined, undefined ]);
        expect(comicStorageService.storeSurrounding).toHaveBeenCalledWith(ComicFixtures.volume[2].id);
      });

      it('does not cache the volume when at the end', async () => {
        component.markAsReadUntil(ComicFixtures.volume[7]);
        await volumesService.markAllAsReadUntil.calls.mostRecent().returnValue.toPromise();

        expect(comicStorageService.storeSurrounding).not.toHaveBeenCalled();
      });

      describe('on success', () => {

        it('shows a success toast', async () => {
          component.markAsReadUntil(ComicFixtures.volume[1]);
          await volumesService.markAllAsReadUntil.calls.mostRecent().returnValue.toPromise();
          await comicStorageService.storeSurrounding.calls.mostRecent().returnValue;

          expect(toastController.create).toHaveBeenCalledWith({
            duration: 3000,
            message: 'Volume cached.'
          });
        });
      });
    });
  });

  describe('#openMenu', () => {

    it('opens a popover menu', async () => {
      component.openMenu(new Event(''), ComicFixtures.volume[0]);

      expect(popoverController.create).toHaveBeenCalled();
      expect(popoverController.create.calls.mostRecent().args[0].componentProps)
        .toEqual({ comic: ComicFixtures.volume[0] });
      await popoverController.create.calls.mostRecent().returnValue;
      expect(popoverElement.onDidDismiss).toHaveBeenCalled();
      expect(popoverElement.present).toHaveBeenCalled();
    });

    describe('with a chosen issue', () => {

      beforeEach(() => {
        popoverElement.onDidDismiss.and.resolveTo({
          data: {
            markAsReadUntil: ComicFixtures.volume[5]
          }
        });
      });

      it('marks as read until the chosen issue', async () => {
        expect(volumesService.markAllAsReadUntil).not.toHaveBeenCalled();
        component.openMenu(new Event(''), ComicFixtures.volume[0]);

        expect(popoverController.create).toHaveBeenCalled();
        expect(popoverController.create.calls.mostRecent().args[0].componentProps)
          .toEqual({ comic: ComicFixtures.volume[0] });
        await popoverController.create.calls.mostRecent().returnValue;
        expect(popoverElement.onDidDismiss).toHaveBeenCalled();
        expect(popoverElement.present).toHaveBeenCalled();
        await popoverElement.onDidDismiss.calls.mostRecent().returnValue;
        expect(volumesService.markAllAsReadUntil).toHaveBeenCalledWith(ComicFixtures.volume[5]);
      });
    });
  });
});
