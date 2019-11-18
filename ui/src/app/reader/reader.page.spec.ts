import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { comic1 as comic } from '../../testing/comic.fixtures';

import { ComicStorageService } from '../comic-storage.service';
import { ReaderPage } from './reader.page';
import { ReaderPageModule } from './reader.module';

describe('ReaderPage', () => {

  let component: ReaderPage;
  let fixture: ComponentFixture<ReaderPage>;
  let router;
  let comicStorageService;

  const clickRightSide = async () => {
    fixture.debugElement.query(By.css('.pages-layer'))
      .triggerEventHandler('click', {
        clientX      : 700,
        currentTarget: { offsetWidth: 800 }
      });
    await fixture.whenStable();
    fixture.detectChanges();
  };
  const clickLeftSide = async () => {
    fixture.debugElement.query(By.css('.pages-layer'))
      .triggerEventHandler('click', {
        clientX      : 100,
        currentTarget: { offsetWidth: 800 }
      });
    await fixture.whenStable();
    fixture.detectChanges();
  };
  const clickCenter = async () => {
    fixture.debugElement.query(By.css('.pages-layer'))
      .triggerEventHandler('click', {
        clientX      : 500,
        currentTarget: { offsetWidth: 800 }
      });
    await fixture.whenStable();
    fixture.detectChanges();
  };

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);
    comicStorageService = jasmine
      .createSpyObj('ComicStorageService', ['get', 'readPage', 'storeSurrounding']);
    comicStorageService.get.and.returnValue(Promise.resolve(Object.assign({}, comic)));
    comicStorageService.readPage.and.returnValue(Promise.resolve('/api/read/923/0'));
    comicStorageService.storeSurrounding.and.returnValue(Promise.resolve({}));

    TestBed.configureTestingModule({
      imports: [
        ReaderPageModule,
        NoopAnimationsModule,
        RouterTestingModule.withRoutes([
          { path: 'read/:id', component: ReaderPage }
        ])
      ],
      providers: [{
        provide: ComicStorageService, useValue: comicStorageService
      }, {
        provide: ActivatedRoute, useValue: {
          snapshot: {
            params: { id: '493' },
            queryParams: { page: 0 }
          }
        }
      }, {
        provide: Router, useValue: router
      }]
    });

    fixture = TestBed.createComponent(ReaderPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
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

      it('shows the controls when clicking in the center', async () => {
        expect(fixture.debugElement.query(By.css('.control-layer'))).toBeNull();
        await clickCenter();
        expect(fixture.debugElement.query(By.css('.control-layer'))).not.toBeNull();
      });

      it('does not navigate', async () => {
        await fixture.whenStable();
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

        beforeEach(async () => {
          await clickRightSide();
        });

        it('sets the current page and updates the route', () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(1);
        });
      });

      describe('to the end of the comic', () => {

        beforeEach(async () => {
          await clickRightSide();
          await clickRightSide();
          await clickRightSide();
        });

        it('does not exceed the last page', async () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(3);
          await clickRightSide();
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
        expect(comicStorageService.readPage.calls.mostRecent().args[1]).toBe(0);
      });

      describe('to the next page', () => {

        beforeEach(async () => {
          comicStorageService.readPage.calls.reset();
          await clickRightSide();
        });

        it('sets the current page and updates the route', () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(1);
        });

        it('displays two pages', () => {
          expect(comicStorageService.readPage.calls.count()).toBe(2);
        });

        it('navigating back displays only the cover', async () => {
          comicStorageService.readPage.calls.reset();
          await clickLeftSide();
          expect(comicStorageService.readPage.calls.count()).toBe(1);
        });
      });

      describe('to the end of the comic', () => {

        beforeEach(async () => {
          await clickRightSide();
          await clickRightSide();
        });

        it('does not exceed the last page', async () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(3);
          await clickRightSide();
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(3);
        });
      });
    });
  });
});
