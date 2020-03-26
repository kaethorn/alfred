import { ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoadingController, ToastController } from '@ionic/angular';
import { throwError } from 'rxjs';

import { ComicFixtures } from '../../../testing/comic.fixtures';
import { ComicsServiceMocks } from '../../../testing/comics.service.mocks';
import { LoadingControllerMocks } from '../../../testing/loading.controller.mocks';
import { ThumbnailsServiceMocks } from '../../../testing/thumbnails.service.mocks';
import { ToastControllerMocks } from '../../../testing/toast.controller.mocks';
import { ComicsService } from '../../comics.service';
import { ThumbnailsService } from '../../thumbnails.service';
import { SettingsPageModule } from '../settings.module';

import { CoversComponent } from './covers.component';

let component: CoversComponent;
let fixture: ComponentFixture<CoversComponent>;
let loadingController: jasmine.SpyObj<LoadingController>;
let loadingElement: jasmine.SpyObj<HTMLIonLoadingElement>;
let toastController: jasmine.SpyObj<ToastController>;
let toastElement: jasmine.SpyObj<HTMLIonToastElement>;
let comicsService: jasmine.SpyObj<ComicsService>;
let thumbnailsService: jasmine.SpyObj<ThumbnailsService>;

describe('CoversComponent', () => {

  beforeEach(<any>fakeAsync(async () => {
    loadingController = LoadingControllerMocks.loadingController;
    loadingElement = LoadingControllerMocks.loadingElementSpy;
    toastController = ToastControllerMocks.toastController;
    toastElement = ToastControllerMocks.toastElementSpy;
    comicsService = ComicsServiceMocks.comicsService;
    thumbnailsService = ThumbnailsServiceMocks.thumbnailsService;

    TestBed.configureTestingModule({
      imports: [
        SettingsPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ThumbnailsService, useValue: thumbnailsService
      }, {
        provide: LoadingController, useValue: loadingController
      }, {
        provide: ToastController, useValue: toastController
      }]
    });

    fixture = TestBed.createComponent(CoversComponent);
    component = fixture.componentInstance;
    component.ionViewWillEnter();

    await loadingController.create.calls.mostRecent().returnValue;
    await loadingElement.present.calls.mostRecent().returnValue;
    tick();
    await comicsService.listComicsWithoutErrors.calls.mostRecent().returnValue.toPromise();
    await thumbnailsService.getFrontCover.calls.mostRecent().returnValue;
    await thumbnailsService.getBackCover.calls.mostRecent().returnValue;

    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loads covers and displays feedback', <any>fakeAsync(async () => {
    loadingElement.dismiss.calls.reset();
    component.ionViewWillEnter();

    expect(loadingController.create).toHaveBeenCalledWith({
      message: 'Loading covers...'
    });

    await loadingController.create.calls.mostRecent().returnValue;
    await loadingElement.present.calls.mostRecent().returnValue;
    tick();
    await comicsService.listComicsWithoutErrors.calls.mostRecent().returnValue.toPromise();

    expect(loadingElement.dismiss).toHaveBeenCalled();
  }));

  describe('on loading error', () => {

    beforeEach(() => {
      comicsService.listComicsWithoutErrors.and.returnValue(throwError(''));
    });

    it('dismisses loading feedback', <any>fakeAsync(async () => {
      loadingElement.dismiss.calls.reset();
      component.ionViewWillEnter();

      expect(loadingController.create).toHaveBeenCalledWith({
        message: 'Loading covers...'
      });

      await loadingController.create.calls.mostRecent().returnValue;
      await loadingElement.present.calls.mostRecent().returnValue;
      tick();
      await new Promise(resolve =>
        comicsService.listComicsWithoutErrors.calls.mostRecent().returnValue.toPromise().catch(resolve));

      expect(loadingElement.dismiss).toHaveBeenCalled();
    }));
  });

  describe('#deleteFrontCover', () => {

    it('deletes the front cover', () => {
      component.deleteFrontCover(ComicFixtures.volume[0]);
      expect(loadingElement.present).toHaveBeenCalled();
      expect(comicsService.deletePage).toHaveBeenCalledWith(ComicFixtures.volume[0], '/1.png');
    });

    describe('on success', () => {

      it('shows a success toast', async () => {
        component.deleteFrontCover(ComicFixtures.volume[0]);
        await comicsService.deletePage.calls.mostRecent().returnValue.toPromise();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Front cover of "401.cbz" deleted.',
          duration: 3000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicsService.deletePage.and.returnValue(throwError(''));
      });

      it('shows an error toast', async () => {
        component.deleteFrontCover(ComicFixtures.volume[0]);
        await new Promise(resolve =>
          comicsService.deletePage.calls.mostRecent().returnValue.toPromise().catch(resolve));

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error while deleting front cover of "401.cbz".',
          duration: 4000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });

  describe('#deleteBackCover', () => {

    it('deletes the back cover', () => {
      component.deleteBackCover(ComicFixtures.volume[0]);

      expect(loadingElement.present).toHaveBeenCalled();
      expect(comicsService.deletePage).toHaveBeenCalledWith(ComicFixtures.volume[0], '/1.png');
    });

    describe('on success', () => {

      it('shows a success toast', async () => {
        component.deleteBackCover(ComicFixtures.volume[0]);
        await comicsService.deletePage.calls.mostRecent().returnValue.toPromise();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Back cover of "401.cbz" deleted.',
          duration: 3000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicsService.deletePage.and.returnValue(throwError(''));
      });

      it('shows an error toast', async () => {
        component.deleteBackCover(ComicFixtures.volume[0]);
        await new Promise(resolve =>
          comicsService.deletePage.calls.mostRecent().returnValue.toPromise().catch(resolve));

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error while deleting back cover of "401.cbz".',
          duration: 4000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });
});
