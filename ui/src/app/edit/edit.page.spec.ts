import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastController } from '@ionic/angular';
import { throwError } from 'rxjs';

import { ComicsServiceMocks } from '../../testing/comics.service.mocks';
import { ToastControllerMocks } from '../../testing/toast.controller.mocks';
import { Comic } from '../comic';
import { ComicsService } from '../comics.service';

import { EditPageModule } from './edit.module';
import { EditPage } from './edit.page';

let component: EditPage;
let fixture: ComponentFixture<EditPage>;
let toastController: jasmine.SpyObj<ToastController>;
let toastElement: jasmine.SpyObj<HTMLIonToastElement>;
let comicsService: jasmine.SpyObj<ComicsService>;

describe('EditPage', () => {

  beforeEach(() => {
    toastController = ToastControllerMocks.toastController;
    toastElement = ToastControllerMocks.toastElementSpy;
    comicsService = ComicsServiceMocks.comicsService;

    TestBed.configureTestingModule({
      imports: [
        EditPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ToastController, useValue: toastController
      }]
    });
    fixture = TestBed.createComponent(EditPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#onSubmit', () => {

    beforeEach(() => {
      component.ionViewDidEnter();
    });

    it('saves the comic', () => {
      component.comicForm.patchValue({ volume: '2000' });

      component.onSubmit();

      const passedComic: Comic = comicsService.update.calls.mostRecent().args[0];
      expect(passedComic.volume).toEqual('2000');
      expect(passedComic.series).toEqual('Batman');
    });

    describe('on success', () => {

      it('shows a success toast', async () => {
        component.onSubmit();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Comic saved.',
          duration: 3000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicsService.update.and.returnValue(throwError(''));
      });

      it('shows a error toast', async () => {
        component.onSubmit();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error saving comic.',
          duration: 4000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });

  describe('#scrape', () => {

    beforeEach(() => {
      component.ionViewDidEnter();
    });

    it('scrapes the comic', () => {
      component.scrape();

      expect(comicsService.scrape).toHaveBeenCalledWith(component.comic);
    });

    describe('on success', () => {

      it('shows a success toast', async () => {
        component.scrape();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Comic scraped.',
          duration: 3000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicsService.scrape.and.returnValue(throwError(''));
      });

      it('shows a error toast', async () => {
        component.scrape();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error scraping comic.',
          duration: 4000
        });
        await toastController.create.calls.mostRecent().returnValue;
        expect(toastElement.present).toHaveBeenCalled();
      });
    });
  });
});
