import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ComicStorageServiceMocks } from '../../testing/comic-storage.service.mocks';
import { ComicStorageService } from '../comic-storage.service';

import { ReaderPageModule } from './reader.module';
import { ReaderPage } from './reader.page';

let component: ReaderPage;
let fixture: ComponentFixture<ReaderPage>;
let router;
let comicStorageService: jasmine.SpyObj<ComicStorageService>;

const clickRightSide = async (): Promise<void> => {
  fixture.debugElement.query(By.css('.pages-layer'))
    .triggerEventHandler('click', {
      clientX      : 700,
      currentTarget: { offsetWidth: 800 }
    });
  await fixture.whenStable();
  fixture.detectChanges();
};
const clickLeftSide = async (): Promise<void> => {
  fixture.debugElement.query(By.css('.pages-layer'))
    .triggerEventHandler('click', {
      clientX      : 100,
      currentTarget: { offsetWidth: 800 }
    });
  await fixture.whenStable();
  fixture.detectChanges();
};
const clickCenter = async (): Promise<void> => {
  fixture.debugElement.query(By.css('.pages-layer'))
    .triggerEventHandler('click', {
      clientX      : 500,
      currentTarget: { offsetWidth: 800 }
    });
  await fixture.whenStable();
  fixture.detectChanges();
};

describe('ReaderPage', () => {

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);
    comicStorageService = ComicStorageServiceMocks.comicStorageService;

    TestBed.configureTestingModule({
      imports: [
        ReaderPageModule,
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
        component.pagesLayer = {
          nativeElement: {
            parentElement: {
              clientWidth : 1000,
              clientHeight: 2000
            }
          }
        };
        component.ionViewDidEnter();

        await fixture.whenStable();
        fixture.detectChanges();
      });

      it('loads only the cover', () => {
        expect(fixture.debugElement.query(By.css('.pages-layer')).styles.transform)
          .toEqual('translateX(0vw)');
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
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(4);
          await clickRightSide();
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(4);
        });
      });
    });

    describe('in side by side mode', () => {

      beforeEach(async () => {
        component.pagesLayer = {
          nativeElement: {
            parentElement: {
              clientWidth : 2000,
              clientHeight: 1000
            }
          }
        };
        component.ionViewDidEnter();

        await fixture.whenStable();
        fixture.detectChanges();
      });

      it('loads only the cover', () => {
        expect(fixture.debugElement.query(By.css('.pages-layer')).styles.transform)
          .toEqual('translateX(0vw)');
      });

      describe('to the next page', () => {

        beforeEach(async () => {
          await clickRightSide();
        });

        it('updates the route', () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(2);
        });

        it('displays two pages', () => {
          expect(fixture.debugElement.query(By.css('.pages-layer')).styles.transform)
            .toEqual('translateX(-100vw)');
        });

        it('navigating back displays only the cover', async () => {
          await clickLeftSide();
          expect(fixture.debugElement.query(By.css('.pages-layer')).styles.transform)
            .toEqual('translateX(0vw)');
        });
      });

      describe('to the end of the comic', () => {

        beforeEach(async () => {
          await clickRightSide();
          await clickRightSide();
        });

        it('does not exceed the last page', async () => {
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(4);
          await clickRightSide();
          expect(router.navigate.calls.mostRecent().args[1].queryParams.page).toEqual(4);
        });
      });
    });
  });
});
