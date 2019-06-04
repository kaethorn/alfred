import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { defer } from 'rxjs';

import { TestModule } from '../../testing/test.module';
import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';
import { comic1 as comic } from '../../testing/comic.fixtures';

import { ComicsService } from '../comics.service';
import { ReaderPage } from './reader.page';

describe('ReaderPage', () => {

  let component: ReaderPage;
  let fixture: ComponentFixture<ReaderPage>;
  let router;
  const clickRightSide = () => {
    fixture.debugElement.query(By.css('.pages-layer'))
      .triggerEventHandler('click', {
        clientX      : 700,
        currentTarget: { offsetWidth: 800 }
      });
  };
  const clickLeftSide = () => {
    fixture.debugElement.query(By.css('.pages-layer'))
      .triggerEventHandler('click', {
        clientX      : 100,
        currentTarget: { offsetWidth: 800 }
      });
  };
  const clickCenter = () => {
    fixture.debugElement.query(By.css('.pages-layer'))
      .triggerEventHandler('click', {
        clientX      : 500,
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
      provide: ActivatedRoute,
      useValue: {
        snapshot: {
          params: { id: '493' },
          queryParams: { page: 0 }
        }
      }
    });
    testModule.providers.push({
      provide: Router, useValue: router
    });

    testModule.imports.pop();
    testModule.imports.push(
      RouterTestingModule.withRoutes([
        { path: 'read/:id', component: ReaderPage }
      ])
    );

    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    // Allow steering ComicsService response:
    comicsService.get.and.returnValue(defer(() => Promise.resolve(comic)));

    fixture = TestBed.createComponent(ReaderPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('navigation', () => {

    it('starts off on the first page', async () => {
      expect(component.comic.id).toBeUndefined();

      await fixture.whenStable();
      fixture.detectChanges();

      expect(component.comic.id).toBe('923');
      expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(0);
    });

    describe('when clicking the center', () => {

      it('shows the controls when clicking in the center', () => {
        expect(fixture.debugElement.query(By.css('.control-layer'))).toBeNull();
        clickCenter();
        fixture.detectChanges();
        expect(fixture.debugElement.query(By.css('.control-layer'))).not.toBeNull();
      });

      it('does not navigate', () => {
        router.navigate.calls.reset();
        clickCenter();
        expect(router.navigate).not.toHaveBeenCalled();
      });
    });

    describe('in single page mode', () => {

      beforeEach(async () => {
        await fixture.whenStable();
        fixture.detectChanges();

        component.pagesLayer = {
          nativeElement: {
            parentElement: {
              clientWidth : 1000,
              clientHeight: 2000
            }
          }
        };
      });

      describe('to the next page', () => {

        beforeEach(() => {
          clickRightSide();
        });

        it('sets the current page and updates the route', () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(1);
        });
      });

      describe('to the end of the comic', () => {

        beforeEach(() => {
          clickRightSide();
          clickRightSide();
          clickRightSide();
        });

        it('does not exceed the last page', () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(3);
          clickRightSide();
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(3);
        });
      });
    });

    describe('in side by side mode', () => {

      beforeEach(async () => {
        await fixture.whenStable();
        fixture.detectChanges();

        component.pagesLayer = {
          nativeElement: {
            parentElement: {
              clientWidth : 2000,
              clientHeight: 1000
            }
          }
        };
      });

      it('loads only the cover', () => {
        expect(component.imagePathRight).toBeNull();
      });

      describe('to the next page', () => {

        beforeEach(() => {
          clickRightSide();
        });

        it('sets the current page and updates the route', () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(1);
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
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(3);
          clickRightSide();
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(3);
        });
      });
    });
  });
});
