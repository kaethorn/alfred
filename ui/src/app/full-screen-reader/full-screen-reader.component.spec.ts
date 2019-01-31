import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Location } from '@angular/common';
import { Router, Routes, ActivatedRoute } from '@angular/router';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { TestModule } from './../../testing/test.module';
import { ComicsServiceMocks as comicsService } from './../../testing/comics.service.mocks';

import { ComicsService } from '../comics.service';
import { Comic } from '../comic';
import { FullScreenReaderComponent } from './full-screen-reader.component';

describe('FullScreenReaderComponent', () => {
  let component: FullScreenReaderComponent;
  let fixture: ComponentFixture<FullScreenReaderComponent>;
  let router;
  const clickRightSide = () => {
    fixture.debugElement.query(By.css('.layer'))
      .triggerEventHandler('click', {
        clientX      : 500,
        currentTarget: { offsetWidth: 800 }
      });
  };
  const clickLeftSide = () => {
    fixture.debugElement.query(By.css('.layer'))
      .triggerEventHandler('click', {
        clientX      : 300,
        currentTarget: { offsetWidth: 800 }
      });
  };

  beforeEach(async(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);

    const testModule: any = TestModule();
    testModule.providers.push({
      provide: ComicsService, useValue: comicsService
    });
    testModule.providers.push({
      provide: ActivatedRoute, useValue: { snapshot: { params: {
        id: '493',
        page: '0'
      }}}
    });
    testModule.providers.push({
      provide: Router, useValue: router
    });

    testModule.imports.pop();
    testModule.imports.push(
      RouterTestingModule.withRoutes([
        { path: 'read-full-screen/:id/:page', component: FullScreenReaderComponent }
      ])
    );

    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FullScreenReaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('navigation', () => {

    it('starts off on the first page', () => {
      expect(component.currentPage).toBe(0);
      expect(component.comic.id).toBe(923);
      expect(router.navigate).toHaveBeenCalledWith(['/read-full-screen/', 923, 0]);
    });

    describe('in single page mode', () => {

      beforeEach(() => {
        component.sideBySide = false;
      });

      describe('to the next page', () => {

        beforeEach(() => {
          clickRightSide();
        });

        it('sets the current page and updates the route', () => {
          expect(component.currentPage).toBe(1);
          expect(router.navigate).toHaveBeenCalledWith(['/read-full-screen/', 923, 1]);
        });
      });

      describe('to the end of the comic', () => {

        beforeEach(() => {
          clickRightSide();
          clickRightSide();
          clickRightSide();
          clickRightSide();
        });

        it('does not exceed the last page', () => {
          expect(component.currentPage).toBe(4);
          expect(router.navigate).toHaveBeenCalledWith(['/read-full-screen/', 923, 4]);
          clickRightSide();
          expect(component.currentPage).toBe(4);
          expect(router.navigate).toHaveBeenCalledWith(['/read-full-screen/', 923, 4]);
        });
      });
    });

    describe('in side by side mode', () => {

      beforeEach(() => {
        component.sideBySide = true;
      });

      it('loads only the cover', () => {
        expect(component.imagePathRight).toBeNull();
      });

      describe('to the next page', () => {

        beforeEach(() => {
          clickRightSide();
        });

        it('sets the current page and updates the route', () => {
          expect(component.currentPage).toBe(2);
          expect(router.navigate).toHaveBeenCalledWith(['/read-full-screen/', 923, 2]);
        });

        it('displays two pages', () => {
          expect(component.imagePathLeft).not.toBeNull();
          expect(component.imagePathRight).not.toBeNull();
        });

        it('navigating back displays only the cover', () => {
          clickLeftSide();
          expect(component.imagePathLeft).not.toBeNull();
          expect(component.imagePathRight).toBeNull();
        });
      });

      describe('to the end of the comic', () => {

        beforeEach(() => {
          clickRightSide();
          clickRightSide();
        });

        it('does not exceed the last page', () => {
          expect(component.currentPage).toBe(4);
          expect(router.navigate).toHaveBeenCalledWith(['/read-full-screen/', 923, 4]);
          clickRightSide();
          expect(component.currentPage).toBe(4);
          expect(router.navigate).toHaveBeenCalledWith(['/read-full-screen/', 923, 4]);
        });
      });
    });
  });
});
