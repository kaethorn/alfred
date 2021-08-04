import { ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastController, LoadingController } from '@ionic/angular';
import { throwError } from 'rxjs';

import { ComicFixtures } from '../../../testing/comic.fixtures';
import { ComicsServiceMocks } from '../../../testing/comics.service.mocks';
import { LoadingControllerMocks } from '../../../testing/loading.controller.mocks';
import { ToastControllerMocks } from '../../../testing/toast.controller.mocks';
import { ComicsService } from '../../comics.service';
import { SettingsPageModule } from '../settings.module';

import { QueuePage } from './queue.page';

let component: QueuePage;
let fixture: ComponentFixture<QueuePage>;
let loadingController: jasmine.SpyObj<LoadingController>;
let loadingElement: jasmine.SpyObj<HTMLIonLoadingElement>;
let toastController: jasmine.SpyObj<ToastController>;
let toastElement: jasmine.SpyObj<HTMLIonToastElement>;
let comicsService: jasmine.SpyObj<ComicsService>;

describe('QueuePage', () => {

  beforeEach(<any>fakeAsync(async () => {
    loadingController = LoadingControllerMocks.loadingController;
    loadingElement = LoadingControllerMocks.loadingElementSpy;
    toastController = ToastControllerMocks.toastController;
    toastElement = ToastControllerMocks.toastElementSpy;
    comicsService = ComicsServiceMocks.comicsService;

    TestBed.configureTestingModule({
      imports: [
        SettingsPageModule,
        RouterTestingModule
      ],
      providers: [
        { provide: ComicsService, useValue: comicsService },
        { provide: LoadingController, useValue: loadingController },
        { provide: ToastController, useValue: toastController }
      ]
    });

    fixture = TestBed.createComponent(QueuePage);
    component = fixture.componentInstance;
    component.ionViewWillEnter();

    await loadingController.create.calls.mostRecent().returnValue;
    await loadingElement.present.calls.mostRecent().returnValue;
    tick();
    await comicsService.listComicsWithErrors.calls.mostRecent().returnValue.toPromise();

    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('displays feedback while loading', <any>fakeAsync(async () => {
    loadingElement.dismiss.calls.reset();
    component.ionViewWillEnter();

    expect(loadingController.create).toHaveBeenCalledWith({
      message: 'Loading queue...'
    });

    await loadingController.create.calls.mostRecent().returnValue;
    await loadingElement.present.calls.mostRecent().returnValue;
    tick();
    await comicsService.listComicsWithErrors.calls.mostRecent().returnValue.toPromise();

    expect(loadingElement.dismiss).toHaveBeenCalled();
  }));

  describe('on loading error', () => {

    beforeEach(() => {
      comicsService.listComicsWithErrors.and.returnValue(throwError(''));
    });

    it('dismisses loading feedback', <any>fakeAsync(async () => {
      loadingElement.dismiss.calls.reset();
      component.ionViewWillEnter();

      expect(loadingController.create).toHaveBeenCalledWith({
        message: 'Loading queue...'
      });

      await loadingController.create.calls.mostRecent().returnValue;
      await loadingElement.present.calls.mostRecent().returnValue;
      tick();
      await new Promise(resolve =>
        comicsService.listComicsWithErrors.calls.mostRecent().returnValue.toPromise().catch(resolve));

      expect(loadingElement.dismiss).toHaveBeenCalled();
    }));
  });

  it('displays feedback while loading', <any>fakeAsync(async () => {
    loadingElement.dismiss.calls.reset();
    component.ionViewWillEnter();

    expect(loadingController.create).toHaveBeenCalledWith({
      message: 'Loading queue...'
    });

    await loadingController.create.calls.mostRecent().returnValue;
    await loadingElement.present.calls.mostRecent().returnValue;
    tick();
    await comicsService.listComicsWithErrors.calls.mostRecent().returnValue.toPromise();

    expect(loadingElement.dismiss).toHaveBeenCalled();
  }));


  describe('#fix', () => {

    it('auto-fixes the comic', () => {
      component.fix(ComicFixtures.comic, ComicFixtures.scannerIssueFixable);

      expect(comicsService.fixIssue).toHaveBeenCalledWith(ComicFixtures.comic, ComicFixtures.scannerIssueFixable);
    });

    describe('on success', () => {

      it('shows a success toast', async () => {
        component.fix(ComicFixtures.comic, ComicFixtures.scannerIssueFixable);

        await comicsService.fixIssue.calls.mostRecent().returnValue.toPromise();
        expect(toastController.create).toHaveBeenCalledWith({
          duration: 3000,
          message: 'Flattened comic archive "401.cbz".'
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicsService.fixIssue.and.returnValue(throwError(''));
      });

      it('shows an erro toast', async () => {
        component.fix(ComicFixtures.comic, ComicFixtures.scannerIssueFixable);

        await new Promise(resolve =>
          comicsService.fixIssue.calls.mostRecent().returnValue.toPromise().catch(resolve));
        expect(toastController.create).toHaveBeenCalledWith({
          duration: 4000,
          message: 'Error while flattening comic archive "401.cbz".'
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });
});
