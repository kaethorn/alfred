import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastController } from '@ionic/angular';
import { throwError, of } from 'rxjs';

import { comic1 as comic } from '../../testing/comic.fixtures';
import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';
import { Comic } from '../comic';
import { ComicsService } from '../comics.service';

import { EditPageModule } from './edit.module';
import { EditPage } from './edit.page';

describe('EditPage', () => {
  let component: EditPage;
  let fixture: ComponentFixture<EditPage>;
  let toastSpy;
  let toastController;

  beforeEach(() => {
    toastSpy = jasmine.createSpyObj('HTMLIonToastElement', ['present']);
    toastController = jasmine.createSpyObj('ToastController', ['create']);
    toastController.create.and.returnValue(Promise.resolve(toastSpy));

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
        await toastController.create();
        expect(toastSpy.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicsService.update.and.returnValue(throwError(''));
      });

      afterEach(() => {
        // FIXME mock services should provide methods returning fresh mocks
        comicsService.update.and.returnValue(of(comic));
      });

      it('shows a error toast', async () => {
        component.onSubmit();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error saving comic.',
          duration: 4000
        });
        await toastController.create();
        expect(toastSpy.present).toHaveBeenCalled();
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
        await toastController.create();
        expect(toastSpy.present).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        comicsService.scrape.and.returnValue(throwError(''));
      });

      afterEach(() => {
        // FIXME mock services should provide methods returning fresh mocks
        comicsService.scrape.and.returnValue(of(comic));
      });

      it('shows a error toast', async () => {
        component.scrape();

        expect(toastController.create).toHaveBeenCalledWith({
          message: 'Error scraping comic.',
          duration: 4000
        });
        await toastController.create();
        expect(toastSpy.present).toHaveBeenCalled();
      });
    });
  });
});
