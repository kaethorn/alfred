import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastController } from '@ionic/angular';
import { throwError } from 'rxjs';

import { comic1 as comic, scannerIssueFixable } from '../../../testing/comic.fixtures';
import { ComicsServiceMocks } from '../../../testing/comics.service.mocks';
import { ToastControllerMocks } from '../../../testing/toast.controller.mocks';
import { ComicsService } from '../../comics.service';
import { SettingsPageModule } from '../settings.module';

import { QueueComponent } from './queue.component';

let component: QueueComponent;
let fixture: ComponentFixture<QueueComponent>;
let toastController: jasmine.SpyObj<ToastController>;
let toastElement: jasmine.SpyObj<HTMLIonToastElement>;
let comicsService: jasmine.SpyObj<ComicsService>;

describe('QueueComponent', () => {

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
    fixture = TestBed.createComponent(QueueComponent);
    component = fixture.componentInstance;
    component.ionViewWillEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#fix', () => {

    it('auto-fixes the comic', () => {
      component.fix(comic, scannerIssueFixable);

      expect(comicsService.fixIssue).toHaveBeenCalledWith(comic, scannerIssueFixable);
    });

    describe('on success', () => {

      it('shows a success toast', async () => {
        component.fix(comic, scannerIssueFixable);

        await comicsService.fixIssue.calls.mostRecent().returnValue.toPromise();
        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Flattened comic archive "401.cbz".',
          duration: 3000
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
        component.fix(comic, scannerIssueFixable);

        await new Promise(resolve =>
          comicsService.fixIssue.calls.mostRecent().returnValue.toPromise().catch(resolve));
        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error while flattening comic archive "401.cbz".',
          duration: 4000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });
});
