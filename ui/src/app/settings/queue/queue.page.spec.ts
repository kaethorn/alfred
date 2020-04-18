import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastController } from '@ionic/angular';
import { throwError } from 'rxjs';

import { ComicFixtures } from '../../../testing/comic.fixtures';
import { ComicsServiceMocks } from '../../../testing/comics.service.mocks';
import { ToastControllerMocks } from '../../../testing/toast.controller.mocks';
import { ComicsService } from '../../comics.service';
import { SettingsPageModule } from '../settings.module';

import { QueuePage } from './queue.page';

let component: QueuePage;
let fixture: ComponentFixture<QueuePage>;
let toastController: jasmine.SpyObj<ToastController>;
let toastElement: jasmine.SpyObj<HTMLIonToastElement>;
let comicsService: jasmine.SpyObj<ComicsService>;

describe('QueuePage', () => {

  beforeEach(() => {
    toastController = ToastControllerMocks.toastController;
    toastElement = ToastControllerMocks.toastElementSpy;
    comicsService = ComicsServiceMocks.comicsService;

    TestBed.configureTestingModule({
      imports: [
        SettingsPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ToastController, useValue: toastController
      }]
    });
    fixture = TestBed.createComponent(QueuePage);
    component = fixture.componentInstance;
    component.ionViewWillEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

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
