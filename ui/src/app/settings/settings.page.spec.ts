import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastController } from '@ionic/angular';
import { throwError } from 'rxjs';

import { ComicsServiceMocks } from '../../testing/comics.service.mocks';
import { SettingFixtures } from '../../testing/setting.fixtures';
import { SettingsServiceMocks } from '../../testing/settings.service.mocks';
import { StatsServiceMocks } from '../../testing/stats.service.mocks';
import { ToastControllerMocks } from '../../testing/toast.controller.mocks';
import { ComicsService } from '../comics.service';
import { SettingsService } from '../settings.service';
import { StatsService } from '../stats.service';
import { UserSettingsService } from '../user-settings.service';

import { SettingsPageModule } from './settings.module';
import { SettingsPage } from './settings.page';

let component: SettingsPage;
let fixture: ComponentFixture<SettingsPage>;
let comicsService: jasmine.SpyObj<ComicsService>;
let settingsService: jasmine.SpyObj<SettingsService>;
let statsService: jasmine.SpyObj<StatsService>;
let toastController: jasmine.SpyObj<ToastController>;
let toastElement: jasmine.SpyObj<HTMLIonToastElement>;

describe('SettingsPage', () => {

  beforeEach(() => {
    comicsService = ComicsServiceMocks.comicsService;
    settingsService = SettingsServiceMocks.settingsService;
    statsService = StatsServiceMocks.statsService;
    toastController = ToastControllerMocks.toastController;
    toastElement = ToastControllerMocks.toastElementSpy;

    TestBed.configureTestingModule({
      imports: [
        SettingsPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: SettingsService, useValue: settingsService
      }, {
        provide: ComicsService, useValue: comicsService
      }, {
        provide: StatsService, useValue: statsService
      }, {
        provide: ToastController, useValue: toastController
      },
      UserSettingsService
      ]
    });
    fixture = TestBed.createComponent(SettingsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#onSubmit', () => {

    beforeEach(async () => {
      component.ionViewWillEnter();
      await settingsService.list.calls.mostRecent().returnValue.toPromise();
    });

    it('updates settings', () => {
      component.onSubmit();

      expect(settingsService.update).toHaveBeenCalledTimes(1);
      expect(settingsService.update).toHaveBeenCalledWith(SettingFixtures.setting);
    });

    describe('on success', () => {

      it('shows a success toast', async () => {
        component.onSubmit();

        await settingsService.update.calls.mostRecent().returnValue.toPromise();
        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Settings saved.',
          duration: 3000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        settingsService.update.and.returnValue(throwError({
          status: 500,
          statusText: 'Server error'
        }));
      });

      it('shows an error toast', async () => {
        component.onSubmit();

        await new Promise(resolve =>
          settingsService.update.calls.mostRecent().returnValue.toPromise().catch(resolve));
        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error saving settings (500: Server error).',
          duration: 4000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });

  describe('#saveUserSettings', () => {

    afterEach(() => {
      localStorage.clear();
    });

    it('saves user settings', () => {
      component.saveUserSettings();
      expect(JSON.parse(localStorage.getItem('userSettings'))).toEqual({});

      component.userSettings.darkMode = true;
      component.saveUserSettings();

      expect(JSON.parse(localStorage.getItem('userSettings'))).toEqual({
        darkMode: true
      });
    });
  });
});
