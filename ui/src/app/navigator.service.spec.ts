import { TestBed } from '@angular/core/testing';

import { NavigatorService, AdjacentComic } from './navigator.service';

describe('NavigatorService', () => {
  let service: NavigatorService;

  beforeEach(async () => {
    service = TestBed.get(NavigatorService);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  describe('#go', () => {

    let result;

    describe('navigating backwards', () => {

      describe('in side by side mode', () => {

        describe('when in the middle of an issue', () => {

          beforeEach(() => {
            service.set(10, 4, true);
            result = service.go(-1);
          });

          it('suggests to stay on the current issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.same);
          });

          it('keeps side by side mode', () => {
            expect(result.sideBySide).toBe(true);
          });

          it('navigates to the previous pair of pages', () => {
            expect(NavigatorService.page).toBe(1);
          });
        });

        describe('when at the start of an issue', () => {

          beforeEach(() => {
            service.set(10, 0, true);
            result = service.go(-1);
          });

          it('suggests to open the previous issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.previous);
          });

          it('exits side by side mode', () => {
            expect(result.sideBySide).toBe(false);
          });

          it('navigates stays on the current page', () => {
            expect(NavigatorService.page).toBe(0);
          });
        });
      });

      describe('in single page mode', () => {

        describe('when in the middle of an issue', () => {

          beforeEach(() => {
            service.set(10, 4, false);
            result = service.go(-1);
          });

          it('suggests to stay on the current issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.same);
          });

          it('keeps single pagemode', () => {
            expect(result.sideBySide).toBe(false);
          });

          it('navigates to the previous page', () => {
            expect(NavigatorService.page).toBe(3);
          });
        });

        describe('when at the start of an issue', () => {

          beforeEach(() => {
            service.set(10, 0, false);
            result = service.go(-1);
          });

          it('suggests to open the previous issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.previous);
          });

          it('keeps single page mode', () => {
            expect(result.sideBySide).toBe(false);
          });

          it('navigates stays on the current page', () => {
            expect(NavigatorService.page).toBe(0);
          });
        });
      });
    });

    describe('navigating forward', () => {

      describe('in side by side mode', () => {

        describe('when in the middle of an issue', () => {

          beforeEach(() => {
            service.set(10, 4, true);
            result = service.go(1);
          });

          it('suggests to stay on the current issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.same);
          });

          it('keeps side by side mode', () => {
            expect(result.sideBySide).toBe(true);
          });

          it('navigates to the next pair of pages', () => {
            expect(NavigatorService.page).toBe(5);
          });
        });

        describe('when at the end of an issue', () => {

          beforeEach(() => {
            service.set(10, 9, true);
            result = service.go(1);
          });

          it('suggests to open the next issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.next);
          });

          it('exits side by side mode', () => {
            expect(result.sideBySide).toBe(false);
          });

          it('navigates stays on the current page', () => {
            expect(NavigatorService.page).toBe(9);
          });
        });
      });

      describe('in single page mode', () => {

        describe('when in the middle of an issue', () => {

          beforeEach(() => {
            service.set(10, 4, false);
            result = service.go(1);
          });

          it('suggests to stay on the current issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.same);
          });

          it('keeps single pagemode', () => {
            expect(result.sideBySide).toBe(false);
          });

          it('navigates to the previous page', () => {
            expect(NavigatorService.page).toBe(5);
          });
        });

        describe('when at the end of an issue', () => {

          beforeEach(() => {
            service.set(10, 9, false);
            result = service.go(1);
          });

          it('suggests to open the next issue', () => {
            expect(result.adjacent).toBe(AdjacentComic.next);
          });

          it('keeps single page mode', () => {
            expect(result.sideBySide).toBe(false);
          });

          it('navigates stays on the current page', () => {
            expect(NavigatorService.page).toBe(9);
          });
        });
      });
    });

    describe('resuming', () => {

      describe('in side by side mode', () => {

        describe('when on the first page', () => {

          beforeEach(() => {
            service.set(10, 0, true);
            service.go(0);
          });

          it('continues on the first page', () => {
            expect(NavigatorService.page).toBe(0);
          });
        });

        describe('when on a following page', () => {

          beforeEach(() => {
            service.set(10, 1, true);
            service.go(0);
          });

          it('continues on odd pages', () => {
            expect(NavigatorService.page).toBe(1);

            service.set(10, 2, true);
            service.go(0);
            expect(NavigatorService.page).toBe(1);

            service.set(10, 3, true);
            service.go(0);
            expect(NavigatorService.page).toBe(3);

            service.set(10, 4, true);
            service.go(0);
            expect(NavigatorService.page).toBe(3);
          });
        });
      });

      describe('in single page mode', () => {

        describe('when on the first page', () => {

          beforeEach(() => {
            service.set(10, 0, false);
            service.go(0);
          });

          it('continues on the first page', () => {
            expect(NavigatorService.page).toBe(0);
          });
        });

        describe('when on the second page', () => {

          beforeEach(() => {
            service.set(10, 1, false);
            service.go(0);
          });

          it('continues on the second page', () => {
            expect(NavigatorService.page).toBe(1);
          });
        });
      });
    });
  });
});
